package business.control

import business.model.Procedimento
import business.model.Simbolo
import java.util.LinkedList

class ControllerAnalisadorSintatico
{
    var i: Int = 0  //indice
    lateinit var tab: LinkedList<Simbolo>
    lateinit var listaIdentificadores: LinkedList<String>
    lateinit var listaProcedimentos: LinkedList<Procedimento>

    private val PROGRAM = "program|PROGRAM"
    private val PROCEDURE = "procedure|PROCEDURE"
    private val BEGIN = "begin|BEGIN"
    private val END = "end|END"
    private val VAR = "var|VAR"
    private val DO = "do|DO"
    private val WHILE = "while|WHILE"
    private val IF = "if|IF"
    private val THEN = "then|THEN"
    private val ELSE = "else|ELSE"
    private val TRUE = "true|TRUE"
    private val FALSE = "false|FALSE"
    private val NOT = "not|NOT"

    //exclusivamente um tipo de uma variável
    private val TIPO = "integer|INTEGER|real|REAL|boolean|BOOLEAN|char|CHAR"


    fun analisar(tabela: LinkedList<Simbolo>)
    {
        copiarTabelaSemComentarios(tabela)
        listaIdentificadores = LinkedList()
        listaProcedimentos = LinkedList()
        programId()
    }

    /**
     * Estou incrementando o indice na mesma linha que eu uso o get() no if
     * o incremento usado é o ++i, que deve ser incrementado antes de acessar a variável i
     */
    private fun programId()
    {
        if(program())
        {
            i++
            if (identificador())
            {
                listaIdentificadores.add(tab.get(i).token)
                i++
                if (tab.get(i).token.equals(";"))
                {
                    i++
                    if(this.declaracoesVariaveis())
                    {
                        print("")
                        this.declaracoesDeSubprogramas()
                        //this.comandoComposto()
                    }
                }
                else print("ERRO: é esperado um ';' na linha ${tab.get(i).linha}")
            }
            else print("ERRO: é esperado um 'IDENTIFICADOR' para o program, na linha ${tab.get(i).linha} ")
        }
        else print("ERRO: é esperado a 'PALAVRA_RESERVADA'  program no início, na linha ${tab.get(i).linha} ")
    }

    private fun declaracoesVariaveis() : Boolean {
        if (var_())
            return listaDeclaracoesVariaveis()
        /*else{não há lista de declarações de variaveis. CONTINUE...}*/
        return true
    }

    private fun listaDeclaracoesVariaveis() : Boolean
    {
        if(listaDeIdentificadores())
        {
            i++
            if (tipo())
            {
                i++
                if(tab.get(i).token.equals(";"))
                {
                    return listaDeclaracoesVariaveis()
                }
                else
                {
                    print("ERRO: é esperado um ';' após o 'TIPO' das variáveis")
                    return false
                }
            }
            else
            {
                print("ERRO: é esperado um 'TIPO' após declarar as variáveis")
                return false
            }
        }
        else if(!this.listaIdentificadores.isNullOrEmpty())
        {
            if(begin() || procedure())
                return true
            else
            {
                print("ERRO: Após as declarações de variáveis é esperando um escopo de um método")
                return false
            }
        }
        else
        {   //como encontrou um VAR, entao tem que ter uma lista de variaveis.
            print("ERRO: é esperado pelo menos 1 'IDENTIFICADOR' ao iniciar o escopo da lista de variáveis.")
            return false   /*houve algum erro na lista de identificadores*/
        }
    }

    private fun listaDeIdentificadores() : Boolean
    {
        //verificar se é um 'IDENTIFICADOR'
        i++
        if (identificador())
        {
            //verificar se já existe um identificador declarado com o mesmo nome
            if( existeNaListaDeIdentificadores(tab.get(i).token) )
            {
                print("ERRO: ['IDENTIFICADOR': ${tab.get(i).token}, linha ${tab.get(i).linha}] já foi declarado")
                return false
            }
            else
            {
                //entao adiciona o novo 'IDENTIFICADOR' à lista e incrementa o índice.
                listaIdentificadores.add(tab.get(i).token)
                i++
                if (tab.get(i).token.equals(","))
                    return listaDeIdentificadores() //|id
                else if (tab.get(i).token.equals(":"))
                    return true
                else
                {
                    print("ERRO: é esperado ':' ou ',' após um 'IDENTIFICADOR' ainda não declarado.")
                    return false
                }
            }
        }
        else return false   /*se já declarou as variáveis, pode ser um begin|procedure*/
    }


    private fun declaracoesDeSubprogramas(): Boolean
    {
        if (procedure())
        {
            if(procedureId())
            {
                /*
                declaracoes de var
                declaracoes de subprog(recursao)
                comando composto
                 */
                return true//pra tirar o erro por enquanto
            }
            else
            {
                return false//pra tirar o erro por enquanto
            }
        }
        else if(begin())
        {
            return true
        }
        else
        {
            println("ERRO: é esperado um 'BEGIN' ou 'PROCEDURE'")
            return false
        }

    }

    private fun procedureId() : Boolean
    {
        i++
        if (identificador())
        {
            val nomeProcedimento = tab.get(i).token //procedimento só será salvo se passar nos testes
            var novoProcedimento = Procedimento(nomeProcedimento)
            i++
            if (tab.get(i).token.equals("("))
            {
                i++
                if (var_()) //se for um 'var' pula, não interessa para o sintático no momento
                    i++
                if (argumentos(novoProcedimento))
                {
                    if (existeNaListaDeProcedimentos(novoProcedimento))
                    {
                        println("ERRO: O procedimento já existe com mesmo nome e argumentos")
                        return false
                    }
                    else
                    {
                        listaProcedimentos.add(novoProcedimento)
                        return true
                    }
                }
                else    //houve um erro na lista de argumentos
                {
                    println("ERRO: problema na lista de argumentos")
                    return false
                }
            }
            else if (tab.get(i).token.equals(";"))
            {
                return true
            }
        }
        //um simples false. pode ser um begin. nada de erro ainda.
        return false
    }

    private fun argumentos(procedimento: Procedimento) : Boolean
    {
        if (identificador())
        {
            i++
            if (tab.get(i).token.equals(":"))
            {
                i++
                if (tipo())
                {
                    procedimento.argumentos.add(tab.get(i).token)//adiciona o tipo do argumento
                    i++
                    if( tab.get(i).token.equals(")") )
                    {
                        i++
                        if (tab.get(i).token.equals(";"))
                            return true
                        else
                        {
                            println("ERRO: É esperado um ';' para completar o procedimento")
                            return false
                        }
                    }
                    //recursivamento valida os próximos argumentos
                    else if (tab.get(i).token.equals(","))
                    {
                        i++
                        return argumentos(procedimento)
                    }
                    else
                    {
                        println("ERRO: É esperado um ')' para completar os argumentos ou ',' para novos argumentos.")
                        return false
                    }
                }
                else
                {
                    println("ERRO: É esperado o tipo do argumento na linha")
                    return false
                }
            }
            else
            {
                println("ERRO: É esperado ':' após o nome do argumento na linha")
                return false
            }
        }
        else
        {
            println("ERRO: Após abrir lista de argumentos para um programa")
            return false
        }
    }


    /*******************************************************************************************************************
     *                                   Atalhos para testar Regex                                                     *
     ******************************************************************************************************************/
    private fun program() : Boolean = tab.get(i).token.matches(PROGRAM.toRegex())

    private fun begin() : Boolean = tab.get(i).token.matches(BEGIN.toRegex())

    private fun procedure() : Boolean = tab.get(i).token.matches(PROCEDURE.toRegex())

    private fun var_() : Boolean = tab.get(i).token.matches(VAR.toRegex())

    private fun identificador() : Boolean = tab.get(i).classificacao.equals("IDENTIFICADOR")

    private fun tipo() : Boolean = tab.get(i).token.matches(TIPO.toRegex())


    /*******************************************************************************************************************
     *                           Métodos auxiliares para o analisador sintático                                        *
     ******************************************************************************************************************/
    private fun existeNaListaDeIdentificadores (identificador: String): Boolean
    {
        for (id in this.listaIdentificadores)
            if(id == identificador)
                return true

        return false
    }

    private fun existeNaListaDeProcedimentos (procedimento: Procedimento): Boolean
    {
        //identificador0 é o nome do programa.
        if (procedimento.nome != listaIdentificadores.get(0))
        {
            if (!this.listaProcedimentos.isNullOrEmpty()) {
                for (proc in this.listaProcedimentos) {
                    if (proc.nome.toLowerCase() == procedimento.nome.toLowerCase()) {//no refinamento vou verificar nome|NOME
                        if (proc.argumentos.size == procedimento.argumentos.size) {
                            for (i in 0..proc.argumentos.size) {
                                if (proc.argumentos.get(i).toLowerCase() == procedimento.argumentos.get(i).toLowerCase())
                                else
                                    return false
                            }
                        }
                    }
                }
            }
            else
                return false
        }
        //se não houve diferença, então retorna true
        return true
    }

    private fun copiarTabelaSemComentarios(tabela: LinkedList<Simbolo>)
    {
        this.tab = LinkedList()
        for(t in tabela)
            if(t.classificacao != "COMENTARIO")
                this.tab.add(t)
    }

}


package business.control

import business.model.Simbolo
import java.util.LinkedList

class ControllerAnalisadorSintatico
{
    var i: Int = 0  //indice
    lateinit var tab: LinkedList<Simbolo>
    lateinit var listaIdentificadores: LinkedList<String>

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

            return true
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

    private fun copiarTabelaSemComentarios(tabela: LinkedList<Simbolo>)
    {
        this.tab = LinkedList()
        for(t in tabela)
            if(t.classificacao != "COMENTARIO")
                this.tab.add(t)
    }

}


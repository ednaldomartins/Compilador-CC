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
        tab = tabela    //referencia de tabela
        listaIdentificadores = LinkedList()
        programId()
    }

    /**
     * Estou incrementando o indice na mesma linha que eu uso o get() no if
     * o incremento usado é o ++i, que deve ser incrementado antes de acessar a variável i
     */
    private fun programId()
    {
        if(tab.get(i).token.matches(PROGRAM.toRegex()))
        {
            if (tab.get(++i).classificacao.equals("IDENTIFICADOR"))
            {
                listaIdentificadores.add(tab.get(i).token)
                if (tab.get(++i).token.equals(";"))
                {
                    this.declaracoesVariaveis()
                    //this.declaracoesSubProgramas()
                    //this.comandoComposto()
                }
                else print("ERRO: é esperado um ';' na linha ${tab.get(i).linha}")
            }
            else print("ERRO: é esperado um 'IDENTIFICADOR' para o program, na linha ${tab.get(i).linha} ")
        }
        else print("ERRO: é esperado a 'PALAVRA_RESERVADA'  program no início, na linha ${tab.get(i).linha} ")
    }

    private fun declaracoesVariaveis()
    {
        do {i++} while(tab.get(i).classificacao.equals("COMENTARIO"))
        if(tab.get(i).token.matches(VAR.toRegex()))
            listaDeclaracoesVariaveis()
        /*else{não há lista de declarações de variaveis. CONTINUE...}*/
    }

    private fun listaDeclaracoesVariaveis() : Boolean
    {
        //estah em 'var'|','|':', incrementa e verifica se o proximo eh um comentario, se for, continuar incrementando.
        do {i++} while(tab.get(i).classificacao.equals("COMENTARIO"))
        //verificar se é um 'IDENTIFICADOR'
        if (tab.get(i).classificacao.equals("IDENTIFICADOR"))
        {
            //verificar se já existe um identificador declarado com o mesmo nome
            if( existeNaListaDeIdentificadores(tab.get(i).token) )
            {
                print("ERRO: 'IDENTIFICADOR' já foi declarado")
                return false
            }
            else
            {
                //entao adiciona o novo 'IDENTIFICADOR' à lista e incrementa o índice.
                listaIdentificadores.add(tab.get(i++).token)
                if (tab.get(i).token.equals(","))
                    return listaDeclaracoesVariaveis()
                else if (tab.get(i).token.equals(":"))
                {   //depois do ':' tem um TIPO?
                    if (tipo(tab.get(++i).token))
                    {
                        if(tab.get(++i).token.equals(";"))
                        {
                            listaDeclaracoesVariaveis()
                            return true
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
                else
                {
                    print("ERRO: é esperado ':' ou ',' após um 'IDENTIFICADOR' ainda não declarado.")
                    return false
                }
            }
        }

        if(this.listaIdentificadores!=null)
            return true

        /*else*/
        print("ERRO: é esperado pelo menos 1 'IDENTIFICADOR' ao iniciar o escopo da lista de variáveis.")
        return false
    }

    private fun tipo(tipo: String) : Boolean = tipo.matches(TIPO.toRegex())

    private fun existeNaListaDeIdentificadores (identificador: String): Boolean
    {
        for (id in this.listaIdentificadores)
            if(id == identificador)
                return true

        return false
    }


}


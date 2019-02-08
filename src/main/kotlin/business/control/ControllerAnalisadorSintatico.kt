package business.control

import business.model.Lexico
import business.model.Simbolo
import java.util.LinkedList

class ControllerAnalisadorSintatico
{
    var i: Int = 0  //indice
    lateinit var tab: LinkedList<Simbolo>

    private val VAR = "integer|INTEGER|real|REAL|boolean|BOOLEAN|char|CHAR"
    private val PROGRAM = "PROGRAM|program"

    fun analisar(tabela: LinkedList<Simbolo>)
    {
        tab = tabela    //referencia de tabela
        programId()
    }

    /**
     * Estou incrementando o indice na mesma linha que eu uso o get() no if
     * o incremento usado é o i++, que deve ser incrementado após o uso da variável i
     * caso encontre algum erro no código, deve-se decrementar o indice para pegar a linha do simbolo que esta errado
     */
    private fun programId()
    {
        if(tab.get(i++).token.matches(PROGRAM.toRegex()))
            if(tab.get(i++).classificacao.equals("IDENTIFICADOR"))
                if (tab.get(i++).token.equals(";"))
                    this.declaracoesVariaveis()
                else print("ERRO: é esperado um ';' na linha ${tab.get(--i).linha}")
            else print("ERRO: é esperado um 'IDENTIFICADOR' para o program, na linha ${tab.get(--i).linha} ")
        else print("ERRO: é esperado a 'PALAVRA_RESERVADA'  program no início, na linha ${tab.get(--i).linha} ")
    }

    private fun declaracoesVariaveis()
    {
        while(tab.get(i).classificacao.equals("COMENTARIO")) {i++}
        if(tab.get(i).token.equals("var"))
        {
            i++
            listaDeclaracoesVariaveis()
        }
        else{/*não há lista de declarações de variaveis. CONTINUE...*/}
    }

    private fun listaDeclaracoesVariaveis()
    {
        while(tab.get(i).classificacao.equals("COMENTARIO")) {i++}
        if (tab.get(i).classificacao.equals("IDENTIFICADOR"))
        {
            //if -> listaDeIdentificadores()  verificar se identificador ja foi declarado para continuar
            i++
            if (tab.get(i).token.equals(","))
            {
                i++
                listaDeclaracoesVariaveis()
            }
            else if (tab.get(i).token.equals(":"))
            {
                i++
                if (tab.get(i).token.matches(VAR.toRegex()))
                {
                    tipo()
                }
            }
            else print("ERRO: é esperado uma 'ATRIBUIÇÃO' ou ',' após um 'IDENTIFICADOR'.")
        }
        else print("ERRO: é esperado pelo menos 1 'IDENTIFICADOR' no escopo da lista de variáveis.")
    }

    private fun tipo()
    {

    }

}


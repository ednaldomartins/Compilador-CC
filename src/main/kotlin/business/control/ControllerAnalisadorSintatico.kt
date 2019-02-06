package business.control

import business.model.Simbolo
import java.util.LinkedList

class ControllerAnalisadorSintatico
{
    var i: Int = 0  //indice
    lateinit var tab: LinkedList<Simbolo>

    fun analisar(tabela: LinkedList<Simbolo>)
    {
        tab = tabela    //referencia de tabela
        programId()
    }

    /**
     * Estou incrementando o indice na mesma linha que eu uso o get()
     * o incremento usado é o i++, que deve ser incrementado após o uso da variável i
     * caso encontre algum erro no código, deve-se decrementar o indice para pegar a linha do simbolo que esta errado
     */
    private fun programId()
    {
        if(tab.get(i++).token == "program")
            if(tab.get(i++).classificacao == "IDENTIFICADOR")
                if (tab.get(i++).token == ";")
                    this.declaracoesVariaveis()
                else print("ERRO: é esperado um ';' na linha ${tab.get(--i).linha}")
            else print("ERRO: é esperado um 'IDENTIFICADOR' para o program, na linha ${tab.get(--i).linha} ")
        else print("ERRO: é esperado a 'PALAVRA_RESERVADA'  program no início, na linha ${tab.get(--i).linha} ")
    }

    private fun declaracoesVariaveis()
    {


    }

}
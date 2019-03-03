package business.model

import java.util.LinkedList

class AnalisadorSemantico
{
    companion object
    {
        /**
         * listaIdentificadores guarda as variáveis com seus nomes e tipos, e também o nome dos precedimentos, considerando
         * o tipo como procedimento, como também o nome do programa considerando program o seu tipo.
         *
         * profundidadeEscopo é incrementado sempre que o token no código é o 'BEGIN', e decrementado quando o token no
         * código é o 'END', logo quando (profundidadeEscopo > 0) então estamos em uma parte do código onde variáveis ou
         * procedimentos não podem ser declarados.
         */
        var listaIdentificadores: LinkedList<Identificador> = LinkedList()
        var profundidadeEscopo:Int = 0

        /**
         *
         */
        @JvmStatic fun analisaIdentificador():Boolean = if (profundidadeEscopo > 0) buscaIdentificador() else empilhaIdentificador()

        private fun buscaIdentificador(): Boolean
        {
            return true
        }

        private fun empilhaIdentificador(): Boolean
        {
            return true
        }

        private fun desempilhaIdentificador(): Boolean
        {
            return true
        }


        /**
         *
         */
        @JvmStatic fun analisaProcedimento():Boolean = if (profundidadeEscopo > 0) buscaProcedimento() else empilharProcedimento()

        private fun buscaProcedimento(): Boolean
        {
            return true
        }

        private fun empilharProcedimento(): Boolean
        {
            return true
        }

        private fun desempilhaProcedimento(): Boolean
        {
            return true
        }

    }

}

fun main()
{

}
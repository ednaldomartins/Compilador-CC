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
         * procedimentos não podem ser declarado, mas podem ser usado.
         */
        private var listaIdentificadores: LinkedList<Identificador> = LinkedList()
        private var profundidadeEscopo:Int = 0

        /**
         *
         */
        @JvmStatic fun analisaIdentificador(identificador: Identificador):Boolean = if (profundidadeEscopo > 0) buscaIdentificador(identificador) else empilhaVariavel(identificador)

        /**
         *
         */
        @JvmStatic fun analisaProcedimento(identificador: Identificador):Boolean = if (profundidadeEscopo > 0) buscaIdentificador(identificador) else empilhaProcedimento(identificador)


        private fun buscaIdentificador(identificador: Identificador): Boolean
        {
            //A leitura da pilha começa de cima para baixo, logo os últimos identificadores declarados são os primeiros.
            var i:Int = 0
            while (listaIdentificadores.get(i).nome != "#")
            {
                if (listaIdentificadores.get(i).nome.equals(identificador.nome) && listaIdentificadores.get(i).tipo.equals((identificador.tipo)))
                    return true
                i++
            }
            return false
        }

        private fun empilhaVariavel(identificador: Identificador): Boolean
        {
            //se a variavel já foi declarada dentro do mesmo escopo, então retorna erro
            if (buscaIdentificador(identificador))
            {
                println("ERRO SEMÂNTICO: Erro inesperado. Problema no tipo do procedimento ou programa. Como isso chegou aqui???!!!")
                return false
            }
            listaIdentificadores.push(identificador)
            return true
        }

        private fun empilhaProcedimento(identificador: Identificador): Boolean
        {
            if (identificador.tipo.equals("procedure"))
            {
                //se já existe procedimento ou programa com o mesmo nome então não pode adicionar a pilha. retorna erro
                if (buscaIdentificador(identificador))
                {
                    println("ERRO SEMÂNTICO: Esse nome já foi usado como identificador de um outro procedimento nesse mesmo escopo")
                    return false
                }
                //verificar se o nome do procedimento é igual ao nome do programa
                if (identificador.nome.equals(listaIdentificadores.get(listaIdentificadores.size-2).nome))
                {
                    println("ERRO SEMÂNTICO: Esse nome já foi usado como identificador no nome do programa")
                    return false
                }
                listaIdentificadores.push(identificador)
                listaIdentificadores.push(Identificador("#", "#"))
                return true
            }
            else if (identificador.tipo.equals("program"))
            {
                listaIdentificadores.push(Identificador("#", "#"))
                listaIdentificadores.push(identificador)
                return true
            }
            else
            {
                println("ERRO SEMÂNTICO: Erro inesperado. Problema no tipo do procedimento ou programa. Como isso chegou aqui???!!!")
                return false
            }
        }

        private fun desempilhaProcedimento()
        {
            //Desempilha a partir do topo da pilha
            while (listaIdentificadores.first.nome != "#")
                listaIdentificadores.pop()
            //Desempilha o marcador '#'
            listaIdentificadores.pop()
        }

        /***
         * Métodos auxiliares
         */
        @JvmStatic fun definirTipoDasVariaveis (tipo:String)
        {
            var i:Int = 0
            while (i < listaIdentificadores.size-1 && listaIdentificadores.get(i).tipo.equals(""))
                listaIdentificadores.get(i++).tipo = tipo
        }

        @JvmStatic fun abreEscopo()
        {
            profundidadeEscopo++
        }

        @JvmStatic fun fechaEscopo()
        {
            profundidadeEscopo--
            if (profundidadeEscopo == 0)
                desempilhaProcedimento()
        }

        @JvmStatic fun pilhaVazia(): Boolean = this.listaIdentificadores.isNullOrEmpty()
    }

}
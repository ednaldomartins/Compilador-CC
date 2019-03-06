package business.model

import java.util.LinkedList

class AnalisadorSemantico
{
    companion object
    {
        /***************************************************************************************************************
         * listaIdentificadores guarda as variáveis com seus nomes e tipos, e também o nome dos precedimentos,         *
         * considerando o tipo como procedimento, como também o nome do programa considerando program o seu tipo.      *
         *                                                                                                             *
         * profundidadeEscopo é incrementado sempre que o token no código é o 'BEGIN', e decrementado quando o token   *
         * no código é o 'END', logo quando (profundidadeEscopo > 0) então estamos em uma parte do código onde         *
         * variáveis ou procedimentos não podem ser declarado, mas podem ser usado.                                    *
         **************************************************************************************************************/
        private var listaIdentificadores: LinkedList<Identificador> = LinkedList()
        private var profundidadeEscopo:Int = 0

        /***************************************************************************************************************
         * O método analisaVariavel deve decidir de acordo com a profundidade do escopo se a variavel recebida deve    *
         * ser declarada ou usada.                                                                                     *
         **************************************************************************************************************/
        @JvmStatic fun analisaVariavel(identificador: Identificador):Boolean = if (profundidadeEscopo > 0) buscaIdentificadorNoPrograma(identificador) else empilhaVariavel(identificador)

        /***************************************************************************************************************
         * O método analisaProcedimento deve decidir de acordo com a profundidade do escopo se o procedimento          *
         * chamado deve ser declarado ou usado.                                                                        *
         **************************************************************************************************************/
        @JvmStatic fun analisaProcedimento(identificador: Identificador):Boolean = if (profundidadeEscopo > 0) buscaIdentificadorNoPrograma(identificador) else empilhaProcedimento(identificador)

        /***************************************************************************************************************
         * O método buscaIdentificadorNoPrograma deve buscar o identificador dentro do programa para saber se ele      *
         * existe e pode ser usado.                                                                                    *
         **************************************************************************************************************/
        private fun buscaIdentificadorNoPrograma(identificador: Identificador):Boolean
        {

            return true
        }

        /***************************************************************************************************************
         * O método buscaIdentificadorNoEscopo deve buscar o identificador dentro do próprio escopo em que ele está    *
         * sendo declarado.                                                                                            *
         **************************************************************************************************************/
        private fun buscaIdentificadorNoEscopo(identificador: Identificador): Boolean
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

        /***************************************************************************************************************
         * O método empilhaVariavel apenas empilha a variável caso ainda não tenha sido declarado uma variável com o   *
         * mesmo identificador dentro do mesmo escopo.                                                                 *
         **************************************************************************************************************/
        private fun empilhaVariavel(identificador: Identificador): Boolean
        {
            //se a variavel já foi declarada dentro do mesmo escopo, então retorna erro
            if (buscaIdentificadorNoEscopo(identificador))
            {
                println("ERRO SEMÂNTICO: Erro inesperado. Problema no tipo do procedimento ou programa. Como isso chegou aqui???!!!")
                return false
            }
            listaIdentificadores.push(identificador)
            return true
        }

        /***************************************************************************************************************
         * O método empilha procedimento deve empilhar o procedimento na pilha caso não exista um procedimento ou o    *
         * programa não tenha o mesmo nome. Esse método também empilha o nome do programa.                             *
         **************************************************************************************************************/
        private fun empilhaProcedimento(identificador: Identificador): Boolean
        {
            if (identificador.tipo.equals("procedure"))
            {
                //se já existe procedimento ou programa com o mesmo nome então não pode adicionar a pilha. retorna erro
                if (buscaIdentificadorNoEscopo(identificador))
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

        /***************************************************************************************************************
         * O método desempilhaProcedimento deve desempilhar todas as variavéis e procedimentos que existirem dentro do *
         * escopo dele. Ele ainda deve permanecer na pilha, pois está antes do marcador '#', portanto se um comando ou *
         * procedimento fizer uma chamada ainda dentro do escopo em que ele está contido, ele poderá ser chamado.      *
         **************************************************************************************************************/
        private fun desempilhaProcedimento()
        {
            //Desempilha a partir do topo da pilha
            while (listaIdentificadores.first.nome != "#")
                listaIdentificadores.pop()
            //Desempilha o marcador '#'
            listaIdentificadores.pop()
        }

        /***************************************************************************************************************
         *                                          MÉTODOS AUXILIARES                                                 *
         **************************************************************************************************************/
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
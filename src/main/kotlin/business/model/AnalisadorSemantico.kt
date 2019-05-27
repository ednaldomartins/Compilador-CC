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
        private var pilhaDeIdentificadores: LinkedList<Identificador> = LinkedList()
        //private var pilhaDeComandos: LinkedList<Identificador> = LinkedList()
        var tipoDeComandos: String = ""
        private var profundidadeEscopo:Int = 0

        /***************************************************************************************************************
         * O método analisaVariavel deve decidir de acordo com a profundidade do escopo se a variavel recebida deve    *
         * ser declarada ou usada.                                                                                     *
         **************************************************************************************************************/
        @JvmStatic fun analisaVariavel(identificador: Identificador):Boolean = if (profundidadeEscopo > 0) lerIdentificador(identificador.nome) else empilhaVariavel(identificador)

        /***************************************************************************************************************
         * O método analisaProcedimento deve decidir de acordo com a profundidade do escopo se o procedimento          *
         * chamado deve ser declarado ou usado.                                                                        *
         **************************************************************************************************************/
        @JvmStatic fun analisaProcedimento(identificador: Identificador):Boolean = if (profundidadeEscopo > 0) lerIdentificador(identificador.nome) else empilhaProcedimento(identificador)

        /***************************************************************************************************************
         * O método lerIdentificador deve buscar o identificador dentro do programa para saber se ele existe e pode    *
         * ser usado. Além disso os tipos dos identificadores devem ser do tipo compatível com as operações realizadas.*
         **************************************************************************************************************/
        private fun lerIdentificador(identificador: String):Boolean
        {
            //A leitura da pilha começa de cima para baixo, logo os últimos identificadores declarados são os primeiros.
            var i = 0
            while (pilhaDeIdentificadores.size-1 > i)
            {
                if (pilhaDeIdentificadores.get(i).nome == identificador)
                {
                    /*  após encontrar o identificador, deve-se verificar qual o seu tipo, e caso já exista algum
                        comando em andamento, deve-se verificar se os tipos são compatíveis.    */
                    if (pilhaDeIdentificadores.get(i).tipo.equals("procedure"))
                        return true
                    else
                        return analisaTipo(pilhaDeIdentificadores.get(i).tipo)
                }
                i++
            }
            println("ERRO SEMÂNTICO: Identificador não encontrado")
            return false
        }

        @JvmStatic fun analisaTipo(tipoDoIdentificador: String): Boolean
        {
            /**falta analisar se a pilha ta vazia
             * e tambem ve o opRelacional pra add na pilha
             */
            if (tipoDeComandos.equals(""))
            {
                tipoDeComandos = tipoDoIdentificador
                return true
            }
            else if (tipoDeComandos.equals("integer"))
            {
                if (tipoDoIdentificador.equals("integer"))
                {
                    return true
                }
                else if (tipoDoIdentificador.matches("\\*|\\/".toRegex()))
                {
                    return true
                }
                //verifica se o token atual é um relacional, esse comando agora será boolean
                else if (tipoDoIdentificador.matches("=|<|>|<=|>=|<>".toRegex()))
                {
                    //exemplo: tipoDeComando = "integer_relacional"
                    tipoDeComandos += "_relacional"
                    return true
                }
                else
                {
                    println("ERRO SEMÂNTICO: tipo imcopatível com integer")
                    return false
                }
            }
            else if (tipoDeComandos.equals("real"))
            {
                if (tipoDoIdentificador.equals("integer"))
                {
                    return true
                }
                else if (tipoDoIdentificador.matches("\\*|\\/".toRegex()))
                {
                    return true
                }
                else if (tipoDoIdentificador.equals("real"))
                {
                    //tipoDeComandos = identificador)
                    return true
                }
                //verifica se o token atual é um relacional, esse comando agora será boolean
                else if (tipoDoIdentificador.matches("=|<|>|<=|>=|<>".toRegex()))
                {
                    //exemplo: tipoDeComando = "real_relacional"
                    tipoDeComandos += "_relacional"
                    return true
                }
                else
                {
                    println("ERRO SEMÂNTICO: tipo imcopatível com real")
                    return false
                }
            }
            else if (tipoDeComandos.equals("boolean"))
            {
                if (tipoDoIdentificador.equals("boolean"))
                {
                    return true
                }
                /*  um tipo booleano não pode ser maior ou menor que outro termo
                    note que após o relacional ele continuará como boolean, logo, se o próximo termo for um número
                    vai acusar erro quando voltar para esse método, pois a situação não se encaixará em nenhum IF
                    Nessa situação está incluso: "=|<|>|<=|>=|<>|and|or"
                 */
                else if (tipoDoIdentificador.matches("=|<=|>=|<>|and|or|".toRegex()))
                {
                    //se for relacional, então só retorna true
                    return true
                }
                else
                {
                    println("ERRO SEMÂNTICO: tipo imcopatível com boolean")
                    return false
                }
            }
            else if (tipoDeComandos.matches("integer_relacional|real_relacional".toRegex()))
            {
                if (tipoDoIdentificador.matches("integer|real".toRegex()))
                {
                    //então, esse comando agora se trata de um tipo booleano
                    tipoDeComandos = "boolean"
                    return true
                }
                else
                {
                    println("ERRO SEMÂNTICO: quando integer|real && operador elacional, o próximo deve ser integer|real ")
                    return false
                }
            }
            else
            {
                println("ERRO SEMÂNTICO: tipo imcopatível desconhecido")
                return false
            }
        }

        /***************************************************************************************************************
         * O método buscaIdentificadorNoEscopo deve buscar o identificador dentro do próprio escopo em que ele está    *
         * sendo declarado.                                                                                            *
         **************************************************************************************************************/
        private fun buscaIdentificadorNoEscopo(identificador: Identificador): Boolean
        {
            //A leitura da pilha começa de cima para baixo, logo os últimos identificadores declarados são os primeiros.
            var i = 0
            while (pilhaDeIdentificadores.get(i).nome != "#")
            {
                if (pilhaDeIdentificadores.get(i).nome.equals(identificador.nome) && pilhaDeIdentificadores.get(i).tipo.equals((identificador.tipo)))
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
            pilhaDeIdentificadores.push(identificador)
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
                if (identificador.nome.equals(pilhaDeIdentificadores.get(pilhaDeIdentificadores.size-2).nome))
                {
                    println("ERRO SEMÂNTICO: Esse nome já foi usado como identificador no nome do programa")
                    return false
                }
                pilhaDeIdentificadores.push(identificador)
                pilhaDeIdentificadores.push(Identificador("#", "#"))
                return true
            }
            else if (identificador.tipo.equals("program"))
            {
                pilhaDeIdentificadores.push(Identificador("#", "#"))
                pilhaDeIdentificadores.push(identificador)
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
            while (pilhaDeIdentificadores.first.nome != "#")
                pilhaDeIdentificadores.pop()
            //Desempilha o marcador '#'
            pilhaDeIdentificadores.pop()
        }

        fun desempilharComandos()
        {
            tipoDeComandos = ""
        }

        /***************************************************************************************************************
         *                                          MÉTODOS AUXILIARES                                                 *
         **************************************************************************************************************/
        @JvmStatic fun definirTipoDasVariaveis (tipo:String)
        {
            var i = 0
            while (i < pilhaDeIdentificadores.size-1 && pilhaDeIdentificadores.get(i).tipo.equals(""))
                pilhaDeIdentificadores.get(i++).tipo = tipo
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

        @JvmStatic fun pilhaVazia(): Boolean = this.pilhaDeIdentificadores.isNullOrEmpty()
    }

}
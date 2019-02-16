package business.control

import business.model.Procedimento
import business.model.Simbolo
import business.model.Variavel
import java.util.LinkedList

class ControllerAnalisadorSintatico
{
    var indice: Int = 0
    lateinit var tab: LinkedList<Simbolo>
    lateinit var listaIdentificadores: LinkedList<String>
    lateinit var listaProcedimentos: LinkedList<Procedimento>

    private val PROGRAM = "program|PROGRAM"
    private val PROCEDURE = "procedure|PROCEDURE"
    private val BEGIN = "begin|BEGIN"
    private val END = "end|END"
    private val VAR = "var|VAR"
    private val ATRIBUICAO = ":="
    private val DO = "do|DO"
    private val WHILE = "while|WHILE"
    private val IF = "if|IF"
    private val THEN = "then|THEN"
    private val ELSE = "else|ELSE"
    private val TRUE = "true|TRUE"
    private val FALSE = "false|FALSE"
    private val NOT = "not|NOT"

    //exclusivamente um REGEX_tipo de uma variável
    private val TIPO = "integer|INTEGER|real|REAL|boolean|BOOLEAN|char|CHAR"


    fun analisar(tabela: LinkedList<Simbolo>)
    {
        AUX_copiarTabelaSemComentarios(tabela)
        listaIdentificadores = LinkedList()
        listaProcedimentos = LinkedList()
        programId()
    }

    /***
     *  programa →
     *      REGEX_program id;
     *      declarações_variáveis
     *      declarações_de_subprogramas
     *      comando_composto
     *      .
     */
    private fun programId()
    {
        if(REGEX_program())
        {
            AUX_proximo()
            val nomeDoPrograma = tab.get(indice).token
            if (REGEX_identificador())
            {
                listaIdentificadores.add(nomeDoPrograma)
                listaProcedimentos.add(Procedimento(nomeDoPrograma))
                AUX_proximo()
                if (tab.get(indice).token.equals(";"))
                {
                    AUX_proximo()
                    if(this.declaracoesVariaveis())
                    {
                        this.declaracoesDeSubprogramas()
                        //this.comandoComposto()
                    }
                }
                else print("ERRO: é esperado um ';' na linha ${tab.get(indice).linha}")
            }
            else print("ERRO: é esperado um 'IDENTIFICADOR' para o REGEX_program, na linha ${tab.get(indice).linha} ")
        }
        else print("ERRO: é esperado a 'PALAVRA_RESERVADA'  REGEX_program no início, na linha ${tab.get(indice).linha} ")
    }


    /***
     *  declarações_variáveis →
     *      var lista_declarações_variáveis
     *      | ε
     */
    private fun declaracoesVariaveis() : Boolean {
        if (REGEX_var())
            return listaDeclaracoesVariaveis()
        /*else{não há lista de declarações de variaveis. CONTINUE...}*/
        return true
    }


    /***
     *  lista_declarações_variáveis →
     *      lista_declarações_variáveis lista_de_identificadores: REGEX_tipo;
     *      | lista_de_identificadores: REGEX_tipo;
     */
    private fun listaDeclaracoesVariaveis() : Boolean
    {
        if(listaDeIdentificadores())
        {
            AUX_proximo()
            if (REGEX_tipo())
            {
                /*  definir o REGEX_tipo em todas as variaveis que foram declaradas  */
                var j = listaProcedimentos.last.argumentos.size - 1
                while (j >= 0 && listaProcedimentos.last.argumentos.get(j).tipo == "")
                {
                    listaProcedimentos.last.argumentos.get(j--).tipo = tab.get(indice).token
                }
                AUX_proximo()
                if(tab.get(indice).token.equals(";"))
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
            if(REGEX_begin() || REGEX_procedure())
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


    /***
     *  lista_de_identificadores →
     *      id
     *      | lista_de_identificadores, id
     */
    private fun listaDeIdentificadores() : Boolean
    {
        //verificar se é um 'IDENTIFICADOR'
        AUX_proximo()
        if (REGEX_identificador())
        {
            /*****************************************************
             *
             * ESSA PARTE DEVERIA SER IMPLEMENTADA NO SEMANTICO
             *
             *****************************************************/
            //verificar se já existe um REGEX_identificador declarado com o mesmo nome
            val nomeVariavel = tab.get(indice).token
            if( existeNaListaDeIdentificadores(nomeVariavel) )
            {
                print("ERRO: ['IDENTIFICADOR': $nomeVariavel, linha ${tab.get(indice).linha}] já foi declarado")
                return false
            }
            else
            {
                //entao adiciona o novo 'IDENTIFICADOR' à lista e incrementa o índice.
                listaIdentificadores.add(nomeVariavel)//remover essa lista
                listaProcedimentos.last.argumentos.add(Variavel(nomeVariavel, ""))
                AUX_proximo()
                if (tab.get(indice).token.equals(","))
                    return listaDeIdentificadores() //|id
                else if (tab.get(indice).token.equals(":"))
                    return true
                else
                {
                    print("ERRO: é esperado ':' ou ',' após um 'IDENTIFICADOR' ainda não declarado.")
                    return false
                }
            }
        }
        else return false   /*se já declarou as variáveis, pode ser um REGEX_begin|REGEX_procedure*/
    }


    /***
     *  declarações_de_subprogramas →
     *      declarações_de_subprogramas declaração_de_subprograma;
     *      | ε
     */
    private fun declaracoesDeSubprogramas(): Boolean
    {
        if (REGEX_procedure())
        {
            return declaracaoDeSubprograma()
        }
        else if(REGEX_begin())
        {
            return true
        }
        else
        {
            println("ERRO: é esperado um 'BEGIN' ou 'PROCEDURE'")
            return false
        }

    }


    /***
     *  declaração_de_subprograma →
     *      REGEX_procedure id argumentos;
     *      declarações_variáveis
     *      declarações_de_subprogramas
     *      comando_composto
     */
    private fun declaracaoDeSubprograma() :Boolean{
        if(procedureId())
        {
            /* Um procedimento pode ter var: no início do seu escopo    */
            AUX_proximo()
            if (declaracoesVariaveis())
            {
                /*  após as declarações de variáveis e antes do REGEX_begin, um procedimento pode conter um subprograma   */
                if (declaracoesDeSubprogramas())
                {
                    if(comandoComposto())
                    {
                        //eu acredito que pelo que estah PDF, aqui o retorno já vai ser o metodo comandoComposto no lugar do IF
                        return true
                    }
                    else
                    {
                        println("ERRO: problema em comando composto")
                        return false
                    }
                }
                else
                {
                    println("ERRO: problema em declarações de Subprogramas")
                    return false
                }
            }
            else
            {
                println("ERRO: problema nas declarações de variáveis")
                return false
            }
        }
        else
        {
            println("ERRO: problema na declaração do procedimento")
            return false
        }
    }

    private fun procedureId() : Boolean
    {
        AUX_proximo()
        if (REGEX_identificador())
        {
            val nomeProcedimento = tab.get(indice).token //procedimento só será salvo se passar nos testes
            var novoProcedimento = Procedimento(nomeProcedimento)
            AUX_proximo()
            if (argumentos(novoProcedimento))
            {
                /*****************************************************
                 *
                 * ESSA PARTE DEVERIA SER IMPLEMENTADA NO SEMANTICO
                 *
                 *****************************************************/
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
        else if (REGEX_begin())
        {
            return true
        }

        return false
    }

    /***
     *  argumentos →
     *      (lista_de_parametros)
     *      | ε
     */
    private fun argumentos(procedimento: Procedimento) : Boolean
    {
        if (tab.get(indice).token.equals("("))   //tem lista de parâmetros
            return listaDeParametros(procedimento)
        else if (tab.get(indice).token.equals(";"))  //não tem lista de parâmetros
            return true
        else
        {
            println("ERRO: ao declarar um procedimento é esperado uma lista de argumentos ou ';'")
            return false
        }
    }

    /***
     *  lista_de_parametros →
     *      lista_de_identificadores: REGEX_tipo
     *      | lista_de_parametros; lista_de_identificadores: REGEX_tipo
     */
    private fun listaDeParametros(procedimento: Procedimento): Boolean
    {
        AUX_proximo()
        if (REGEX_var()) //se for um 'var' pula, não interessa para o sintático no momento
            AUX_proximo()

        if (REGEX_identificador())
        {
            val nomeArgumento = tab.get(indice).token
            AUX_proximo()
            if (tab.get(indice).token.equals(":"))
            {
                AUX_proximo()
                val tipoArgurmento = tab.get(indice).token
                if (REGEX_tipo())
                {
                    procedimento.argumentos.add(Variavel(nomeArgumento, tipoArgurmento))//adiciona o REGEX_tipo do argumento.  PARA O SEMANTICO É IDEAL SABER O NOME DO ARGUMENTO PARA COMPARAR SE JA EXISTE
                    AUX_proximo()
                    if( tab.get(indice).token.equals(")") )
                    {
                        AUX_proximo()
                        if (tab.get(indice).token.equals(";"))
                            return true
                        else
                        {
                            println("ERRO: É esperado um ';' para completar o procedimento")
                            return false
                        }
                    }
                    //recursivamento valida os próximos argumentos
                    else if (tab.get(indice).token.equals(";"))
                    {
                        return listaDeParametros(procedimento)
                    }
                    else
                    {
                        println("ERRO: É esperado um ')' para completar os argumentos ou ',' para novos argumentos.")
                        return false
                    }
                }
                else
                {
                    println("ERRO: É esperado o REGEX_tipo do argumento na linha")
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

    /***
     *  comando_composto →
     *      begin
     *      comandos_opcionais
     *      end
     */
    private fun comandoComposto():Boolean
    {
        AUX_proximo()
        if (REGEX_begin())
        {
            AUX_proximo()
            if(comandosOpcionais())
            {
                AUX_proximo()
                if (REGEX_end())
                {
                    return true
                }
                else
                {
                    println("ERRO: é esperado um 'end' no fim do procedimento")
                    return false
                }
            }
            else
            {
                println("ERRO: problema nos comandos opcionais")
                return false
            }
        }
        else
        {
            println("ERRO: após declaração do método é esperado 'begin' para iniciar os comandos")
            return false
        }
    }

    /***
     *  comandos_opcionais →
     *      lista_de_comandos
     *      | ε
     */
    private fun comandosOpcionais(): Boolean
    {
        /*  O procedimento pode ter uma lista de comandos ou ser vazio. se for vazio tem que ter um 'end'   */
        if (REGEX_end())
        {
            indice--    //pq ele vai incrementar quando sair e vai testar o end novamente
            return true
        }
        else if(listaDeComandos())
            return true
        else
        {
            println("ERRO: problema na lista de comandos")
            return false
        }
    }

    /***
     *  lista_de_comandos →
     *      comando
     *      | lista_de_comandos; comando
     */
    private fun listaDeComandos(): Boolean
    {
        if(comando())
            AUX_proximo()
            if (!REGEX_end())
                return listaDeComandos()
        //se retornou falso nos IFs aciman então contém erro na lista de comandos.
        println("ERRO: lista de comandos contém erro.")
        return false
    }

    /***
     *  comando →
     *      variável := expressão
     *      | ativação_de_procedimento
     *      | comando_composto
     *      | if expressão then comando parte_else
     *      | while expressão do comando
     */
    private fun comando(): Boolean
    {
        //to do
        if(variavel())
        {
            AUX_proximo()
            if (REGEX_atribuicao())
            {
                if (expressao())
                {
                    return true//por enquanto
                }
            }
        }
        return true
    }

    /***
     *  expressão →
     *      expressão_simples
     *      | expressão_simples op_relacional expressão_simples
     */
    private fun expressao(): Boolean
    {
        if(expressaoSimples())
        {
            if (opRelacional())
            {
                if (expressaoSimples())

            }
            return true
        }
        return true//pra tirar o erro
    }

    /***
     *  expressão_simples →
     *      termo
     *      | sinal termo
     *      | expressão_simples op_aditivo termo
     */
    private fun expressaoSimples(): Boolean
    {
        if(termo())
        {

        }
        else if (sinal())
        {
            if(termo())
            {
                return true
            }
        }
        else if (expressaoSimples())
        {
            if (opAditivo())
            {
                if (termo())
                {
                    return true
                }
            }
        }
    }

    /***
     *  ativação_de_procedimento →
     *      id
     *      | id (lista_de_expressões)
     */
    private fun ativacaoDeProcedimento()
    {

    }

    /***
     *  termo →
     *      fator
     *      | termo op_multiplicativo fator
     */
    private fun termo(): Boolean
    {
        if(fator())
            if (opMultiplicativo())
            return true
        else
    }

    /***
     *  fator →
     *      id
     *      | id(lista_de_expressões)
     *      | num_int
     *      | num_real
     *      | true
     *      | false
     *      | (expressão)
     *      | not fator
     */
    private fun fator(): Boolean {
        if (REGEX_identificador())
        {
            AUX_proximo()
            if (tab.get(indice).token.equals("("))
            {
                AUX_proximo()
                if (listaDeExpressoes())
            }
        }
    }

    /***
     *  lista_de_expressões →
     *      expressão
     *      | lista_de_expressões, expressão
     */
    private fun listaDeExpressoes(): Boolean {
        if (expressao())
        {
            AUX_proximo()
            if (listaDeExpressoes())
            {

            }
        }
    }

    /***
     *  variável →
     *      id
     */
    private fun variavel(): Boolean {
        return REGEX_identificador()
    }

    /***
     *  op_aditivo →
     *      + | - | or
     */
    private fun opAditivo(): Boolean {
        return REGEX_operadorAditivo()
    }

    /***
     *  op_multiplicativo →
     *      * | / | and
     */
    private fun opMultiplicativo(): Boolean {
        return REGEX_operadorMultiplicativo()
    }

    /***
     *  op_relacional →
     *      = | < | > | <= | >= | <>
     */
    private fun opRelacional(): Boolean {
        return REGEX_operadorRelacional()
    }

    /***
     *  sinal →
     *      + | -
     */
    private fun sinal(): Boolean {
        return REGEX_sinal()
    }



    /*******************************************************************************************************************
     *                                   Atalhos para testar Regex                                                     *
     ******************************************************************************************************************/
    private fun REGEX_program() : Boolean = tab.get(indice).token.matches(PROGRAM.toRegex())

    private fun REGEX_begin() : Boolean = tab.get(indice).token.matches(BEGIN.toRegex())

    private fun REGEX_procedure() : Boolean = tab.get(indice).token.matches(PROCEDURE.toRegex())

    private fun REGEX_var() : Boolean = tab.get(indice).token.matches(VAR.toRegex())

    private fun REGEX_identificador() : Boolean = tab.get(indice).classificacao.equals("IDENTIFICADOR")

    private fun REGEX_tipo() : Boolean = tab.get(indice).token.matches(TIPO.toRegex())

    private fun REGEX_end() : Boolean = tab.get(indice).token.matches(END.toRegex())

    private fun REGEX_atribuicao() : Boolean = tab.get(indice).classificacao.equals("ATRIBUICAO")

    private fun REGEX_operadorAditivo() : Boolean = tab.get(indice).classificacao.equals("OPERADOR_ADITIVO")

    private fun REGEX_operadorMultiplicativo() : Boolean = tab.get(indice).classificacao.equals("OPERADOR_MULTIPLICATIVO")

    private fun REGEX_operadorRelacional() : Boolean = tab.get(indice).classificacao.equals("OPERADOR_RELACIONAL")

    private fun REGEX_sinal() : Boolean = tab.get(indice).token.matches("+|-".toRegex())

    /*******************************************************************************************************************
     *                           Métodos auxiliares para o analisador sintático                                        *
     ******************************************************************************************************************/
    private fun existeNaListaDeIdentificadores (identificador: String): Boolean
    {
        var i = 0
        if (!listaProcedimentos.isNullOrEmpty())
            while( i < this.listaProcedimentos.last.argumentos.size)
                if(identificador == listaProcedimentos.last.argumentos.get(i++).nome)
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
                                if (proc.argumentos.get(i).tipo.toLowerCase() == procedimento.argumentos.get(i).tipo.toLowerCase())
                                else
                                    return false
                            }
                        }
                        else return false   //tem o mesmo nome, mas a lista de parâmetros é diferente
                    }
                    else
                        return false    //tem o nome diferente
                }
            }
            else
                return false    //ainda não existe procedimentos, logo não existe outro igual
        }
        //se não houve diferença, então retorna true
        return true
    }

    private fun AUX_copiarTabelaSemComentarios(tabela: LinkedList<Simbolo>)
    {
        this.tab = LinkedList()
        for(t in tabela)
            if(t.classificacao != "COMENTARIO")
                this.tab.add(t)
    }

    private fun AUX_proximo()
    {
        if (indice < tab.size-1)
            indice++
        else
        {
            println("ERRO: fim da leitura do programa antes do 'end.'")
            System.exit(0)
        }
    }
}


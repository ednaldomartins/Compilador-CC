package business.control

import business.model.AnalisadorSemantico as Semantico
import business.model.Identificador
import business.model.Simbolo
import java.util.LinkedList

class ControllerAnalisadorSintatico
{
    var indice: Int = 0
    lateinit var tab: LinkedList<Simbolo>
    lateinit var listaIdentificadores: LinkedList<String>
    lateinit var identificadorAtual: Identificador

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

    //FEITO EM SALA
    private val CASE = "case|CASE"
    private val OF = "of|OF"

    //exclusivamente um REGEX_tipo de uma variável
    private val TIPO = "integer|INTEGER|real|REAL|boolean|BOOLEAN|char|CHAR"


    fun analisar(tabela: LinkedList<Simbolo>)
    {
        AUX_copiarTabelaSemComentarios(tabela)
        identificadorAtual = Identificador()
        listaIdentificadores = LinkedList()
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
            identificadorAtual.nome = tab.get(indice).token
            identificadorAtual.tipo = tab.get(indice-1).token
            //val nomeDoPrograma = tab.get(indice).token
            //val tipoDoPrograma = tab.get(indice-1).token
            if (REGEX_identificador())
            {
                Semantico.analisaProcedimento(identificadorAtual)
                //listaIdentificadores.add(nomeDoPrograma)
                //listaProcedimentos.add(Procedimento(nomeDoPrograma))
                AUX_proximo()
                if (tab.get(indice).token.equals(";"))
                {
                    AUX_proximo()
                    if(this.declaracoesVariaveis())
                    {
                        if (this.declaracoesDeSubprogramas())
                        {
                            if (this.comandoComposto())
                            {
                                if (tab.get(indice).token.equals("."))
                                    println("O PROGRAMA PASSOU NO TESTE DO SINTÁTICO")
                                else println("ERRO: ao final do program é esperado um '.'")
                            }
                        }
                        else println("ERRO: problema nas declarações de Subprogramas")
                    }
                    else println("ERRO: problema nas declarações de variáveis")
                }
                else println("ERRO: é esperado um ';' na linha ${tab.get(indice).linha}")
            }
            else println("ERRO: é esperado um 'IDENTIFICADOR' para o REGEX_program, na linha ${tab.get(indice).linha} ")
        }
        else println("ERRO: é esperado a 'PALAVRA_RESERVADA'  REGEX_program no início, na linha ${tab.get(indice).linha} ")
    }


    /***
     *  declarações_variáveis →
     *      var lista_declarações_variáveis
     *      | ε
     */
    private fun declaracoesVariaveis() : Boolean {
        if (REGEX_var())
        {
            AUX_proximo()
            return listaDeclaracoesVariaveis()
        }
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
                //O Semântico vai definir os tipos das variáveis que foram declaradas nesse trecho.
                Semantico.definirTipoDasVariaveis(tab.get(indice).token)
                AUX_proximo()
                if(tab.get(indice).token.equals(";"))
                {
                    AUX_proximo()
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
        else if(!Semantico.pilhaVazia())
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
            return false
        }
    }


    /***
     *  lista_de_identificadores →
     *      id
     *      | lista_de_identificadores, id
     */
    private fun listaDeIdentificadores() : Boolean
    {
        //pular, porque os metodos que chamam listaDeIdentificadores ou veio de um ';' ou de um 'var'

        if (REGEX_identificador())
        {
            //limpar informação do identificador anterior
            identificadorAtual = Identificador()
            identificadorAtual.nome = tab.get(indice).token
            //O Analisador Semântico vai analisar se a variável já foi declarada no escopo do próprio procedimento
            if (Semantico.analisaVariavel(identificadorAtual))
            {
                AUX_proximo()
                if (tab.get(indice).token.equals(","))
                {
                    AUX_proximo()
                    return listaDeIdentificadores() //|id
                }
                else if (tab.get(indice).token.equals(":"))
                    return true
                else
                {
                    print("ERRO: é esperado ':' ou ',' após um 'IDENTIFICADOR' ainda não declarado.")
                    return false
                }
            }
            else
                return false
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
        if (declaracaoDeSubprograma())
        {
            if (tab.get(indice).token.equals(";")) {
                AUX_proximo()
                return declaracoesDeSubprogramas()
            }
        }
        else if(REGEX_begin())
        {
            return true
        }
        //print("pode ser um .???")
        return true
    }


    /***
     *  declaração_de_subprograma →
     *      procedure id argumentos;
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
                       return true
                    }
                    else
                    {
                        println("ERRO: problema em comando composto na linha ${tab.get(indice).linha}")
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
        return false
    }

    private fun procedureId() : Boolean
    {
        if (REGEX_procedure())
        {
            AUX_proximo()
            //Guardar e enviar o nome do procedimento e seu tipo 'procedure'
            identificadorAtual = Identificador()
            identificadorAtual.nome = tab.get(indice).token
            identificadorAtual.tipo = tab.get(indice-1).token
            if (REGEX_identificador())
            {
                //analisar procedimento e adicionar a pilha
                if ( Semantico.analisaProcedimento(identificadorAtual) )
                {
                    AUX_proximo()
                    if (argumentos())
                    {
                        return true
                    }
                    else
                    {
                        println("ERRO SINTÁTICO: problema na lista de argumentos")
                        return false
                    }
                }
                else
                {
                    println("ERRO SEMÂNTICO: problema na declaração do procedimento")
                    return false
                }
            }
            else if (REGEX_begin())
            {
                return true
            }
        }

        return false
    }

    /***
     *  argumentos →
     *      (lista_de_parametros)
     *      | ε
     */
    private fun argumentos() : Boolean
    {
        if (tab.get(indice).token.equals("("))
        {   //tem lista de parâmetros
            AUX_proximo()
            return listaDeParametros()
        }
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
    private fun listaDeParametros(): Boolean
    {
        //AUX_proximo()
        //se for um 'var' pula, não interessa para o sintático no momento
        if (REGEX_var())
            AUX_proximo()

        //if (REGEX_identificador())
        if (listaDeIdentificadores())
        {
            if (tab.get(indice).token.equals(":"))
            {
                AUX_proximo()
                val tipoArgurmento = tab.get(indice).token
                if (REGEX_tipo())
                {
                    /** agora definir o tipo das variáveis que foram declaradas **/
                    Semantico.definirTipoDasVariaveis(tipoArgurmento)
                    //adiciona o REGEX_tipo do argumento.  PARA O SEMANTICO É IDEAL SABER O NOME DO ARGUMENTO PARA COMPARAR SE JA EXISTE
                    AUX_proximo()
                    if( tab.get(indice).token.equals(")") )
                    {
                        AUX_proximo()
                        if (tab.get(indice).token.equals(";"))
                        {
                            return true
                        }
                        else
                        {
                            println("ERRO: É esperado um ';' para completar o procedimento")
                            return false
                        }
                    }
                    //recursivamento valida os próximos argumentos
                    else if (tab.get(indice).token.equals(";"))
                    {
                        AUX_proximo()
                        return listaDeParametros()
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
        if (REGEX_begin())
        {
            //SEMÂNTICO: incrementa quando encontra um begin, para aumentar a profundidade do escopo
            Semantico.abreEscopo()
            AUX_proximo()
            if(comandosOpcionais())
            {
                if (REGEX_end())
                {
                    //SEMÂNTICO: decrementa quando encontra um end, para diminui a profundidade do escopo
                    Semantico.fechaEscopo()
                    AUX_proximo()
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
        return false
    }

    /***
     *  comandos_opcionais →
     *      lista_de_comandos
     *      | ε
     */
    private fun comandosOpcionais(): Boolean
    {
        if(listaDeComandos())
            return true
        else
        {
            return true
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
        {
            //após um comando vem um ';'
            if (tab.get(indice).token.equals(";"))
            {
                /**aqui tem que desempilhar lista de comandos antes de receber um novo comando ou sair de um.**/
                Semantico.desempilharComandos()
                AUX_proximo()
                if (REGEX_end())
                    return true
                else if ( listaDeComandos() )
                    return true
            }
        }
        println("ERRO: problema na lista de comandos.")
        return false//ou é E, false??
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
        /** identificador que será empilhado na pilhaDeComandos do Semântico **/
        var id = Identificador(tab.get(indice).token, "")

        if(identificador())
        {
            if (ativacaoDeProcedimento())
                return true
            else
            {
                AUX_proximo()
                if (REGEX_atribuicao())
                {
                    /** como está dentro de um begin, então vai lerVariavel() e verificar o tipo **/
                    Semantico.analisaVariavel(id)
                    AUX_proximo()
                    return expressao()
                }
            }
        }
        else if (comandoComposto())
            return true
        else if (REGEX_if())
        {
            AUX_proximo()
            if (expressao())
            {
                if (Semantico.tipoDeComandos.equals("boolean"))
                {
                    if (REGEX_then())
                    {
                        //limpar tipo do comando
                        Semantico.desempilharComandos()
                        AUX_proximo()
                        if (comando())
                        {
                            AUX_proximo()
                            if(REGEX_else())
                            {
                                AUX_proximo()
                                return (comando())
                            }
                            else {
                                AUX_proximo()
                                return true

                            }
                        }
                    }
                }
                else
                {
                    println("ERRO SEMÂNTICO: comando do if não é do tipo boolean.")
                    return false
                }
            }
        }
        else if(REGEX_while())
        {
            AUX_proximo()
            if (expressao())
            {
                //verificar se é um comando boolean
                if (Semantico.tipoDeComandos.equals("boolean"))
                {
                    if (REGEX_do())
                    {
                        //remover tipo do comando
                        Semantico.desempilharComandos()
                        AUX_proximo()
                        return comando()
                    }
                    else
                    {
                        println("ERRO SINTÁTICO: depois da expressão do 'while' é esperado um 'do'.")
                        return false
                    }
                }
                else
                {
                    println("ERRO SEMÂNTICO: comando do while não é do tipo boolean.")
                    return false
                }
            }
        }
        //FEITO NA SALA
        else if (REGEX_case())
        {
            AUX_proximo()
            if (seletor())
            {
                AUX_proximo()
                if (REGEX_of())
                {
                    AUX_proximo()
                    if (listaSeletor()) {
                        if (REGEX_else()) {
                            AUX_proximo()
                            if (tab.get(indice).token.equals(":"))
                            {
                                AUX_proximo()
                                return (comando())
                            } else {
                                println("ERRO: problema de sintaxe no 'case', após o else é esperado um ':'")
                                return false
                            }
                        } else {
                            println("ERRO: problema de sintaxe no 'case', é esperado um 'else' após a lista do case")
                            return false
                        }
                    }
                }
                else
                {
                    println("ERRO: problema de sintaxe no 'case', é esperado um 'of' após um número")
                    return false
                }
            }
            else
            {
                println("ERRO: problema de sintaxe no 'case', é esperado um número após o case")
                return false
            }
        }
        return false
    }

    private fun listaSeletor(): Boolean
    {
        if (seletor())
        {
            AUX_proximo()
            if (tab.get(indice).token.equals(":"))
            {
                AUX_proximo()
                if (comando())
                {
                    if (tab.get(indice).token.equals(";"))
                    {
                        AUX_proximo()
                        /** Aqui também tem que desempilhar os comandos **/
                        Semantico.desempilharComandos()
                        if (seletor())
                            return listaSeletor()
                        else
                            return true
                    }
                    else
                    {
                        println("ERRO: problema de sintaxe no 'case', é esperado um ';' após o comando do case seletor")
                        return false
                    }
                }
                else
                {
                    println("ERRO: problema de sintaxe no 'case', problema no 'else'")
                    return false
                }
            }
            else
            {
                println("ERRO: problema de sintaxe no 'case', após o seletor é esperado um ':''")
                return false
            }
        }
        else
        {
            println("ERRO: problema de sintaxe no 'case', é esperado uma lista de seletores do case")
            return false
        }
    }

    //FEITO EM SALA
    private fun seletor(): Boolean = REGEX_numInt() || REGEX_numReal()

    /***
     *  expressão →
     *      expressão_simples
     *      | expressão_simples op_relacional expressão_simples
     */
    private fun expressao(): Boolean
    {
        if(expressaoSimples())
        {
            /**ainda tenho que arrumar isso aqui pra testar o comando relacional < > com true e false**/
            if (opRelacional())
            {
                /*  Uma forma que encontrei para testar alterar o tipo de uma expressão
                    exemplo:    se comando() --> i:= 3 < 5 --> i:= true
                    daí verificasse se o tipo de i := boolean. Esse teste servirá principalmente para if e while.
                    Nessa situação enviarei o token, pois o opRelacional usado no para integer|real em alguns casos,
                    pode ser diferentes dos que são usados no boolean
                    exemplo:    if true > true then (EXPRESSÃO ERRADA)
                                if 5 or 7 then (EXPRESSÃO ERRADA)
                                if 7 > 6 and true then (EXPRESSÃO CORRETA)
                    Dentro do semântico o comando 7 > 6 se torna true do tipo boolean.
                 */
                Semantico.analisaTipo(tab.get(indice).token)
                AUX_proximo()
                return expressaoSimples()
            }
            return true
        }
        return false
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
            if (opAditivo())
            {
                AUX_proximo()
                return expressaoSimples()
            }
            return true
        }
        //o termo pode ser: um número que recebe um sinal negativo antes. exemplo: -4
        else if (sinal())
        {
            return termo()
        }
        println("ERRO: é esperado pelo menos um termo")
        return false
    }

    /***
     *  ativação_de_procedimento →
     *      id
     *      | id (lista_de_expressões)
     */
    private fun ativacaoDeProcedimento() : Boolean
    {
        if (REGEX_identificador())
        {
            AUX_proximo()
            if (tab.get(indice).token.equals("("))
            {
                AUX_proximo()
                if (listaDeExpressoes())
                {
                    if( tab.get(indice).token.equals(")") )
                    {
                        AUX_proximo()
                        return true
                    }
                    else
                    {
                        println("ERRO: problema na sintax do procedimento")
                        return false
                    }
                }
            }
            else
            {
                //então não é um procedimento. voltar e testar se é um identificador
                indice--
                return false
            }
        }
        return false
    }

    /***
     *  termo →
     *      fator
     *      | termo op_multiplicativo fator
     */
    private fun termo(): Boolean
    {
        /*
            pode ser apenas um fator. Se tiver um opMultiplicativo depois, então tem que ter um termo depois
            ,senão pode retornar true pois é apenas um fator.
         */
        if(fator())
        {
            AUX_proximo()
            if (opMultiplicativo())
            {
                Semantico.analisaTipo(tab.get(indice).token)
                AUX_proximo()
                return termo()
            }
            return true
        }
        println("ERRO: problema no termo")
        return false
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
        /** identificador que será empilhado na pilhaDeComandos do Semântico **/
        var id = Identificador(tab.get(indice).token, "")

        if (REGEX_identificador())
        {
            /**acho que o semantico é pra ser chamado aqui**/
            Semantico.analisaVariavel(id)
            AUX_proximo()
            if (tab.get(indice).token.equals("("))
            {
                AUX_proximo()
                if (listaDeExpressoes())
                {
                    //se abriu tem que fechar
                    AUX_proximo()
                    if (tab.get(indice).token.equals(")"))
                    {
                        AUX_proximo()
                        return true
                    }
                }
            }
            //tem que decrementar porque quando voltar vai incrementar novamente
            indice--
            return true
        }
        else if (REGEX_numInt())
        {
            Semantico.analisaTipo("integer")
            return true
        }
        else if (REGEX_numReal())
        {
            Semantico.analisaTipo("real")
            return true
        }
        else if (REGEX_true())
        {
            Semantico.analisaTipo("boolean")
            return true
        }
        else if (REGEX_false())
        {
            Semantico.analisaTipo("boolean")
            return true
        }
        else if ( tab.get(indice).token.equals("(") )
        {
            AUX_proximo()
            if (expressao())
            {
                return tab.get(indice).token.equals(")")
            }
            else
            {
                println("ERRO: problema no fator -> (expressão)")
                return false
            }
        }
        else if (REGEX_not())
        {
            AUX_proximo()
            return fator()
        }
        else
        {
            println("ERRO: problema no fator.")
            return false
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
            if (tab.get(indice).token.equals(","))
                return listaDeExpressoes()
            indice--
            return true
        }

        println("ERRO: problema na lista de expressões.")
        return false
    }

    /***
     *  variável →
     *      id
     */
    private fun identificador(): Boolean {
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

    private fun REGEX_if() : Boolean = tab.get(indice).token.matches(IF.toRegex())

    private fun REGEX_then() : Boolean = tab.get(indice).token.matches(THEN.toRegex())

    private fun REGEX_else() : Boolean = tab.get(indice).token.matches(ELSE.toRegex())

    private fun REGEX_while() : Boolean = tab.get(indice).token.matches(WHILE.toRegex())

    private fun REGEX_do() : Boolean = tab.get(indice).token.matches(DO.toRegex())

    private fun REGEX_atribuicao() : Boolean = tab.get(indice).classificacao.equals("ATRIBUICAO")

    private fun REGEX_operadorAditivo() : Boolean = tab.get(indice).classificacao.equals("OPERADOR_ADITIVO")

    private fun REGEX_operadorMultiplicativo() : Boolean = tab.get(indice).classificacao.equals("OPERADOR_MULTIPLICATIVO")

    private fun REGEX_operadorRelacional() : Boolean = tab.get(indice).classificacao.equals("OPERADOR_RELACIONAL")

    private fun REGEX_sinal() : Boolean = tab.get(indice).token.matches("\\+|\\-".toRegex())

    private fun REGEX_numInt() : Boolean = tab.get(indice).classificacao.equals("NUMERO_INTEIRO")

    private fun REGEX_numReal() : Boolean = tab.get(indice).classificacao.equals("NUMERO_REAL")

    private fun REGEX_true() : Boolean = tab.get(indice).token.matches(TRUE.toRegex())

    private fun REGEX_false() : Boolean = tab.get(indice).token.matches(FALSE.toRegex())

    private fun REGEX_not(): Boolean = tab.get(indice).token.matches(NOT.toRegex())

    //FEITO NA SALA
    private fun REGEX_case(): Boolean = tab.get(indice).token.matches(CASE.toRegex())
    private fun REGEX_of(): Boolean = tab.get(indice).token.matches(OF.toRegex())

    /*******************************************************************************************************************
     *                           Métodos auxiliares para o analisador sintático                                        *
     ******************************************************************************************************************/
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


package business.control

import business.model.Simbolo
import infra.Arquivo

import java.io.IOException
import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger

class AnalisadorFacade
{
    /**  Windows = \\ , Linux = //  **/
    val LOCAL_ARQUIVO = "..//Compilador_CC//docs//codes//"

    //val nomeArquivo = "codigo_1_simples_correto.txt"
    val nomeArquivo = "codigo_2_complexo_correto.txt"
    //val nomeArquivo = "codigo_3_erro_opRelacional_boolean.txt"
    //val nomeArquivo = "codigo_4_erro_opRelacional_numero.txt"
    //val nomeArquivo = "codigo_5_var_procedure_nome.txt"

    val tabela = LinkedList<Simbolo>()
    val codigo = LinkedList<String>(carregarListaArquivo())

    fun inicializarAnalisador ()
    {
        //analise
        val analisadorLexico = ControllerAnalisadorLexico()
        analisadorLexico.analisar(tabela, codigo)
        val analisadorSintatico = ControllerAnalisadorSintatico()
        analisadorSintatico.analisar(tabela)
    }

    /**************************************************************************
     * Metodo para retornar Arquivo de codigo em MultableList                 *
     * @return MutableList                                                    *
     **************************************************************************/
    fun carregarListaArquivo(): MutableList<String>? {
        try {
            return Arquivo(LOCAL_ARQUIVO+nomeArquivo).carregarCodigo() as MutableList<String>
        } catch (ex: IOException) {
            Logger.getLogger(ControllerAnalisadorLexico::class.java.name).log(Level.SEVERE, null, ex)
        }
        return null
    }

}
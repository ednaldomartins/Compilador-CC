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
    val ARQUIVO_ORIGEM = "..//Compilador_CC//codigo2.txt"
    val tabela = LinkedList<Simbolo>()
    val codigo = LinkedList<String>(carregarListaArquivo())

    fun inicializarAnalisador ()
    {
        //analise
        var analisadorLexico = ControllerAnalisadorLexico()
        analisadorLexico.analisar(tabela, codigo)
        var analisadorSintatico = ControllerAnalisadorSintatico()
        analisadorSintatico.analisar(tabela)
    }

    /**************************************************************************
     * Metodo para retornar Arquivo de codigo em MultableList                 *                                                           *
     * @return MutableList                                                    *
     **************************************************************************/
    fun carregarListaArquivo(): MutableList<String>? {
        try {
            return Arquivo(ARQUIVO_ORIGEM).carregarCodigo() as MutableList<String>
        } catch (ex: IOException) {
            Logger.getLogger(ControllerAnalisadorLexico::class.java.name).log(Level.SEVERE, null, ex)
        }

        return null
    }

}
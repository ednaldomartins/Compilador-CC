
package compilador;

import business.control.ControllerAnalisadorLexico;
import business.control.ControllerAnalisadorSintatico;
import infra.Arquivo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import business.model.Lexico;

/**
 * @author marti
 *  date: 02.08.2018
 */
public class Compilador {

    public static void main(String[] args) {
        ControllerAnalisadorLexico analisador = new ControllerAnalisadorLexico();
        analisador.analisar();
        
        ControllerAnalisadorSintatico analisadorSintatico = new ControllerAnalisadorSintatico();
        //analisador.exibirTabela();
    }    
}

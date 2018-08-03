
package compilador;

import infra.Arquivo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marti
 *  date: 02.08.2018
 */
public class Compilador {

    public static void main(String[] args) {
        try {
            //testando lista
            List <String> codigo = new ArrayList (new Arquivo("..\\Compilador\\codigo.txt").carregarCodigo());
            int numeroLinhas = codigo.size();
            for(int i = 0; i < numeroLinhas; i++) {
                System.out.println(codigo.get(i));
            }
            
        
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

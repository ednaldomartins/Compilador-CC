
package compilador;

import infra.Arquivo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Lexico;

/**
 * @author marti
 *  date: 02.08.2018
 */
public class Compilador {

    public static void main(String[] args) {
        testeArquivoArray();
        testeExpressaoRegular();
    }
    
    
    
    //teste sem junit
    public static void testeArquivoArray()
    {
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
    
    public static void testeExpressaoRegular()
    {
        //https://www.devmedia.com.br/conceitos-basicos-sobre-expressoes-regulares-em-java/27539
        Lexico lexico = new Lexico();
        boolean teste;
        
        //Testando numeros inteiros
        teste = "993453".matches(lexico.getNumeroInteiro());
        System.out.print("\n\n\n"+  teste  +"\n");
        teste = "99343.".matches(lexico.getNumeroInteiro());
        System.out.print("\n"+  teste  +"\n");
        teste = "99.075".matches(lexico.getNumeroInteiro());
        System.out.print("\n"+  teste  +"\n");
        teste = ".99545".matches(lexico.getNumeroInteiro());
        System.out.print("\n"+  teste  +"\n");
        //hard
        teste = "99.e075".matches(lexico.getNumeroInteiro());
        System.out.print("\n"+  teste  +"\n");
        teste = "w99545".matches(lexico.getNumeroInteiro());
        System.out.print("\n"+  teste  +"\n");
        teste = "w995q45e".matches(lexico.getNumeroInteiro());
        System.out.print("\n"+  teste  +"\n");
        
        
        //testando numeros reais
        teste = "995345".matches(lexico.getNumeroReal());
        System.out.print("\n\n\n"+  teste  +"\n");
        teste = "99534.".matches(lexico.getNumeroReal());
        System.out.print("\n"+  teste  +"\n");
        teste = "99.054".matches(lexico.getNumeroReal());
        System.out.print("\n"+  teste  +"\n");
        teste = ".54499".matches(lexico.getNumeroReal());
        System.out.print("\n"+  teste  +"\n");
        //hard
        teste = "99534..".matches(lexico.getNumeroReal());
        System.out.print("\n"+  teste  +"\n");
        teste = "99..054".matches(lexico.getNumeroReal());
        System.out.print("\n"+  teste  +"\n");
        teste = "..54499".matches(lexico.getNumeroReal());
        System.out.print("\n"+  teste  +"\n");
    }
    
}

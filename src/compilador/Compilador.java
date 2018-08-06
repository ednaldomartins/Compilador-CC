
package compilador;

import business.control.ControllerAnalisadorLexico;
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
        //testeArquivoArray();
        //testeExpressaoRegular();
        boolean t = "vara".matches(Lexico.IDENTIDICADOR);
        System.out.print(t);
        ControllerAnalisadorLexico analisador = new ControllerAnalisadorLexico();
        try {
            analisador.analisar();
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
        analisador.exibirTabela();

  
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
        boolean teste;
        
        //Testando numeros inteiros
        System.out.print("\n\n\n TESTE DE INTEIROS\n");
        teste = "993453".matches(Lexico.NUMEROS_INTEIROS);//true
        System.out.print("\n"+  teste  +"\n");
        teste = "99343.".matches(Lexico.NUMEROS_INTEIROS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "99.075".matches(Lexico.NUMEROS_INTEIROS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = ".99545".matches(Lexico.NUMEROS_INTEIROS);//true
        System.out.print("\n"+  teste  +"\n");
        //hard
        teste = "99.e075".matches(Lexico.NUMEROS_INTEIROS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "e995435".matches(Lexico.NUMEROS_INTEIROS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = ".95345.".matches(Lexico.NUMEROS_INTEIROS);//false
        System.out.print("\n"+  teste  +"\n");
       
        //testando numeros reais
        System.out.print("\n\n\n TESTE DE REAIS\n");
        teste = "995345".matches(Lexico.NUMEROS_REAIS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "99534.".matches(Lexico.NUMEROS_REAIS);//true
        System.out.print("\n"+  teste  +"\n");
        teste = "99.054".matches(Lexico.NUMEROS_REAIS);//true
        System.out.print("\n"+  teste  +"\n");
        teste = ".54499".matches(Lexico.NUMEROS_REAIS);//false
        System.out.print("\n"+  teste  +"\n");
        //hard
        teste = "99534..".matches(Lexico.NUMEROS_REAIS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "99..054".matches(Lexico.NUMEROS_REAIS);//false
        System.out.print("\n"+  teste  +"\n");
        teste = ".54499.".matches(Lexico.NUMEROS_REAIS);//false
        System.out.print("\n"+  teste  +"\n");
        
        //testando comentarios
        System.out.print("\n\n\n TESTE DE COMENTARIOS\n");
        teste = "{}".matches(Lexico.COMENTARIO);//true
        System.out.print("\n"+  teste  +"\n");
        teste = "{asd".matches(Lexico.COMENTARIO);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "{as}d".matches(Lexico.COMENTARIO);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "weq}".matches(Lexico.COMENTARIO);//false
        System.out.print("\n"+  teste  +"\n");
        teste = "w{eq}".matches(Lexico.COMENTARIO);//false
        System.out.print("\n"+  teste  +"\n");
        /*
            a verificacao do fecha comentario "}" deve ficar em tempo de execucao. 
            ex.: quando vier o primeiro "}" o resto vai ser verificado depois, 
                 e dara um erro pq sera considerado uma variavel nao declarada.
        */
        teste = "{;~´sdas232;~´asd,.=-)}a3123;~´}".matches(Lexico.COMENTARIO);//true
        System.out.print("\n"+  teste  +"\n");
    }
    
}

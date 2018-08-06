
package business.model;

/****************************************************************************************************
 * @author marti                                                                                    *
 *  date: 02.08.2018                                                                                *
 *  link: https://www.devmedia.com.br/conceitos-basicos-sobre-expressoes-regulares-em-java/27539    *
 *  link da página ensina como usar expressoes regulares na String. muito Util!                     *
 ****************************************************************************************************/
public class Lexico {
    public static final String PALAVRAS_CHAVES = "program|var|integer|real|boolean|procedure|begin|end|if|then|else|while|do|not";
    public static final String DELIMITADORES = ";|.|:|,|(|)";
    //  [^;=] ===> deixar claro que o '=' de ':=' nao eh operador relacional 
    public static final String OPERADORES_RELACIONAIS = "[=|<|>|<=|>=|<>][^:=]";
    
    public static final String OPERADORES_ADITIVOS = "\\w{0}or\\w{0}";
    public static final String OPERADORES_MULTIPLICATIVOS = "[|*|/|][\\w{0}and\\w{0}]";
    public static final String ATRIBUICAO = ":=";
    //  \\.? ===> encontrar ponto 0 ou 1 vez
    static final String NUMEROS_INTEIROS = "\\.?\\d+";
    //  \\.{1} ===> encontrar ponto exatamento 1 vez entre inteiros (ou no fim) 
    static final String NUMEROS_REAIS = "\\d+\\.{1}\\d*";
    //  [\\w\\W]* ===> palavra e digito, ou simbolo em qualquer ordem
    static final String COMENTARIO = "\\{{1}[\\w\\W]*\\}{1}";
    
    

    //GETs de Strings
    public static String getNumeroInteiro() {
        return NUMEROS_INTEIROS;
    }

    public static String getNumeroReal() {
        return NUMEROS_REAIS;
    }
    
    public static String getAtribuicao() {
        return ATRIBUICAO;
    }

    public static String getComentario() {
        return COMENTARIO;
    }
    
    
 
    //GETs de valores dos vetores
    public static String getPalavraChave(int i) {
        return PALAVRAS_CHAVES[i];
    }

    public static String getDelimitador(int i) {
        return DELIMITADORES[i];
    }

   
    public static String getOperadorMultiplicativo(int i) {
        return OPERADORES_MULTIPLICATIVOS[i];
    }
    
    
    
    //GETs de vetores e variaveis
    public static String[] getPalavrasChaves() {
        return PALAVRAS_CHAVES;
    }

    public static String[] getDelimitadores() {
        return DELIMITADORES;
    }

    public static String[] getOperadoresMultiplicativos() {
        return OPERADORES_MULTIPLICATIVOS;
    }    
    
}

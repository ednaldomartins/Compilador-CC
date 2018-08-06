
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
    public static final String NUMEROS_INTEIROS = "\\.?\\d+";
    //  \\.{1} ===> encontrar ponto exatamento 1 vez entre inteiros (ou no fim) 
    public static final String NUMEROS_REAIS = "\\d+\\.{1}\\d*";
    //  [\\w\\W]* ===> palavra e digito, ou simbolo em qualquer ordem
    public static final String COMENTARIO = "\\{{1}[\\w\\W]*\\}{1}";

    
}

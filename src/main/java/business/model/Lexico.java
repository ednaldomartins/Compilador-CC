
package business.model;

/****************************************************************************************************
 * @author marti                                                                                    *
 *  date: 02.08.2018                                                                                *
 *  link: https://www.devmedia.com.br/conceitos-basicos-sobre-expressoes-regulares-em-java/27539    *
 *  link da página ensina como usar expressoes regulares na String. muito Util!                     *
 ****************************************************************************************************/
public class Lexico
{
    //  comparacao comum a qualquer uma do conjunto de Strings
    public static final String PALAVRAS_CHAVES = "program|var|integer|real|boolean|procedure|begin|end|if|then|else|while|do|not";//OK
    //  \\w+ ===> qual letra ou digito seguinte. O primeiro caracter ja foi confirmado como letra
    public static final String IDENTIDICADOR = "\\_*\\w+[\\_\\w+]*";//OK
    //  ; | . | : | , | ( | ) ===> compara se e igual a alguma das Strings
    public static final String DELIMITADORES = "\\;|\\.|\\:|\\,|\\(|\\)";
    //  [^;=] ===> deixar claro que o '=' de ':=' nao eh operador relacional
    public static final String OPERADORES_RELACIONAIS = "=|<|>|<=|>=|<>";//OK
    //  +|- ===> String igual a + ou a -
    public static final String OPERADORES_ADITIVOS = "[+|-]";//OK
    //  or ===> comparacao comum de Strings
    public static final String OPERADOR_ADITIVO_OR = "or";//OK
    //  *|/ ===> String igual a * ou a /
    public static final String OPERADORES_MULTIPLICATIVOS = "\\*|/";
    //  and ===> and, sem letra ou digito antes e depois
    public static final String OPERADOR_MULTIPLICATIVO_AND = "\\w{0}and\\w{0}";
    //  := ===> comparacao comum de Strings
    public static final String ATRIBUICAO = ":=";//OK
    //  \\.? ===> encontrar ponto 0 ou 1 vez
    public static final String NUMEROS_INTEIROS = "\\d+";//OK
    //  \\.{1} ===> encontrar ponto exatamento 1 vez entre inteiros (ou no fim)
    public static final String NUMEROS_REAIS = "\\d+\\.{1}\\d*";//OK
    //  [\\w\\W]* ===> palavra e digito, ou simbolo em qualquer ordem
    public static final String COMENTARIO = "\\{{1}[\\w\\W]*\\}{1}";//OK

    public static final String COMENTARIO_AULA = "^//[\\w\\W]*";
}


package model;

/****************************************************************************************************
 * @author marti                                                                                    *
 *  date: 02.08.2018                                                                                *
 *  link: https://www.devmedia.com.br/conceitos-basicos-sobre-expressoes-regulares-em-java/27539    *
 *  link da página ensina como usar expressoes regulares na String. muito Util!                     *
 ****************************************************************************************************/
public class Lexico {
    private final String [] palavrasChaves = {"program", "var", "integer", "real", "boolean", "procesure", "begin", "end", "if", "then", "else", "while", "do", "not"};
    private final String [] delimitadores = {";", ".", ":", "(", ")", ","};
    private final String [] operadoresRelacionais = {"=", "<", ">", "<=", ">=", "<>"};
    private final String [] operadoresAditivos = {"+", "-"};
    private final String [] operadoresMultiplicativos = {"*", "/"};
    //  \\.? ===> encontrar ponto 0 ou 1 vez.
    private final String numerosInteiros = "\\.?\\d+";
    //  \\.{1} ===> encontrar ponto exatamento 1 vez entre inteiros 
    private final String numerosReais = "\\d+\\.{1}\\d*";
    private final String atribuicao = ":=";
    //OBS.: por enquanto ta como vetor, mas se nao for necessario criar mais nenhuma expressao regular entao despensar vetor
        
    public Lexico()
    {
    }

    //GETs de Strings
    public String getNumeroInteiro() {
        return numerosInteiros;
    }

    public String getNumeroReal() {
        return numerosReais;
    }
    
    public String getAtribuicao() {
        return atribuicao;
    }
    
    
    //GETs de valores dos vetores
    public String getPalavraChave(int i) {
        return palavrasChaves[i];
    }

    public String getDelimitador(int i) {
        return delimitadores[i];
    }

    public String getOperadorRelacional(int i) {
        return operadoresRelacionais[i];
    }

    public String getOperadorAditivo(int i) {
        return operadoresAditivos[i];
    }

    public String getOperadorMultiplicativo(int i) {
        return operadoresMultiplicativos[i];
    }
    
    
    
    //GETs de vetores e variaveis
    public String[] getPalavrasChaves() {
        return palavrasChaves;
    }

    public String[] getDelimitadores() {
        return delimitadores;
    }

    public String[] getOperadoresRelacionais() {
        return operadoresRelacionais;
    }

    public String[] getOperadoresAditivos() {
        return operadoresAditivos;
    }

    public String[] getOperadoresMultiplicativos() {
        return operadoresMultiplicativos;
    }    
    
}

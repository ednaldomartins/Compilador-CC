
package business.model;

/**
 * @author marti
 * date: 02.08.2018
 */
public class Simbolo {
    private String token;
    private String classificacao;
    private int linha;

    public Simbolo(String token, String classificacao, int linha) {
        this.token = token;
        this.classificacao = classificacao;
        this.linha = linha;
    }

    
    //GETs
    public String getToken() {
        return token;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public int getLinha() {
        return linha;
    }

    //SETs
    public void setToken(String token) {
        this.token = token;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }    
    
}

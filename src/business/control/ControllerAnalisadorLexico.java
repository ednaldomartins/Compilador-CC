
package business.control;

import business.model.Lexico;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import business.model.Simbolo;
import infra.Arquivo;

/**
 * @author marti
 *  date: 04.08.2018
 */
public class ControllerAnalisadorLexico {
    private List <Simbolo> tabela;
    private final List codigo;
    private final String ARQUIVO_ORIGEM = "..\\Compilador\\codigo.txt";

    public ControllerAnalisadorLexico() {
        this.tabela = new LinkedList<>();
        this.codigo = new LinkedList<String>(carregarListaArquivo());
    }
    
    //trocar por switch case mudando algumas coisas
    public void analisar() throws IOException
    {
        int numeroLinhas = codigo.size();
        for(int l = 0; l < numeroLinhas; l++) 
        {            
            String linha = codigo.get(l).toString();
            int tamanhoLinha = linha.length();
            
            for(int i = 0; i < tamanhoLinha; i++) {
                //pula espaco
                if (i+1 <= tamanhoLinha && " ".equals(linha.substring(i, i+1))) {i++;}
                //comentario
                if (i+1 <= tamanhoLinha && "{".equals(linha.substring(i, i+1)))
                {
                    int j = i + 1;
                    while(   j+1 <= tamanhoLinha && (!"}".equals(linha.substring(j, j+1)))   ) {j++;}
                    if (j < tamanhoLinha && linha.substring(i, j+1).matches(Lexico.COMENTARIO))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j+1), "COMENTARIO", l+1 ) );
                        i = j;
                    }         
                }
                
                //operador aditivo "or"
                if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches("or"))
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                    
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_ADITIVO_OR ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_RELACIONAL", l+1 ) );
                        i = j;
                    }
                }
                
                //palavra
                if (i+1 <= tamanhoLinha && Character.isLetter(linha.charAt(i) ))
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                    //palavra reservada
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.PALAVRAS_CHAVES))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "PALAVRA_CHAVE", l+1 ) );
                        i = j;
                    }
                    else //identificador
                        if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.IDENTIDICADOR ) )
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j), "IDENTIFICADOR", l+1 ) );
                            i = j; 
                        }
                }
                
                
                //numeros
                if (i+1 <= tamanhoLinha && Character.isDigit(linha.charAt(i) )  )
                {
                    int j = i + 1;
                    while(   j <= tamanhoLinha && (linha.substring(i, j).matches( Lexico.NUMEROS_INTEIROS ) || linha.substring(i, j).matches( Lexico.NUMEROS_REAIS ) )  ) 
                    {j++;}
                    if (linha.substring(i, j-1).matches( Lexico.NUMEROS_INTEIROS ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_INTEIRO", l+1 ) );
                        i = j;
                    }
                    else if (linha.substring(i, j-1).matches( Lexico.NUMEROS_REAIS ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_REAL", l+1 ) );
                        i = j;
                    }
                }
                
                //operadores relacionais
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.OPERADORES_RELACIONAIS ) )
                {
                    if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches( Lexico.OPERADORES_RELACIONAIS ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+2), "OPERADOR_RELACIONAL", l+1 ) );
                        i += 2;
                    }
                    else if ( !linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_RELACIONAL", l+1 ) );
                    }
                    i++;
                    
                }
                       
                //operadores aditivos
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.OPERADORES_ADITIVOS ) )
                {
                    tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_ADITIVO", l+1 ) );
                    i++;
                }
                
                //atribuição
                if (   i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( ":" ))
                {
                    if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+2), "ATRIBUICAO", l+1 ) );
                        i += 2;
                    }
                }
                
                //delimitadores
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.DELIMITADORES ) )
                {
                    if ( i+2 <= tamanhoLinha && ( linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) || linha.substring(i, i+2).matches( Lexico.NUMEROS_INTEIROS )) )
                    {
                    }
                    else 
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "DELIMITADOR", l+1 ) );
                        i++;
                    }
                }
                
                //operadores multiplicativos
                if (   i+1 <= tamanhoLinha && ( linha.substring(i, i+1).matches( Lexico.OPERADORES_MULTIPLICATIVOS ))  )
                {
                    tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                    i++;
                }
                
                //operador multiplicativo and
                if ( i+3 <= tamanhoLinha && linha.substring(i, i+3).matches("and"))
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_MULTIPLICATIVO_AND ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                        i = j;
                    }
                }
                
            }//fim do for do for de tamanho da linha         
        }// fim do for de numero de linhas   
        String teste="";
    }//fim do metodo

    public void exibirTabela ()
    {
        
    }
    
    
    /**************************************************************************
    *   Metodo para retornar Arquivo de codigo em List                        *                                                           *  
    *   @return List                                                          * 
    ***************************************************************************/
    public List carregarListaArquivo ()  
    {
        try {
            return new Arquivo(ARQUIVO_ORIGEM).carregarCodigo();
        } catch (IOException ex) {
            Logger.getLogger(ControllerAnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

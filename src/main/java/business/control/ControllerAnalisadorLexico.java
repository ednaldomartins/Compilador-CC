
package business.control;

import java.util.List;

import business.model.Lexico;
import business.model.Simbolo;

/**********************************************************************
 * @author marti                                                      *
 *  date: 04.08.2018                                                  *
 *********************************************************************/
public class ControllerAnalisadorLexico {

    public void analisar(List <Simbolo> tabela, List codigo)
    {
        int numeroLinhas = codigo.size();
        for(int l = 0; l < numeroLinhas; l++)
        {
            String linha = codigo.get(l).toString();
            int tamanhoLinha = linha.length();
            for(int i = 0; i < tamanhoLinha;) {
                System.out.println("caracter encontrada na linha" + (l+1) + ">  " +linha.substring(i,i+1) +"  <");
                //pular ESPACO e TAB
                if (   i+1 <= tamanhoLinha && linha.substring(i, i+1).matches(" |\t")   ) {i++;}
                //COMENTARIO + COMENTARIO aplicado em aula
                else if (   i+1 <= tamanhoLinha && ( "{".equals(linha.substring(i, i+1)) || "/".equals(linha.substring(i, i+1)) )   )
                {
                    int j = i + 1;
                    while(   j < tamanhoLinha && (!"}".equals(linha.substring(i, j)))   )
                    {
                        j++;
                    }
                    if (j <= tamanhoLinha && (linha.substring(i, j).matches(Lexico.COMENTARIO) || linha.substring(i, j).matches(Lexico.COMENTARIO_AULA) ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "COMENTARIO", l+1 ) );
                        i = j;
                    }
                    else
                    {
                        System.out.println("erro no comentario");
                        System.exit(0);
                    }
                }
                //PALAVRA
                else if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches("[a-z]*|_*") )
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && ( Character.isLetterOrDigit(linha.charAt(j)) || linha.substring(j, j+1).matches("_") )   ) {j++;}
                    //PALAVRA RESERVADA
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.PALAVRAS_CHAVES))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "PALAVRA_CHAVE", l+1 ) );
                    }
                    //OPERADOR or
                    else if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_ADITIVO_OR ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_ADITIVO", l+1 ) );
                    }
                    //OPERADOR and
                    else if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_MULTIPLICATIVO_AND ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                    }
                    //IDENTIFIDOR
                    else if ( j <= tamanhoLinha && (linha.substring(i, j).matches( Lexico.IDENTIDICADOR )
                            && !(linha.substring(i, j).matches( Lexico.OPERADOR_MULTIPLICATIVO_AND ) && linha.substring(i, j).matches( Lexico.IDENTIDICADOR )) ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "IDENTIFICADOR", l+1 ) );
                    }
                    i = j;
                }
                //NUMERO inteiro ou real
                else if (i < tamanhoLinha && Character.isDigit(linha.charAt(i)) )
                {
                    int j = i + 1;
                    while(   j <= tamanhoLinha && (linha.substring(i, j).matches( Lexico.NUMEROS_INTEIROS ) || linha.substring(i, j).matches( Lexico.NUMEROS_REAIS ) )  ) {j++;}
                    if (linha.substring(i, j-1).matches( Lexico.NUMEROS_INTEIROS ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_INTEIRO", l+1 ) );
                        i = j-1;
                    }
                    else if (linha.substring(i, j-1).matches( Lexico.NUMEROS_REAIS ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_REAL", l+1 ) );
                        i = j-1;
                    }
                }
                //OPERADOR aditivo
                else if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.OPERADORES_ADITIVOS ) )
                {
                    tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_ADITIVO", l+1 ) );
                    i++;
                }
                //OPERADOR relacional
                else if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.OPERADORES_RELACIONAIS ) )
                {
                    if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches( Lexico.OPERADORES_RELACIONAIS ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+2), "OPERADOR_RELACIONAL", l+1 ) );
                        i += 2;
                    }
                    //apagando if ( !linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )
                    else
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_RELACIONAL", l+1 ) );
                        i++;
                    }
                }
                //DELIMITADOR ou ATRIBUICAO
                else if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.DELIMITADORES ) )
                {
                    if (   i+2 <= tamanhoLinha && ( linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )   )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+2), "ATRIBUICAO", l+1 ) );
                        i = i + 2;
                    }
                    else
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "DELIMITADOR", l+1 ) );
                        i++;
                    }
                }
                //OPERADOR multiplicativo
                else if (   i+1 <= tamanhoLinha && ( linha.substring(i, i+1).matches( Lexico.OPERADORES_MULTIPLICATIVOS ))  )
                {
                    tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                    i++;
                }
                //ERRO
                else
                {
                    System.err.println("Erro na linha " +l+ "(" +linha.substring(i,i+1) +")");
                    i++;
                }
            }//fim do for do for de tamanho da linha
        }// fim do for de numero de linhas
        System.out.println("O PROGRAMA PASSOU NO TESTE DO LÉXICO");
    }//fim do metodo

}

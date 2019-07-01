
package infra;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*******************************************************************
 * @author marti                                                   *
 *  date: 02.08.2018                                               *
 ******************************************************************/
public class Arquivo {
    private Path savePath;
    private Charset utf8 = StandardCharsets.UTF_8;
    //construtor para destinar o caminho
    public Arquivo (String savePath) {
        this.savePath = Paths.get(savePath);
    }

    public List carregarCodigo() throws IOException {
        List codigo = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(savePath, utf8)){
            String linha = null;
            while( (linha = reader.readLine()) != null)
                codigo.add( linha.toLowerCase() );
        }
        catch(IOException ex)
        {
            System.err.println("Erro na leitura do arquivo " + savePath.toString());
            String novoArquivo = "codigo"+new Random().nextInt(1000000)+".txt";
            savePath = Paths.get("..//Compilador_CC//docs//codes//"+novoArquivo);
            FileWriter arquivo = new FileWriter(savePath.toString());
            PrintWriter escrita = new PrintWriter(arquivo);
            escrita.print("//Escreva seu código abaixo:");
            escrita.close();
            System.exit(0);
        }

        return codigo;
    }

}

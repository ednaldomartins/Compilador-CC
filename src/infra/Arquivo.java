
package infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author marti
 *  date: 02.08.2018 
 */
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
            while( (linha = reader.readLine()) != null) {
                codigo.add( linha );
            }
            reader.close();         
        }
        catch(IOException ex) {System.err.println("Erro de leitura de arquivo");
            this.savePath = Paths.get("..\\Compilador\\codigo.txt");
            Files.newBufferedWriter(savePath, utf8).write("");
            //ex.printStackTrace();
        }
        
        return codigo;
    }
    
    
    
}

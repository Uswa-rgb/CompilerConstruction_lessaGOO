import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import Pair;

public class Lexar {
    enum Tokens {
        INT, CHAR, // Data types
        DS, WHILE, IOF, FUNC, RET, // Keywords
        AO, RO, IO, ASO, VDO, // Operator types
        NUM, LTC, STR, // Literal types
        BRKT, CMT, PUNCT // Symbol types
    }

    static String openFile(String path) throws IOException{
        if(!path.endsWith(".go")){
            System.out.println("ERROR: Incorrect File Extention. Program exiting.");
            return null;
        }

        String content = Files.readString(Paths.get(path));
        return content;
    }

    static ArrayList<Pair<Tokens,String>> createTokens(BufferedReader fr){
        ArrayList<Pair<Tokens,String>> tokenLexemePairs = new ArrayList<Pair<Tokens,String>>();
        
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Enter path to source file: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String src_path = bf.readLine();
        System.out.println(src_path);

        String fr = openFile(src_path);
        if(fr == null){
            System.exit(1);
        }

        System.out.println(fr);
        
      
    }
}

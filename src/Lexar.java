import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Lexar {

    static BufferedReader openFile(String path) throws IOException{
        if(!path.endsWith(".go")){
            System.out.println("ERROR: Incorrect File Extention. Program exiting.");
            return null;
        }

        BufferedReader fr = new BufferedReader(new FileReader(new File(path)));
        return fr;
    }

    // static ArrayList<Pair<String,String>> createTokens(BufferedReader fr){

    // }

    public static void main(String[] args) throws IOException {
        System.out.println("Enter path to source file: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String src_path = bf.readLine();
        System.out.println(src_path);

        BufferedReader fr = openFile(src_path);
        if(fr == null){
            System.exit(1);
        }
        String temp = new String();
        while((temp = fr.readLine()) != null){
            System.out.println(temp);
        }
        //ArrayList<Pair<String,String>> tokenLexeme = createTokens(fr);
      
    }
}

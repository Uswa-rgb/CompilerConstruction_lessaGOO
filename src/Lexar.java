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
import Tokens;

public class Lexar {

    static String openFile(String path) throws IOException {
        if (!path.endsWith(".go")) {
            System.out.println("ERROR: Incorrect File Extention. Program exiting.");
            return null;
        }

        String content = Files.readString(Paths.get(path));
        return content;
    }

    static ArrayList<Pair<Tokens, String>> createTokens(String fs) {
        ArrayList<Pair<Tokens, String>> tokenLexemePairs = new ArrayList<Pair<Tokens, String>>();
        StringBuilder buffer = new StringBuilder();
        char[] fi = fs.toCharArray();
        int idx = 0;
        int state = 0;
        return null;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Enter path to source file: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String src_path = bf.readLine();
        System.out.println(src_path);

        String fr = openFile(src_path);
        if (fr == null) {
            System.exit(1);
        }
        ArrayList<Pair<Tokens, String>> tokenLexemes = createTokens(fr);
        if (tokenLexemes != null) {
            for (Pair<Tokens, String> pair : tokenLexemes) {
                System.out.println(pair.a + " : " + pair.b);
            }
        }
    }
}

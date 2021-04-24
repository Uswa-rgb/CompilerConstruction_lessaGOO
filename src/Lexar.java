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
import java.util.regex.*;

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
        int line_num = 0;

        while (idx < fs.length()) {
            switch (state) {
            case 0:
                if (Pattern.matches("\\s", "" + fi[idx])) {
                    // TODO: Check for exiting string that might be num, identifier or keyword
                    idx++;
                    state = 0;
                } else if (fi[idx] == '\n') {
                    // TODO: Check for existing string just like above
                    idx++;
                    line_num++;
                    state = 0;
                } else if (Pattern.matches("[\\+\\-\\*/]", "" + fi[idx])) {
                    // Arithematic operator encountered
                    // TODO: Check for existing string again just like above
                    idx++;
                    state = 1;
                } else if (Pattern.matches("[<=>]", "" + fi[idx])) {
                    // Relational operator encountered
                    idx++;
                    state = 2;
                } else if (Pattern.matches("[,;:]", "" + fi[idx])) {
                    // Punctuation symbols
                    idx++;
                    state = 3;
                }
                break;
            case -1:
                break;
            }
        }

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

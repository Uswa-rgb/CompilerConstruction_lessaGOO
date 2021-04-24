import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

public class Lexar {
    public static HashMap<String, Tokens> Keywords;
    static {
        Keywords = new HashMap<>();
        Keywords.put("Integer", Tokens.INT);
        Keywords.put("char", Tokens.CHAR);
        Keywords.put("if", Tokens.DS);
        Keywords.put("elif", Tokens.DS);
        Keywords.put("else", Tokens.DS);
        Keywords.put("while", Tokens.WHILE);
        Keywords.put("In", Tokens.IOF);
        Keywords.put("print", Tokens.IOF);
        Keywords.put("println", Tokens.IOF);
        Keywords.put("func", Tokens.FUNC);
        Keywords.put("ret", Tokens.RET);
    }

    static String openFile(String path) throws IOException {
        if (!path.endsWith(".go")) {
            System.out.println("ERROR: Incorrect File Extention. Program exiting.");
            return null;
        }

        String content = Files.readString(Paths.get(path));
        return content;
    }

    static Tokens isKeyword(String word) {
        return Keywords.get(word);
    }

    static Pair<Tokens, String> handleWord(String word) {
        Tokens token = isKeyword(word);
        if (token != null)
            return new Pair<Tokens, String>(token, word);
        // or identifier
        else if (Pattern.matches("^[a-zA-Z][a-zA-Z0-9]*$", word))
            return new Pair<Tokens, String>(Tokens.ID, word);
        // or integer
        else if (Pattern.matches("^[0-9]*$", word))
            return new Pair<Tokens, String>(Tokens.NUM, word);
        return null;
    }

    static ArrayList<Pair<Tokens, String>> createTokens(String fs) throws Exception {
        ArrayList<Pair<Tokens, String>> tokenLexemePairs = new ArrayList<Pair<Tokens, String>>();
        StringBuilder buffer = new StringBuilder();
        char[] fi = fs.toCharArray();
        int idx = 0;
        int state = 0;
        int line_num = 1;

        while (idx < fs.length()) {
            switch (state) {
            case 0:
                if (Pattern.matches("\\s", "" + fi[idx])) {
                    if (buffer.length() != 0) {
                        Pair<Tokens, String> pair = handleWord(buffer.toString());
                        if (pair == null) {
                            state = -1;
                            System.out.println(
                                    "[!] ERROR: Token does not match keyword, identifier format or numerical constant.");
                            break;
                        }
                        tokenLexemePairs.add(pair);
                        buffer.setLength(0);
                    }
                    idx++;
                    state = 0;

                } else if (fi[idx] == '\n') {
                    if (buffer.length() != 0) {
                        Pair<Tokens, String> pair = handleWord(buffer.toString());
                        if (pair == null) {
                            state = -1;
                            System.out.println(
                                    "[!] ERROR: Token does not match keyword, identifier format or numerical constant.");
                            break;
                        }
                        tokenLexemePairs.add(pair);
                        buffer.setLength(0);
                    }
                    idx++;
                    line_num++;
                    state = 0;

                } else if (Character.isLetter(fi[idx]) || Character.isDigit(fi[idx])) {
                    // letter or digit encountered
                    buffer.append(fi[idx]);
                    idx++;
                    state = 0;

                } else if (Pattern.matches("[\\+\\-\\*/]", "" + fi[idx])) {
                    // Arithematic operator encountered
                    if (buffer.length() != 0) {
                        Pair<Tokens, String> pair = handleWord(buffer.toString());
                        if (pair == null) {
                            state = -1;
                            System.out.println(
                                    "[!] ERROR: Token does not match keyword, identifier format or numerical constant.");
                            break;
                        }
                        tokenLexemePairs.add(pair);
                        buffer.setLength(0);
                    }
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
                } else if (Pattern.matches("regex", "" + fi[idx])) {
                    // Parenthesis / Brackets / Squares
                    idx++;
                    state = 4;
                } else if (Pattern.matches("[\'\"]", "" + fi[idx])) {
                    // single quote / double quote
                    idx++;
                    state = 5;
                } else {
                    // System.out.println();
                    state = -1;
                }
                break;
            case 1:
                // check against all arithematic operators and handle edge cases with other
                // kinds of operators
                if (fi[idx - 1] == '+')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.AO, "+"));
                else if (fi[idx - 1] == '-')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.AO, "-"));
                else if (fi[idx - 1] == '/') {
                    if (fi[idx] == '=') // not equal to case
                    {
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.RO, "/="));
                        idx++;
                    } else if (fi[idx] == '*') // start of string case
                    {
                        // TODO: skip the entire comment
                    } else // simple division case
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.AO, "/"));
                } else if (fi[idx - 1] == '*') {
                    if (fi[idx] == '/') {
                        // abandoned end of comment marker, throw error
                        state = -1;
                        System.out.println("[!] ERROR: Lone end of comment marker encountere */.");
                        break;
                    }
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokesn.AO, "*"));
                }
                state = 0;
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case -1:
                throw new Exception("[!] ERROR: Unknown Token encountered: " + buffer.toString() + " " + fi[idx]
                        + " on line: " + line_num);
            }
        }
        return tokenLexemePairs;
    }

    public static void main(String[] args) throws Exception {
        Lexar lex = new Lexar();
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

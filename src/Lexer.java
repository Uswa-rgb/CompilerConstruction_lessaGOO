import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

public class Lexer {
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

                } else if (fi[idx] == '\n' || fi[idx] == '\r') {
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
                    line_num += 1;
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
                    state = 2;

                } else if (Pattern.matches("[,;:]", "" + fi[idx])) {
                    // Punctuation symbols
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
                    state = 3;

                } else if (Pattern.matches("[\\[\\](){}]", "" + fi[idx])) {
                    // Parenthesis / Brackets / Squares
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
                    state = 4;

                } else if (Pattern.matches("[\'\"]", "" + fi[idx])) {
                    // single quote / double quote
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
                    state = 5;

                } else {
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
                    } else if (fi[idx] == '*') // start of comment case
                    {
                        int nidx = fs.indexOf("*/", idx);
                        if (nidx == -1) {
                            System.out.println("[!] Error: Dangling Comment, no end.");
                            state = -1;
                            break; // */ a
                        }
                        idx = nidx + 2;
                    } else // simple division case
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.AO, "/"));
                } else if (fi[idx - 1] == '*') {
                    if (fi[idx] == '/') {
                        // abandoned end of comment marker, throw error
                        state = -1;
                        System.out.println("[!] ERROR: Lone end of comment marker encountere */.");
                        break;
                    }
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.AO, "*"));
                }
                state = 0;
                break;

            case 2:
                if (fi[idx - 1] == '=')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.RO, "="));
                else if (fi[idx - 1] == '<') {
                    if (fi[idx] == '=') {
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.RO, "<="));
                        idx++;
                    } else
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.RO, "<"));
                } else if (fi[idx - 1] == '>') {
                    if (fi[idx] == '>') // input operator case
                    {
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.IO, ">>"));
                        idx++;
                    } else if (fi[idx] == '=') {
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.RO, ">="));
                        idx++;
                    } else
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.RO, ">"));
                }
                state = 0;
                break;
            case 3:
                if (fi[idx - 1] == ',')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.PUNCT, ","));
                else if (fi[idx - 1] == ';')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.PUNCT, ";"));
                else if (fi[idx - 1] == ':') {
                    if (fi[idx] == '=') { // variable assingment operator case
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.ASO, ":="));
                        idx++;
                    } else
                        tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.PUNCT, ":"));
                }
                state = 0;
                break;
            case 4:
                if (fi[idx - 1] == '[')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, "["));
                else if (fi[idx - 1] == ']')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, "]"));
                else if (fi[idx - 1] == '{')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, "{"));
                else if (fi[idx - 1] == '}')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, "}"));
                else if (fi[idx - 1] == '(')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, "("));
                else if (fi[idx - 1] == ')')
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, ")"));
                state = 0;
                break;
            case 5:
                if (fi[idx - 1] == '\'') { // char z = 'b'
                    if (fi[idx + 1] != '\'') { // character literal exceeding 1 character
                        System.out.println("[!] ERROR: Character literal exceeding 1 character size.");
                        state = -1;
                        break;
                    }
                    tokenLexemePairs
                            .add(new Pair<Tokens, String>(Tokens.LTC, "" + fi[idx - 1] + fi[idx] + fi[idx + 1]));
                    idx += 2;
                } else if (fi[idx - 1] == '\"') {
                    int nidx = fs.indexOf("\"", idx);
                    if (nidx == -1) {
                        System.out.println("[!] Error: String Literal imporper termination.");
                        state = -1;
                        break;
                    }
                    tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.STR, fs.substring(idx - 1, nidx + 1)));
                    idx = nidx + 1;
                }
                state = 0;
                break;
            case -1:
                throw new Exception("[!] ERROR: Unknown Token encountered: " + buffer.toString() + " " + fi[idx]
                        + " on line: " + line_num);
            }
        }
        if(fi[idx-1] == '}')
            tokenLexemePairs.add(new Pair<Tokens, String>(Tokens.BRKT, "}"));
        System.out.println("[!] Processing complete. Processed Lines: ");
        return tokenLexemePairs;
    }

    public void lexicalAnalysis(String path, String destination) throws Exception{
        String fr = openFile(path);
        if (fr == null) {
            System.exit(1);
        }
        ArrayList<Pair<Tokens, String>> tokenLexemes = createTokens(fr);
        if (tokenLexemes != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destination)));
            for (Pair<Tokens, String> pair : tokenLexemes) {
                writer.write("(" + pair.a + ", " + pair.b + ")\n");
                // System.out.println(pair.a + "\t->\t" + pair.b);
            }
            writer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        // THIS IS JUST FOR TESTING
        System.out.println("Enter path to source file: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String src_path = bf.readLine();

        Lexer lex = new Lexer();
        lex.lexicalAnalysis(src_path, "./words.txt");

    }
}

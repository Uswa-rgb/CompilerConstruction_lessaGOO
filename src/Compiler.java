import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Compiler {
    public static void main(String[] args) throws Exception{
        // User input for input file path
        System.out.println("[++] Starting Lexical Analysis");
        System.out.println("Enter path to source file: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String src_path = bf.readLine();
        String dest_path = "./words.txt";

        // Lexical Analysis
        Lexer lex = new Lexer();
        lex.lexicalAnalysis(src_path, dest_path);
        System.out.println("[++] Lexical Analysis Completed");

        // Recursive Descent Parsing
        System.out.println("[++] Parser Starting");
        Parser p = new Parser();
        p.openWordsFile(dest_path);
        p.look = p.nextToken();
        p.Program();
        if(p.look != null){
            System.out.println("[!!] ERROR: Parser terminated without exhausting tokens.");
            System.exit(1);
        }
        else{
            System.out.println("[++] Parser terminated successfully.");
            p.dumpSymbolTable();
            System.out.println("[++] Symbol table dumped.");
            p.dump3AddCode();
            System.out.println("[++] Three Address Code dumped");
            p.dumpMachineCode();
            System.out.println("[++] Machine Code dumped");
            System.exit(0);
        }

    }
}

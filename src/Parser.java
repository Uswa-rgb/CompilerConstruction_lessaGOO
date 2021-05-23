import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

public class Parser {
    ArrayList<Pair<Tokens, String>> tokens;
    HashMap<String, Tokens> symbolTable;
    BufferedWriter treBufferedWriter;
    BufferedReader wordFileReader;
    Pair<Tokens, String> look;
    Tokens curr_dt;
    int tokenIdx;
    int tabs;

    Parser() {
        try {

            this.tabs = 0;
            this.tokens = new ArrayList<Pair<Tokens, String>>();
            this.tokenIdx = 0;
            this.symbolTable = new HashMap<>();
            this.treBufferedWriter = new BufferedWriter(new FileWriter(new File("./parse_tree.txt")));
        } catch (Exception e) {
            System.out.println("[Parser - Init] Exception Occurred.");
            System.out.println(e);
        }
    }

    public void dumpSymbolTable(){
        try{
            FileWriter st = new FileWriter("./symbol_table.txt");
            st.write("Identifier\tData Type\n");
            this.symbolTable.forEach((k, v) -> {
                try {
                    st.write(k + "\t" + v + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            st.close();
        }
        catch(IOException e){
            System.out.println("[Parser-DST] Exception Occurred.");
            System.out.println(e);
        }
    }

    public void emit(String str) {
        try {

            String buffer = new String();
            for (int i = 0; i < this.tabs - 1; i++)
                buffer += ": ";
            buffer = buffer + "|__" + str + "\n";

            // printing to screen
            System.out.print(buffer);
            // writing to file
            this.treBufferedWriter.write(buffer);
            this.treBufferedWriter.flush();
        } catch (Exception e) {
            System.out.println("[Parser-emit] Exception occurred.");
            System.out.println(e);
        }
    }

    public void openWordsFile(String path) throws Exception {
        wordFileReader = new BufferedReader(new FileReader(new File(path)));
        String buffer;
        while ((buffer = wordFileReader.readLine()) != null && buffer.length() != 0) {
            int comma = buffer.indexOf(',', 1);
            String first = buffer.substring(1, comma);
            String second = buffer.substring(comma + 2, buffer.length() - 1);
            this.tokens.add(new Pair<Tokens, String>(Tokens.valueOf(first), second));
        }
    }

    public Pair<Tokens, String> nextToken() throws Exception {
        if (this.tokenIdx < this.tokens.size())
            return this.tokens.get(tokenIdx++);
        return null;
    }

    public void match(Tokens token) {
        try {
            if (look.a == token)
                look = nextToken();
            else
                throw new Exception("MATCH FAILED: unknown or invalid token in look.a");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("[match]: Exception occured: invalid token -> " + look.a + "\texpected -> " + token);
            System.exit(1);
        }
    }

    public void match(Tokens token, String lexeme) {
        try {

            if (look.a == token && look.b.equals(lexeme))
                look = nextToken();
            else
                throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in look");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("[match]: Exception occured: invalid token -> " + look.a + " lexeme -> " + look.b
                    + "\texpected token -> " + token + " expected lemexe -> " + lexeme);
            System.exit(1);
        }
    }

    // CFG Functions begin bere
    public void Program() throws Exception {
        // Program -> FS Program | NULL
        if (this.look != null && this.look.a == Tokens.FUNC) {
            this.tabs++;
            this.emit("FS()");
            this.FS();
            this.emit("Program()");
            this.Program();
            this.tabs--;
        }
    }

    public void LE() throws Exception {
        this.tabs++;
        this.emit("LID()");
        this.LID();
        this.emit("LO()");
        this.LO();
        this.emit("LID()");
        this.LID();
        this.tabs--;
    }

    public void LID() throws Exception {
        this.tabs++;
        if (this.look.b.equals("true")) {
            this.emit("DS -> true");
            this.match(Tokens.DS, "true");
        } else if (this.look.b.equals("false")) {
            this.emit("DS -> false");
            this.match(Tokens.DS, "false");
        } else if (this.look.a.equals(Tokens.ID)) {
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
        } else if (this.look.a.equals(Tokens.NUM)) {
            this.emit("NUM -> " + this.look.b);
            this.match(Tokens.NUM);
        } else if (this.look.a.equals(Tokens.LTC)) {
            this.emit("LTC -> " + this.look.b);
            this.match(Tokens.LTC);
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in LID");
        this.tabs--;
    }

    public void LO() throws Exception {
        this.tabs++;
        if (this.look.b.equals("<")) {
            this.emit("RO -> " + this.look.b);
            this.match(Tokens.RO, "<");
        } else if (this.look.b.equals("<=")) {
            this.emit("RO -> " + this.look.b);
            this.match(Tokens.RO, "<=");
        } else if (this.look.b.equals(">")) {
            this.emit("RO -> " + this.look.b);
            this.match(Tokens.RO, ">");
        } else if (this.look.b.equals(">=")) {
            this.emit("RO -> " + this.look.b);
            this.match(Tokens.RO, ">=");
        } else if (this.look.b.equals("=")) {
            this.emit("RO -> " + this.look.b);
            this.match(Tokens.RO, "=");
        } else if (this.look.b.equals("/=")) {
            this.emit("RO -> " + this.look.b);
            this.match(Tokens.RO, "/=");
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in LO");
        this.tabs--;
    }

    public void AE() throws Exception {
        this.tabs++;
        this.emit("EB()");
        this.EB();
        this.emit("R()");
        this.R();
        this.tabs--;
    }

    public void R() throws Exception {
        this.tabs++;
        if (this.look.b.equals("+")) {
            this.emit("AO -> " + this.look.b);
            this.match(Tokens.AO, "+");
            this.emit("EB()");
            this.EB();
            this.emit("R()");
            this.R();
        } else if (this.look.b.equals("-")) {
            this.emit("AO -> " + this.look.b);
            this.match(Tokens.AO, "-");
            this.emit("EB()");
            this.EB();
            this.emit("R()");
            this.R();
        }
        this.tabs--;
    }

    public void EB() throws Exception {
        this.tabs++;
        this.emit("EC()");
        this.EC();
        this.emit("R1()");
        this.R1();
        this.tabs--;
    }

    public void R1() throws Exception {
        this.tabs++;
        if (this.look.b.equals("*")) {
            this.emit("AO -> " + this.look.b);
            this.match(Tokens.AO, "*");
            this.emit("EC()");
            this.EC();
            this.emit("R1()");
            this.R1();
        } else if (this.look.b.equals("/")) {
            this.emit("AO -> " + this.look.b);
            this.match(Tokens.AO, "/");
            this.emit("EC()");
            this.EC();
            this.emit("R1()");
            this.R1();
        }
        this.tabs--;
    }

    public void EC() throws Exception {
        this.tabs++;
        if (this.look.a.equals(Tokens.ID)) {
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
        } else if (this.look.a.equals(Tokens.NUM)) {
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.NUM);
        } else if (this.look.b.equals("(")) {
            this.emit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "(");
            this.emit("AE()");
            this.AE();
            this.emit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, ")");
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in EC");
        this.tabs--;
    }

    public void VDS() throws Exception {
        this.tabs++;
        this.emit("VT()");
        this.VT();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.emit("VAR()");
        this.VAR();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
        this.curr_dt = null;
    }

    public void VAR() throws Exception {
        this.tabs++;
        this.emit("ID -> " + this.look.b);
        this.symbolTable.put(this.look.b, this.curr_dt);
        this.match(Tokens.ID);
        this.emit("VAR1()");
        this.VAR1();
        this.tabs--;
    }

    public void VAR1() throws Exception {
        this.tabs++;
        if (this.look.b.equals(",")) {
            this.emit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ",");
            this.emit("ID -> " + this.look.b);
            this.symbolTable.put(this.look.b, this.curr_dt);
            this.match(Tokens.ID);
            this.emit("VAR1()");
            this.VAR1();
        }
        this.tabs--;
    }

    public void VT() throws Exception {
        this.tabs++;
        if (this.look.a.equals(Tokens.INT)) {
            this.emit("INT -> " + this.look.b);
            this.match(Tokens.INT);
            this.curr_dt = Tokens.INT;
        } else if (this.look.a.equals(Tokens.CHAR)) {
            this.emit("CHAR -> " + this.look.b);
            this.match(Tokens.CHAR);
            this.curr_dt = Tokens.CHAR;
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in VT");
        this.tabs--;
    }

    public void VAS() throws Exception {
        this.tabs++;
        this.emit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.emit("ASO -> " + this.look.b);
        this.match(Tokens.ASO, ":=");
        this.emit("VAL()");
        this.VAL();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void VAL() throws Exception {
        this.tabs++;
        if (this.look.a.equals(Tokens.NUM) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))) {
            this.emit("NUM -> " + this.look.b);
            this.match(Tokens.NUM);
        } else if (this.look.a.equals(Tokens.LTC)) {
            this.emit("LTC -> " + this.look.b);
            this.match(Tokens.LTC);
        } else if (this.look.a.equals(Tokens.ID) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))
                && !(this.tokens.get(tokenIdx).b.equals("("))) {
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.AO)) {
            this.emit("AE()");
            this.AE();
        } else if (this.look.a.equals(Tokens.ID) && this.tokens.get(tokenIdx).b.equals("(")) {
            this.emit("FC()");
            this.FC();
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in VT");
        this.tabs--;
    }

    public void PS() throws Exception {
        this.tabs++;
        this.emit("PK()");
        this.PK();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "(");
        this.emit("DATA()");
        this.DATA();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, ")");
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void PK() throws Exception {
        this.tabs++;
        if (this.look.b.equals("print")) {
            this.emit("IOF -> " + this.look.b);
            this.match(Tokens.IOF, "print");
        } else if (this.look.b.equals("println")) {
            this.emit("IOF -> " + this.look.b);
            this.match(Tokens.IOF, "println");
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in PK");
        this.tabs--;
    }

    public void DATA() throws Exception {
        this.tabs++;
        if (this.look.a.equals(Tokens.ID) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))) {
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
        } else if (this.look.a.equals(Tokens.NUM) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))) {
            this.emit("NUM -> " + this.look.b);
            this.match(Tokens.NUM);
        } else if (this.look.a.equals(Tokens.LTC)) {
            this.emit("LTC -> " + this.look.b);
            this.match(Tokens.LTC);
        } else if (this.look.a.equals(Tokens.STR)) {
            this.emit("STR -> " + this.look.b);
            this.match(Tokens.STR);
        } else {
            this.emit("AE()");
            this.AE();
        }
        this.tabs--;
    }

    public void INS() throws Exception {
        this.tabs++;
        this.emit("IOF -> " + this.look.b);
        this.match(Tokens.IOF, "In");
        this.emit("IO -> " + this.look.b);
        this.match(Tokens.IO);
        this.emit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void LS() throws Exception {
        this.tabs++;
        this.emit("WHILE -> " + this.look.b);
        this.match(Tokens.WHILE);
        this.emit("LE()");
        this.LE();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "{");
        this.emit("CB()");
        this.CB();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "}");
        this.tabs--;
    }

    public void CS() throws Exception {
        this.tabs++;
        this.emit("DS -> " + this.look.b);
        this.match(Tokens.DS, "if");
        this.emit("LE()");
        this.LE();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "{");
        this.emit("CB()");
        this.CB();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "}");
        this.emit("CE()");
        this.CE();
        this.tabs--;
    }

    public void CE() throws Exception {
        this.tabs++;
        if (look.b.equals("elif")) {
            this.emit("DS -> " + this.look.b);
            this.match(Tokens.DS, "elif");
            this.emit("LE()");
            this.LE();
            this.emit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ":");
            this.emit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "{");
            this.emit("CB()");
            this.CB();
            this.emit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "}");
            this.emit("CE()");
            this.CE();
        } else if (look.b.equals("else")) {
            this.emit("DS -> " + this.look.b);
            this.match(Tokens.DS, "else");
            this.emit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "{");
            this.emit("CB()");
            this.CB();
            this.emit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "}");
        }
        this.tabs--;
    }

    public void FS() throws Exception {
        this.tabs++;
        this.emit("FUNC -> " + this.look.b);
        this.match(Tokens.FUNC);
        this.emit("VT()");
        this.VT();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.emit("ID -> " + this.look.b);
        this.symbolTable.put(this.look.b, Tokens.FUNC);
        this.match(Tokens.ID);
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "(");
        this.emit("VT()");
        this.VT();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.emit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.emit("PR()");
        this.PR();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, ")");
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "{");
        this.emit("CB()");
        this.CB();
        this.emit("RE()");
        this.RE();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "}");
        this.tabs--;
    }

    public void PR() throws Exception {
        this.tabs++;
        if (this.look.b.equals(",")) {
            this.emit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ",");
            this.emit("VT()");
            this.VT();
            this.emit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ":");
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
            this.emit("PR()");
            this.PR();
        }
        this.tabs--;
    }

    public void RE() throws Exception {
        this.tabs++;
        this.emit("RET -> " + this.look.b);
        this.match(Tokens.RET);
        this.emit("RS()");
        this.RS();
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void RS() throws Exception {
        this.tabs++;
        if (look.a.equals(Tokens.ID)) {
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
        } else if (look.a.equals(Tokens.NUM)) {
            this.emit("NUM -> " + this.look.b);
            this.match(Tokens.NUM);
        } else if (look.a.equals(Tokens.LTC)) {
            this.emit("LTC -> " + this.look.b);
            this.match(Tokens.LTC);
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in look, encountered at RS()");
        this.tabs--;
    }

    public void CB() throws Exception {
        this.tabs++;
        if (this.look != null && !this.look.b.equals("}") && !this.look.a.equals(Tokens.RET)) {
            this.emit("ST()");
            this.ST();
            this.emit("CB()");
            this.CB();
        }
        this.tabs--;
    }

    public void FC() throws Exception {
        this.tabs++;
        this.emit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.emit("BRKT-> " + this.look.b);
        this.match(Tokens.BRKT, "(");
        this.emit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.emit("FR()");
        this.FR();
        this.emit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, ")");
        this.emit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void FR() throws Exception {
        this.tabs++;
        if (this.look.b.equals(",")) {
            this.emit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ",");
            this.emit("ID -> " + this.look.b);
            this.match(Tokens.ID);
            this.emit("FR()");
            this.FR();
        }
        this.tabs--;
    }

    public void ST() throws Exception {
        this.tabs++;
        if (this.tokens.get(tokenIdx).a.equals(Tokens.AO)) {
            this.emit("AE()");
            this.AE();
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.RO)) {
            this.emit("LE()");
            this.LE();
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.PUNCT)) {
            this.emit("VDS()");
            this.VDS();
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.ASO)) {
            this.emit("VAS()");
            this.VAS();
        } else if (this.look.b.matches("print(ln){0,1}")) {
            this.emit("PS()");
            this.PS();
        } else if (this.look.b.equals("In")) {
            this.emit("INS()");
            this.INS();
        } else if (this.look.a.equals(Tokens.WHILE)) {
            this.emit("LS()");
            this.LS();
        } else if (this.look.b.equals("if")) {
            this.emit("CS()");
            this.CS();
        } else if (this.tokens.get(tokenIdx).b.equals("(")) {
            this.emit("FC()");
            this.FC();
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in look, encountered at ST()");
        this.tabs--;
    }

    // Main for testing begins here
    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.openWordsFile("./words.txt");
        p.look = p.nextToken();
        p.Program();

    }
}
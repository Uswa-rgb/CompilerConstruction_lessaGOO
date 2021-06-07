import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.StampedLock;
import java.io.File;

public class Parser {
    ArrayList<Pair<Tokens, String>> tokens;
    ArrayList<StringBuilder> _3AC;
    HashMap<String, Pair<Tokens, Integer>> symbolTable;
    BufferedWriter treBufferedWriter;
    BufferedReader wordFileReader;
    Pair<Tokens, String> look;
    Boolean toPrint = true;
    Tokens curr_dt;
    int tokenIdx;
    int temp;
    int nextVar;
    int lines;
    int tabs;

    Parser() {
        try {
            this.tabs = 0;
            this.lines = 1;
            this.tokenIdx = 0;
            this.nextVar = 0;
            this.temp = 1;
            this._3AC = new ArrayList<>();
            this.symbolTable = new HashMap<>();
            this.tokens = new ArrayList<Pair<Tokens, String>>();
            this.treBufferedWriter = new BufferedWriter(new FileWriter(new File("./parse_tree.txt")));
        } catch (Exception e) {
            System.out.println("[Parser - Init] Exception Occurred.");
            System.out.println(e);
        }
    }

    public String nextTemp() {
        return "T" + this.temp++;
    }

    public void dumpSymbolTable() {
        try {
            StringBuilder buffer = new StringBuilder();
            FileWriter st = new FileWriter("./symbol_table.txt");
            st.write("Identifier\tData Type\n");
            this.symbolTable.forEach((k, p) -> {
                try {
                    buffer.append(k);
                    for (int i = 0; i < 10 - k.length(); i++)
                        buffer.append(" ");
                    buffer.append(p.a);
                    for (int i = 0; i < 10 - p.a.toString().length(); i++)
                        buffer.append(" ");
                    buffer.append(p.b).append("\n");
                    st.write(buffer.toString());
                    buffer.setLength(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            st.close();
        } catch (IOException e) {
            System.out.println("[Parser-DST] Exception Occurred.");
            System.out.println(e);
        }
    }

    public void dump3AddCode() {
        try {
            FileWriter st = new FileWriter("./tac.txt");
            this._3AC.forEach((s) -> {
                try {
                    st.write(s.toString() + "\n");
                } catch (IOException e) {
                    System.out.println("[Parser-D3AC] Exception Occurred.");
                    System.out.println(e);
                }
            });
            st.close();
        } catch (IOException e) {
            System.out.println("[Parser-D3AC] Exception Occurred.");
            System.out.println(e);
        }
    }

    public void backpatch(int src, int dest) {
        this._3AC.get(src - 1).append(" ").append(dest);
    }

    public void TAEmit(String str) {
        // emit code
        this._3AC.add(new StringBuilder(str));
        this.lines++;
    }

    public void STEmit(String str) {
        try {

            String buffer = new String();
            for (int i = 0; i < this.tabs - 1; i++)
                buffer += ": ";
            buffer = buffer + "|__" + str + "\n";

            // printing to screen
            if (this.toPrint)
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
            this.STEmit("FS()");
            this.FS();
            this.STEmit("Program()");
            this.Program();
            this.tabs--;
        }
    }

    public String LE() throws Exception {
        StringBuilder expression = new StringBuilder();
        this.tabs++;
        this.STEmit("LID()");
        expression.append(this.LID());
        this.STEmit("LO()");
        expression.append(this.LO());
        this.STEmit("LID()");
        expression.append(this.LID());
        this.tabs--;
        return expression.toString();
    }

    public String LID() throws Exception {
        String ret = new String();
        this.tabs++;
        if (this.look.b.equals("true")) {
            this.STEmit("DS -> true");
            this.match(Tokens.DS, "true");
            ret = "true";
        } else if (this.look.b.equals("false")) {
            this.STEmit("DS -> false");
            this.match(Tokens.DS, "false");
            ret = "false";
        } else if (this.look.a.equals(Tokens.ID)) {
            this.STEmit("ID -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.ID);
        } else if (this.look.a.equals(Tokens.NUM)) {
            this.STEmit("NUM -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.NUM);
        } else if (this.look.a.equals(Tokens.LTC)) {
            this.STEmit("LTC -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.LTC);
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in LID");
        this.tabs--;
        return ret;
    }

    public String LO() throws Exception {
        String ret = new String();
        this.tabs++;
        if (this.look.b.equals("<")) {
            this.STEmit("RO -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.RO, "<");
        } else if (this.look.b.equals("<=")) {
            this.STEmit("RO -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.RO, "<=");
        } else if (this.look.b.equals(">")) {
            this.STEmit("RO -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.RO, ">");
        } else if (this.look.b.equals(">=")) {
            this.STEmit("RO -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.RO, ">=");
        } else if (this.look.b.equals("=")) {
            this.STEmit("RO -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.RO, "=");
        } else if (this.look.b.equals("/=")) {
            this.STEmit("RO -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.RO, "/=");
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in LO");
        this.tabs--;
        return ret;
    }

    public String AE() throws Exception {
        this.tabs++;
        this.STEmit("EB()");
        String op1 = this.EB();
        this.STEmit("R()");
        String ans = this.R(op1);
        this.tabs--;
        return ans;
    }

    public String R(String op1) throws Exception {
        this.tabs++;
        String op2;
        String temp;
        String ans;
        if (this.look.b.equals("+")) {
            this.STEmit("AO -> " + this.look.b);
            this.match(Tokens.AO, "+");
            this.STEmit("EB()");
            op2 = this.EB();
            temp = this.nextTemp();
            this.TAEmit(temp + " = " + op1 + " + " + op2);
            this.STEmit("R()");
            ans = this.R(temp);
        } else if (this.look.b.equals("-")) {
            this.STEmit("AO -> " + this.look.b);
            this.match(Tokens.AO, "-");
            this.STEmit("EB()");
            op2 = this.EB();
            temp = this.nextTemp();
            this.TAEmit(temp + " = " + op1 + " - " + op2);
            this.STEmit("R()");
            ans = this.R(temp);
        } else
            ans = op1;
        this.tabs--;
        return ans;
    }

    public String EB() throws Exception {
        this.tabs++;
        this.STEmit("EC()");
        String op1 = this.EC();
        this.STEmit("R1()");
        String ans = this.R1(op1);
        this.tabs--;
        return ans;
    }

    public String R1(String op1) throws Exception {
        this.tabs++;
        String op2;
        String temp;
        String ans;
        if (this.look.b.equals("*")) {
            this.STEmit("AO -> " + this.look.b);
            this.match(Tokens.AO, "*");
            this.STEmit("EC()");
            op2 = this.EC();
            temp = this.nextTemp();
            this.TAEmit(temp + " = " + op1 + " * " + op2);
            this.STEmit("R1()");
            ans = this.R1(temp);
        } else if (this.look.b.equals("/")) {
            this.STEmit("AO -> " + this.look.b);
            this.match(Tokens.AO, "/");
            this.STEmit("EC()");
            op2 = this.EC();
            temp = this.nextTemp();
            this.TAEmit(temp + " = " + op1 + " / " + op2);
            this.STEmit("R1()");
            ans = this.R1(temp);
        } else
            ans = op1;
        this.tabs--;
        return ans;
    }

    public String EC() throws Exception {
        String ret;
        this.tabs++;
        if (this.look.a.equals(Tokens.ID)) {
            this.STEmit("ID -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.ID);
        } else if (this.look.a.equals(Tokens.NUM)) {
            this.STEmit("ID -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.NUM);
        } else if (this.look.b.equals("(")) {
            this.STEmit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "(");
            this.STEmit("AE()");
            ret = this.AE();
            this.STEmit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, ")");
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in EC");
        this.tabs--;
        return ret;
    }

    public void VDS() throws Exception {
        this.tabs++;
        this.STEmit("VT()");
        this.VT();
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.STEmit("VAR()");
        this.VAR();
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
        this.curr_dt = null;
    }

    public void VAR() throws Exception {
        this.tabs++;
        this.STEmit("ID -> " + this.look.b);
        this.symbolTable.put(this.look.b, new Pair<Tokens, Integer>(this.curr_dt, this.nextVar));
        if (this.curr_dt == Tokens.INT)
            this.nextVar += 4;
        else if (this.curr_dt == Tokens.CHAR)
            this.nextVar += 1;
        this.match(Tokens.ID);
        this.STEmit("VAR1()");
        this.VAR1();
        this.tabs--;
    }

    public void VAR1() throws Exception {
        this.tabs++;
        if (this.look.b.equals(",")) {
            this.STEmit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ",");
            this.STEmit("ID -> " + this.look.b);
            this.symbolTable.put(this.look.b, new Pair<Tokens, Integer>(this.curr_dt, this.nextVar));
            if (this.curr_dt == Tokens.INT)
                this.nextVar += 4;
            else if (this.curr_dt == Tokens.CHAR)
                this.nextVar += 1;
            this.match(Tokens.ID);
            this.STEmit("VAR1()");
            this.VAR1();
        }
        this.tabs--;
    }

    public void VT() throws Exception {
        this.tabs++;
        if (this.look.a.equals(Tokens.INT)) {
            this.STEmit("INT -> " + this.look.b);
            this.match(Tokens.INT);
            this.curr_dt = Tokens.INT;
        } else if (this.look.a.equals(Tokens.CHAR)) {
            this.STEmit("CHAR -> " + this.look.b);
            this.match(Tokens.CHAR);
            this.curr_dt = Tokens.CHAR;
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in VT");
        this.tabs--;
    }

    public void VAS() throws Exception {
        StringBuilder stmt = new StringBuilder();
        this.tabs++;
        this.STEmit("ID -> " + this.look.b);
        stmt.append(this.look.b).append(" = ");
        this.match(Tokens.ID);
        this.STEmit("ASO -> " + this.look.b);
        this.match(Tokens.ASO, ":=");
        this.STEmit("VAL()");
        stmt.append(this.VAL());
        this.TAEmit(stmt.toString());
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public String VAL() throws Exception {
        String ret;
        this.tabs++;
        if (this.look.a.equals(Tokens.NUM) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))) {
            this.STEmit("NUM -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.NUM);
        } else if (this.look.a.equals(Tokens.LTC)) {
            this.STEmit("LTC -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.LTC);
        } else if (this.look.a.equals(Tokens.ID) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))
                && !(this.tokens.get(tokenIdx).b.equals("("))) {
            this.STEmit("ID -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.ID);
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.AO)) {
            this.STEmit("AE()");
            ret = this.AE();
        } else if (this.look.a.equals(Tokens.ID) && this.tokens.get(tokenIdx).b.equals("(")) {
            this.STEmit("FC()");
            ret = "";
            this.FC();
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in VT");
        this.tabs--;
        return ret;
    }

    public void PS() throws Exception {
        StringBuilder stmnt = new StringBuilder();
        this.tabs++;
        this.STEmit("PK()");
        boolean isPrintln = this.PK();
        stmnt.append("OUT ");
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "(");
        this.STEmit("DATA()");
        stmnt.append(this.DATA());
        this.TAEmit(stmnt.toString());
        if (isPrintln)
            this.TAEmit("OUT \\n");
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, ")");
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public boolean PK() throws Exception {
        boolean ret = false;
        this.tabs++;
        if (this.look.b.equals("print")) {
            this.STEmit("IOF -> " + this.look.b);
            this.match(Tokens.IOF, "print");
            ret = false;
        } else if (this.look.b.equals("println")) {
            this.STEmit("IOF -> " + this.look.b);
            this.match(Tokens.IOF, "println");
            ret = true;
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in PK");
        this.tabs--;
        return ret;
    }

    public String DATA() throws Exception {
        String ret = new String();
        this.tabs++;
        if (this.look.a.equals(Tokens.ID) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))) {
            this.STEmit("ID -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.ID);
        } else if (this.look.a.equals(Tokens.NUM) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO))) {
            this.STEmit("NUM -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.NUM);
        } else if (this.look.a.equals(Tokens.LTC)) {
            this.STEmit("LTC -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.LTC);
        } else if (this.look.a.equals(Tokens.STR)) {
            this.STEmit("STR -> " + this.look.b);
            ret = this.look.b;
            this.match(Tokens.STR);
        } else {
            this.STEmit("AE()");
            ret = this.AE();
        }
        this.tabs--;
        return ret;
    }

    public void INS() throws Exception {
        this.tabs++;
        this.STEmit("IOF -> " + this.look.b);
        this.match(Tokens.IOF, "In");
        this.STEmit("IO -> " + this.look.b);
        this.match(Tokens.IO);
        this.STEmit("ID -> " + this.look.b);
        this.TAEmit("IN " + this.look.b);
        this.match(Tokens.ID);
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void LS() throws Exception {
        this.tabs++;
        this.STEmit("WHILE -> " + this.look.b);
        this.match(Tokens.WHILE);
        this.STEmit("LE()");
        String expression = this.LE();
        this.TAEmit("if " + expression + " GOTO " + (this.lines + 2));
        int while_line = this.lines - 1;
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "{");
        this.TAEmit("GOTO");
        int false_line = this.lines - 1;
        this.STEmit("CB()");
        this.CB();
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "}");
        this.TAEmit("GOTO " + while_line);
        this.backpatch(false_line, this.lines);
        this.tabs--;
    }

    public void CS() throws Exception {
        ArrayList<Integer> jumps = new ArrayList<>();
        this.tabs++;
        this.STEmit("DS -> " + this.look.b);
        this.match(Tokens.DS, "if");
        this.STEmit("LE()");
        String expression = this.LE();
        TAEmit("if " + expression + " GOTO " + (this.lines + 2));
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "{");
        TAEmit("GOTO");
        int false_line = this.lines - 1;
        this.STEmit("CB()");
        this.CB();
        this.TAEmit("GOTO");
        jumps.add(this.lines-1);
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "}");
        this.backpatch(false_line, this.lines);
        this.STEmit("CE()");
        this.CE(jumps); // else and elif
        this.tabs--;
        jumps.forEach(l -> {
            this.backpatch(l, this.lines);
        });
    }

    public void CE(ArrayList<Integer> jumps) throws Exception {
        this.tabs++;
        if (look.b.equals("elif")) {
            this.STEmit("DS -> " + this.look.b);
            this.match(Tokens.DS, "elif");
            this.STEmit("LE()");
            String expression = this.LE();
            TAEmit("if " + expression + " GOTO " + (this.lines + 2));
            this.STEmit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ":");
            this.STEmit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "{");
            TAEmit("GOTO");
            int false_line = this.lines - 1;
            this.STEmit("CB()");
            this.CB();
            this.TAEmit("GOTO");
            jumps.add(this.lines-1);
            this.STEmit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "}");
            this.backpatch(false_line, this.lines);
            this.STEmit("CE()");
            this.CE(jumps);
        } else if (look.b.equals("else")) {
            this.STEmit("DS -> " + this.look.b);
            this.match(Tokens.DS, "else");
            this.STEmit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "{");
            this.STEmit("CB()");
            this.CB();
            this.STEmit("BRKT -> " + this.look.b);
            this.match(Tokens.BRKT, "}");
        }
        this.tabs--;
    }

    public void FS() throws Exception {
        this.tabs++;
        this.STEmit("FUNC -> " + this.look.b);
        this.match(Tokens.FUNC);
        this.STEmit("VT()");
        this.VT();
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.STEmit("ID -> " + this.look.b);
        // this.symbolTable.put(this.look.b, Tokens.FUNC);
        this.match(Tokens.ID);
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "(");
        this.STEmit("VT()");
        this.VT();
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ":");
        this.STEmit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.STEmit("PR()");
        this.PR();
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, ")");
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "{");
        this.STEmit("CB()");
        this.CB();
        this.STEmit("RE()");
        this.RE();
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, "}");
        this.tabs--;
    }

    public void PR() throws Exception {
        this.tabs++;
        if (this.look.b.equals(",")) {
            this.STEmit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ",");
            this.STEmit("VT()");
            this.VT();
            this.STEmit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ":");
            this.STEmit("ID -> " + this.look.b);
            this.match(Tokens.ID);
            this.STEmit("PR()");
            this.PR();
        }
        this.tabs--;
    }

    public void RE() throws Exception {
        this.tabs++;
        this.STEmit("RET -> " + this.look.b);
        this.match(Tokens.RET);
        this.STEmit("RS()");
        this.RS();
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void RS() throws Exception {
        this.tabs++;
        if (look.a.equals(Tokens.ID)) {
            this.STEmit("ID -> " + this.look.b);
            this.match(Tokens.ID);
        } else if (look.a.equals(Tokens.NUM)) {
            this.STEmit("NUM -> " + this.look.b);
            this.match(Tokens.NUM);
        } else if (look.a.equals(Tokens.LTC)) {
            this.STEmit("LTC -> " + this.look.b);
            this.match(Tokens.LTC);
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in look, encountered at RS()");
        this.tabs--;
    }

    public void CB() throws Exception {
        this.tabs++;
        if (this.look != null && !this.look.b.equals("}") && !this.look.a.equals(Tokens.RET)) {
            this.STEmit("ST()");
            this.ST();
            this.STEmit("CB()");
            this.CB();
        }
        this.tabs--;
    }

    public void FC() throws Exception {
        this.tabs++;
        this.STEmit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.STEmit("BRKT-> " + this.look.b);
        this.match(Tokens.BRKT, "(");
        this.STEmit("ID -> " + this.look.b);
        this.match(Tokens.ID);
        this.STEmit("FR()");
        this.FR();
        this.STEmit("BRKT -> " + this.look.b);
        this.match(Tokens.BRKT, ")");
        this.STEmit("PUNCT -> " + this.look.b);
        this.match(Tokens.PUNCT, ";");
        this.tabs--;
    }

    public void FR() throws Exception {
        this.tabs++;
        if (this.look.b.equals(",")) {
            this.STEmit("PUNCT -> " + this.look.b);
            this.match(Tokens.PUNCT, ",");
            this.STEmit("ID -> " + this.look.b);
            this.match(Tokens.ID);
            this.STEmit("FR()");
            this.FR();
        }
        this.tabs--;
    }

    public void ST() throws Exception {
        this.tabs++;
        if (this.tokens.get(tokenIdx).a.equals(Tokens.AO)) {
            this.STEmit("AE()");
            this.AE();
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.RO)) {
            this.STEmit("LE()");
            this.LE();
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.PUNCT)) {
            this.STEmit("VDS()");
            this.VDS();
        } else if (this.tokens.get(tokenIdx).a.equals(Tokens.ASO)) {
            this.STEmit("VAS()");
            this.VAS();
        } else if (this.look.b.matches("print(ln){0,1}")) {
            this.STEmit("PS()");
            this.PS();
        } else if (this.look.b.equals("In")) {
            this.STEmit("INS()");
            this.INS();
        } else if (this.look.a.equals(Tokens.WHILE)) {
            this.STEmit("LS()");
            this.LS();
        } else if (this.look.b.equals("if")) {
            this.STEmit("CS()");
            this.CS();
        } else if (this.tokens.get(tokenIdx).b.equals("(")) {
            this.STEmit("FC()");
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
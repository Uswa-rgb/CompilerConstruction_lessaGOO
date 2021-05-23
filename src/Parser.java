import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser {
    BufferedReader wordFileReader;
    Pair<Tokens, String> look;
    ArrayList<Pair<Tokens,String>> tokens; 
    int tokenIdx;

    public void openWordsFile(String path) throws Exception {
        this.tokenIdx = 0;
        wordFileReader = new BufferedReader(new FileReader(new File(path)));
        this.tokens = new ArrayList<Pair<Tokens,String>>();
        String buffer;
        while((buffer = wordFileReader.readLine()) != null && buffer.length() != 0){
            int comma = buffer.indexOf(',', 1);
            String first = buffer.substring(1, comma);
            String second = buffer.substring(comma + 2, buffer.length() - 1);
            this.tokens.add(Pair<Tokens, String>(Tokens.valueOf(first), second));
        }
    }

    public Pair<Tokens, String> nextToken() throws Exception {
        Pair<Tokens, String> pair = this.tokens.get(tokenIdx);
        this.tokenIdx++;
        return pair;
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
        if (this.look.a == Tokens.FUNC) {
            this.FS();
            this.Program();
        }
    }

    public void LE() throws Exception {
        this.LID();
        this.LO();
        this.LID();
    }

    public void LID() throws Exception {
        if (this.look.b.equals("true"))
            this.match(Tokens.DS, "true");
        else if (this.look.b.equals("false"))
            this.match(Tokens.DS, "false");
        else if (this.look.a.equals(Tokens.ID))
            this.match(Tokens.ID);
        else if (this.look.a.equals(Tokens.NUM))
            this.match(Tokens.NUM);
        else if (this.look.a.equals(Tokens.LTC))
            this.match(Tokens.LTC);
        else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in LID");
    }

    public void LO() throws Exception {
        if (this.look.b.equals("<"))
            this.match(Tokens.RO, "<");
        else if (this.look.b.equals("<="))
            this.match(Tokens.RO, "<=");
        else if (this.look.b.equals(">"))
            this.match(Tokens.RO, ">");
        else if (this.look.b.equals(">="))
            this.match(Tokens.RO, ">=");
        else if (this.look.b.equals("="))
            this.match(Tokens.RO, "=");
        else if (this.look.b.equals("/="))
            this.match(Tokens.RO, "/=");
        else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in LO");
    }

    public void AE() throws Exception {
        this.EB();
        this.R();
    }

    public void R() throws Exception {
        if (this.look.b.equals("+")) {
            this.match(Tokens.AO, "+");
            this.EB();
            this.R();
        } else if (this.look.b.equals("-")) {
            this.match(Tokens.AO, "-");
            this.EB();
            this.R();
        }
    }

    public void EB() throws Exception {
        this.EC();
        this.R1();
    }

    public void R1() throws Exception {
        if (this.look.b.equals("*")) {
            this.match(Tokens.AO, "*");
            this.EC();
            this.R1();
        } else if (this.look.b.equals("/")) {
            this.match(Tokens.AO, "/");
            this.EC();
            this.R1();
        }
    }

    public void EC() throws Exception {
        if (this.look.a.equals(Tokens.ID))
            this.match(Tokens.ID);
        else if (this.look.a.equals(Tokens.NUM))
            this.match(Tokens.NUM);
        else if (this.look.b.equals("(")) {
            this.match(Tokens.BRKT, "(");
            this.AE();
            this.match(Tokens.BRKT, ")");
        } else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in EC");
    }

    public void VDS() throws Exception {
        this.VT();
        this.match(Tokens.PUNCT, ":");
        this.VAR();
        this.match(Tokens.PUNCT, ";");
    }

    public void VAR() throws Exception {
        this.match(Tokens.ID);
        this.VAR1();
    }

    public void VAR1() throws Exception {
        if (this.look.b.equals(",")) {
            this.match(Tokens.PUNCT, ",");
            this.match(Tokens.ID);
            this.VAR1();
        }
    }

    public void VT() throws Exception {
        if (this.look.a.equals(Tokens.INT))
            this.match(Tokens.INT);
        else if (this.look.a.equals(Tokens.CHAR))
            this.match(Tokens.CHAR);
        else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in VT");
    }

    public void VAS() throws Exception {
        this.match(Tokens.ID);
        this.match(Tokens.ASO, ":=");
        this.VAL();
        this.match(Tokens.PUNCT, ";");
    }

    public void VAL() throws Exception {
        // TODO: verify this function too
        if (this.look.a.equals(Tokens.NUM) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO)))
            this.match(Tokens.NUM);
        else if (this.look.a.equals(Tokens.LTC))
            this.match(Tokens.LTC);
        else
            this.AE();
    }

    public void PS() throws Exception {
        this.PK();
        this.match(Tokens.BRKT, "(");
        this.DATA();
        this.match(Tokens.BRKT, ")");
        this.match(Tokens.PUNCT, ";");
    }

    public void PK() throws Exception {
        if (this.look.b.equals("print"))
            this.match(Tokens.IOF, "print");
        else if (this.look.b.equals("println"))
            this.match(Tokens.IOF, "println");
        else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in PK");
    }

    public void DATA() throws Exception {
        if (this.look.a.equals(Tokens.ID) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO)))
            this.match(Tokens.ID);
        else if (this.look.a.equals(Tokens.NUM) && !(this.tokens.get(tokenIdx).a.equals(Tokens.AO)))
            this.match(Tokens.NUM);
        else if (this.look.a.equals(Tokens.LTC))
            this.match(Tokens.LTC);
        else if (this.look.a.equals(Tokens.STR))
            this.match(Tokens.STR);
        else
            this.AE();
    }

    public void INS() throws Exception {
        this.match(Tokens.IOF, "In");
        this.match(Tokens.IO);
        this.match(Tokens.ID);
        this.macth(Tokens.PUNCT, ";");
    }

    public void LS() throws Exception {
        this.match(Tokens.WHILE);
        this.LE();
        this.match(Tokens.BRKT, "{");
        this.CB();
        this.match(Tokens.BRKT, "}");
    }

    public void CS() throws Exception {
        this.match(Tokens.DS, "if");
        this.LE();
        this.match(Tokens.PUNCT, ":");
        this.match(Tokens.BRKT, "{");
        this.CB();
        this.match(Tokens.BRKT, "}");
        this.CE();
    }

    public void CE() throws Exception {
        if (look.b.equals("elif")) {
            this.match(Tokens.DS, "elif");
            this.LE();
            this.match(Tokens.PUNCT, ":");
            this.match(Tokens.BRKT, "{");
            this.CB();
            this.match(Tokens.BRKT, "}");
            this.CE();
        } else if (look.b.equals("else")) {
            this.match(Tokens.DS, "else");
            this.match(Tokens.BRKT, "{");
            this.CB();
            this.match(Tokens.BRKT, "}");
        }
    }

    public void FS() throws Exception {
        this.match(Tokens.FUNC);
        this.VT();
        this.match(Tokens.PUNCT, ":");
        this.match(Tokens.ID);
        this.match(Tokens.BRKT, "(");
        this.VT();
        this.match(Tokens.PUNCT, ":");
        this.match(Tokens.ID);
        this.PR();
        this.match(Tokens.BRKT, ")");
        this.match(Tokens.BRKT, "{");
        this.CB();
        this.RE();
        this.match(Tokens.BRKT, "}");
    }

    public void PR() throws Exception {
        if (this.look.b.equals(",")) {
            this.match(Tokens.PUNCT, ",");
            this.VT();
            this.match(Tokens.PUNCT, ":");
            this.match(Tokens.ID);
            this.PR();
        }
    }

    public void RE() throws Exception {
        this.match(Tokens.RET);
        this.RS();
        this.match(Tokens.PUNCT, ";");
    }

    public void RS() throws Exception {
        if (look.a.equals(Tokens.ID))
            this.match(Tokens.ID);
        else if (look.a.equals(Tokens.NUM))
            this.match(Tokens.NUM);
        else if (look.a.equals(Tokens.LTC))
            this.match(Tokens.LTC);
        else
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in look, encountered at RS()");
    }

    public void CB() throws Exception {
        if (this.look != null) {
            this.ST();
            this.CB();
        }
    }

    public void ST() throws Exception {
        if (this.tokens.get(tokenIdx).a.equals(Tokens.AO))
            this.AE();
        else if (this.tokens.get(tokenIdx).a.equals(Tokens.RO))
            this.LE();
        else if (this.tokens.get(tokenIdx).a.equals(Tokens.PUNCT))
            this.VDS();
        else if (this.tokens.get(tokenIdx).a.equals(Tokens.ASO))
            this.VAS();
        else if (this.look.b.matches("print"))
            this.PS();
        else if (this.look.b.equals("In"))
            this.INS();
        else if (this.look.a.equals(Tokens.WHILE))
            this.LS();
        else if (this.look.b.equals("if"))
            this.CS();
        else 
            throw new Exception("MATCH FAILED: unknown or invalid token/lexeme in look, encountered at ST()");
    }

    // Main for testing begins here
    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.openWordsFile("./words.txt");
        p.look = p.nextToken();
        p.Program();

    }
}
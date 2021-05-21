import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Parser {
    private BufferedReader wordFileReader;

    public BufferedReader openWordsFile(String path) throws Exception {
        wordFileReader = new BufferedReader(new FileReader(new File(path)));
        return wordFileReader;
    }

    public Pair<Tokens, String> nextToken() throws Exception {
        String line = this.wordFileReader.readLine();
        if (line == null)
            return null;
        int comma = line.indexOf(',', 1);
        String first = line.substring(1, comma);
        String second = line.substring(comma + 2, line.length() - 1);
        return new Pair<Tokens, String>(Tokens.valueOf(first), second);
    }

    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.openWordsFile("./words.txt");
        Pair<Tokens, String> pair = p.nextToken();
        System.out.println(pair.a + "\n" + pair.b);

    }
}
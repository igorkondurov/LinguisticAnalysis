import com.linguisticanalysis.scanner.Scanner;
import com.linguisticanalysis.scanner.enums.Lexeme;
import com.linguisticanalysis.scanner.scanner.LexemeModel;

public class Main {

    public static void main(String[] args) {
        System.out.println("123");

        Scanner scan = new Scanner("/Users/egor/Desktop/untitled.txt");
        LexemeModel lexeme = new LexemeModel("", 0);
        while (lexeme.getCode() != Lexeme.T_END.lexemeCode) {
            lexeme = scan.getNextLexeme();
            System.out.println("Лексема " + lexeme.getName() + " - " + lexeme.getCode());
        }
    }
}

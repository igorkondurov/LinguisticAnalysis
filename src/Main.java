import com.linguisticanalysis.analyzer.Analyzer;
import com.linguisticanalysis.scanner.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner("/Users/egor/Desktop/untitled.txt");
        Analyzer analyzer = new Analyzer(scan);
        analyzer.S();
        System.out.println("Ошибок не обнаружено!");
    }
}
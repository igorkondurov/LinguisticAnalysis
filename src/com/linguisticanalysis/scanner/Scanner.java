package com.linguisticanalysis.scanner;

import com.linguisticanalysis.scanner.enums.Lexeme;
import com.linguisticanalysis.scanner.scanner.LexemeModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {
    /**
     * Ключевые слова (идентификаторы)
     */
    private static final List<String> stringKeywords = Arrays.asList(
            "int",
            "char",
            "void",
            "main",
            "return",
            "if"
    );

    /**
     * Ключевые слова (коды идентификаторов)
     */
    private static final List<Lexeme> lexemeKeywords = Arrays.asList(
            Lexeme.T_INT,
            Lexeme.T_CHAR,
            Lexeme.T_VOID,
            Lexeme.T_MAIN,
            Lexeme.T_RETURN,
            Lexeme.T_IF
    );

    /**
     * Игнорируемые символы
     */
    private static final List<String> ignoreSymbols = Arrays.asList(
            " ",
            "\t",
            "\n"
    );

    /**
     * Текст программы для сканирования
     */
    private char[] sourceText;

    /**
     * Текущая позиция в тексте
     */
    private int textPointer;

    /**
     * Текущий номер строки в тексте
     */
    private int lineNumber;

    Scanner(String fileName) {
        readProgramFromTextFile(fileName);
        textPointer = 0;
        lineNumber = 0;
    }

    /**
     * Получение очередной лексемы
     */
    public LexemeModel getNextLexeme() {
        int i = 0; // лексема заполняется с позиции i
        // пропускаем все пробелы, табуляции, пустые строки, комментарии
        List<char> currentSymbol = new ArrayList<char>();


        while(isEqualIgnoredSymbol(sourceText[textPointer]) || (sourceText[textPointer] == '/' && sourceText[textPointer+1] == '/')){
            
            // пробел, табуляция
            if ((sourceText[textPointer]==' ') || (sourceText[textPointer]=='\t'))
                textPointer++;

                // пустая строка
            else if(sourceText[textPointer] == '\n') {
                lineNumber++;
                textPointer++;
            }

            // комментарии
            else if (sourceText[textPointer]=='/' && sourceText[textPointer+1]=='/') {
                textPointer += 2;

                // пока не новая строка или не конец программы - пропускаем
                while (sourceText[textPointer] != '\n' && sourceText[textPointer] != '\0') {
                    textPointer++;
                }
            }
        }

        // конец программы
        if (sourceText[textPointer] == '\0') {
            currentSymbol.set(0, '#');

            return new LexemeModel(currentSymbol.toString(),Lexeme.T_END.lexemeCode);
        }

        // id или keyword
        if(((sourceText[textPointer]>='a')&&(sourceText[textPointer]<='z')) ||
                ((sourceText[textPointer]>='A')&&(sourceText[textPointer]<='Z'))) {
            currentSymbol.set(i++, sourceText[textPointer++]);

            while(((sourceText[textPointer]>='a')&&(sourceText[textPointer]<='z')) ||
                    ((sourceText[textPointer]>='A')&&(sourceText[textPointer]<='Z')) ||
                    ((sourceText[textPointer]>='0')&&(sourceText[textPointer]<='9'))){
                if(i < MAXLEX-1){
                    currentSymbol.set(i++, sourceText[textPointer++]);
                }
                else{
                    textPointer++;
                }
            }

            // сверяем с лексикой языка
            for(int j=0; j<MAXKEYW; j++){
                if(strcmp(currentSymbol,keywords[j]) == 0){
                    return indexKeyword[j];
                }
            }
            return Tid;
        }

        // целочисленная константа
        else if((sourceText[textPointer]>='0')&&
                (sourceText[textPointer]<='9')) {

            currentSymbol.set(i++, sourceText[textPointer++]);

            boolean flag = false;

            //ошибка длины константы
            while((sourceText[textPointer]>='0') &&
                    (sourceText[textPointer]<='9')) {

                if(i < MAXLEX-1){
                    currentSymbol.set(i++, sourceText[textPointer++]);
                }
                else {
                    flag = true;
                    textPointer++;
                }
            }

            if((sourceText[textPointer]>='a' && sourceText[textPointer]<='z') || (sourceText[textPointer]>='A' && sourceText[textPointer]<='Z')) {
                while (!isEqualIgnoredSymbol(sourceText[textPointer]) &&
                        sourceText[textPointer] != ';') {
                    currentSymbol.set(i++, sourceText[textPointer++]);
                }
                printError("",currentSymbol);
            }

            if(flag){
                printError("Слишком длинная константа",currentSymbol);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
            }
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_CONST_INT.lexemeCode);
        }

        // =
        else if(sourceText[textPointer] =='='){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                printError("",currentSymbol);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
            }
            return Tassign;
        }

        // * *=
        else if(sourceText[textPointer] =='*'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tmuleq;
            }
            return Tmul;
        }

        // / /=
        else if(sourceText[textPointer] =='/'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tdiveq;
            }
            return Tdiv;
        }

        // % %=
        else if(sourceText[textPointer] =='%'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tmodeq;
            }
            return Tmod;
        }

        // + += ++
        else if(sourceText[textPointer] =='+'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tsumeq;
            }
            if(sourceText[textPointer] == '+'){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tinc;
            }
            return Tsum;
        }

        // - -= --
        else if(sourceText[textPointer] =='-'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tsubeq;
            }
            if(sourceText[textPointer] == '-'){
                currentSymbol.set(i, sourceText[textPointer++]);
                return Tdec;
            }

            return Tsub;
        }


        // &&
        else if(sourceText[textPointer] =='&'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '&'){
                currentSymbol.set(i, sourceText[textPointer++]);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_END.lexemeCode);
            }
            currentSymbol.set(i, sourceText[textPointer++]);
            printError("",currentSymbol);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
        }

        // ||
        else if(sourceText[textPointer] =='|'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '|'){
                currentSymbol.set(i, sourceText[textPointer++]);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_OR.lexemeCode);
            }

            currentSymbol.set(i, sourceText[textPointer]);
            printError("",currentSymbol);
            textPointer++;
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
        }


        // !
        else if(sourceText[textPointer] =='!'){
            currentSymbol.set(i++, sourceText[textPointer++]);

            if(sourceText[textPointer] == '='){
                currentSymbol.set(i, sourceText[textPointer++]);
                printError("",currentSymbol);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
            }

            return new LexemeModel(currentSymbol.toString(),Lexeme.T_NOT.lexemeCode);
        }


        // Символьная константа
        else if(sourceText[textPointer] =='\''){
            currentSymbol.set(i++, sourceText[textPointer++]);

            // Если сразу вторая кавычка
            if(sourceText[textPointer]=='\'')
            {
                currentSymbol.set(i, sourceText[textPointer++]);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_CONST_CHAR.lexemeCode);
            }

            // Если внутри кавычки символ и после него кавычка
            else if(sourceText[textPointer+1]=='\'')
            {
                currentSymbol.set(i++, sourceText[textPointer++]);
                currentSymbol.set(i, sourceText[textPointer++]);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_CONST_CHAR.lexemeCode);
            }
            // Иначе - ошибка
            else {
                currentSymbol.set(0, '\'');
                printError("",currentSymbol);
                return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
            }
        }

        else if(sourceText[textPointer] ==','){
            currentSymbol.set(i, sourceText[textPointer++]);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_COM.lexemeCode);
        }
        else if(sourceText[textPointer] ==';'){
            currentSymbol.set(i, sourceText[textPointer++]);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_SEMI.lexemeCode);
        }
        else if(sourceText[textPointer] =='('){
            currentSymbol.set(i, sourceText[textPointer++]);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_LEFT_ROUND_BR.lexemeCode);
        }
        else if(sourceText[textPointer] ==')'){
            currentSymbol.set(i, sourceText[textPointer++]);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_RIGHT_ROUND_BR.lexemeCode);
        }
        else if(sourceText[textPointer] =='{'){
            currentSymbol.set(i, sourceText[textPointer++]);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_LEFT_BRACE.lexemeCode);
        }
        else if(sourceText[textPointer] =='}'){
            currentSymbol.set(i, sourceText[textPointer++]);
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_RIGHT_BRICE.lexemeCode);
        }
        else {
            currentSymbol.set(0, sourceText[textPointer]);
            printError("Неверный символ",currentSymbol);
            textPointer++;
            return new LexemeModel(currentSymbol.toString(),Lexeme.T_ERROR.lexemeCode);
        }
    }

    public void printError(String description, List<char> lexeme) {
        if (lexeme.toArray().length == 0) {
            System.out.println("Ошибка: " + description);
        } else {
            System.out.println("Ошибка: " + description + ". Неверный символ " + lexeme + "\nСтрока " + lineNumber + 1);
        }
    }

    public static List<String> getStringKeywords() {
        return stringKeywords;
    }

    public static List<Lexeme> getLexemeKeywords() {
        return lexemeKeywords;
    }

    public static List<String> getIgnoreSymbols() {
        return ignoreSymbols;
    }

    public char[] getSourceText() {
        return sourceText;
    }

    public void setSourceText(char[] sourceText) {
        this.sourceText = sourceText;
    }

    public int getTextPointer() {
        return textPointer;
    }

    public void setTextPointer(int textPointer) {
        this.textPointer = textPointer;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Чтение исходного кода программы из текстового файла
     *
     * @param fileName название файла
     */
    private void readProgramFromTextFile(String fileName) {
        try {
            StringBuilder theString;

            File file = new File(fileName);
            java.util.Scanner scanner = new java.util.Scanner(file);
            theString = new StringBuilder(scanner.nextLine());

            while (scanner.hasNextLine()) {
                theString.append("\n").append(scanner.nextLine());
            }

            sourceText = theString.toString().toCharArray();
        } catch (IOException e) {
            printError("Ошибка чтения файла! " + e.toString(), "");
        }
    }

    /**
     * Проверка на игнорируемый символ
     *
     * @param lexeme лексема
     * @return флаг-результат проверки на игнорируемый символ
     */
    private boolean isEqualIgnoredSymbol(char lexeme) {
        return ignoreSymbols.contains(lexeme);
    }
}

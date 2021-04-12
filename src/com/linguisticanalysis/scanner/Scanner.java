package com.linguisticanalysis.scanner;

import com.linguisticanalysis.analyzer.LexemeModel;
import com.linguisticanalysis.enums.Lexeme;

import java.io.File;
import java.io.IOException;

public class Scanner {

    /**
     * Максимальный размер лексемы
     */

    private static final int MAXLEX = 20;
    /**
     * Ключевые слова (идентификаторы)
     */
    private static final String[] stringKeywords = {
            "int",
            "char",
            "void",
            "main",
            "return",
            "if",
            "else"
    };

    /**
     * Ключевые слова (коды идентификаторов)
     */
    private static final Lexeme[] lexemeKeywords = {
            Lexeme.T_INT,
            Lexeme.T_CHAR,
            Lexeme.T_VOID,
            Lexeme.T_MAIN,
            Lexeme.T_RETURN,
            Lexeme.T_IF,
            Lexeme.T_ELSE
    };

    /**
     * Игнорируемые символы
     */
    private static final char[] ignoreSymbols = {
            ' ',
            '\t',
            '\n'
    };

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

    public Scanner(String filePathWithName) {
        readProgramFromTextFile(filePathWithName);
        textPointer = 0;
        lineNumber = 0;
    }

    /**
     * Получение очередной лексемы
     */
    public LexemeModel getNextLexeme() {
        int i = 0; // лексема заполняется с позиции i

        char[] currentLexemeSymbols = new char[MAXLEX];

        // Пропускаем все пробелы, табуляции, пустые строки, комментарии
        while (isEqualIgnoredSymbol(sourceText[textPointer]) || (sourceText[textPointer] == '/' && sourceText[textPointer + 1] == '/')) {

            // пробел, табуляция
            if ((sourceText[textPointer] == ' ') || (sourceText[textPointer] == '\t'))
                textPointer++;

                // пустая строка
            else if (sourceText[textPointer] == '\n') {
                lineNumber++;
                textPointer++;
            }

            // комментарии
            else if (sourceText[textPointer] == '/' && sourceText[textPointer + 1] == '/') {
                textPointer += 2;

                // пока не новая строка или не конец программы - пропускаем
                while (sourceText[textPointer] != '\n' && sourceText[textPointer] != '\0') {
                    textPointer++;
                }
            }
        }

        // Проверка на конец программы
        if (sourceText[textPointer] == '\0') {
            currentLexemeSymbols[0] = '#';

            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_END.lexemeCode);
        }

        // id или keyword
        if (((sourceText[textPointer] >= 'a') && (sourceText[textPointer] <= 'z')) ||
                ((sourceText[textPointer] >= 'A') && (sourceText[textPointer] <= 'Z'))) {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            while (((sourceText[textPointer] >= 'a') && (sourceText[textPointer] <= 'z')) ||
                    ((sourceText[textPointer] >= 'A') && (sourceText[textPointer] <= 'Z')) ||
                    ((sourceText[textPointer] >= '0') && (sourceText[textPointer] <= '9'))) {
                if (i < MAXLEX - 1) {
                    currentLexemeSymbols[i++] = sourceText[textPointer++];
                } else {
                    textPointer++;
                }
            }

            // сверяем с ключевыми словами
            for (int j = 0; j < lexemeKeywords.length; j++) {
                if (listToSting(currentLexemeSymbols).equals(stringKeywords[j])) {
                    return new LexemeModel(listToSting(currentLexemeSymbols), lexemeKeywords[j].lexemeCode);
                }
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_ID.lexemeCode);
        }

        // целочисленная константа
        else if ((sourceText[textPointer] >= '0') &&
                (sourceText[textPointer] <= '9')) {

            currentLexemeSymbols[i++] = sourceText[textPointer++];

            boolean errorLengthFlag = false;

            //ошибка длины константы
            while ((sourceText[textPointer] >= '0') &&
                    (sourceText[textPointer] <= '9')) {

                if (i < MAXLEX - 1) {
                    currentLexemeSymbols[i++] = sourceText[textPointer++];
                } else {
                    errorLengthFlag = true;
                    textPointer++;
                }
            }

            if ((sourceText[textPointer] >= 'a' && sourceText[textPointer] <= 'z') || (sourceText[textPointer] >= 'A' && sourceText[textPointer] <= 'Z')) {
                while (!isEqualIgnoredSymbol(sourceText[textPointer]) &&
                        sourceText[textPointer] != ';') {
                    currentLexemeSymbols[i++] = sourceText[textPointer++];
                }
                printError("", listToSting(currentLexemeSymbols));
            }

            if (errorLengthFlag) {
                printError("Слишком длинная целочисленная константа", listToSting(currentLexemeSymbols));
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_ERROR.lexemeCode);
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_CONST_INT.lexemeCode);
        }

        // * *=
        else if (sourceText[textPointer] == '*') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_MUL_EQ.lexemeCode);
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_MUL.lexemeCode);
        }

        // / /=
        else if (sourceText[textPointer] == '/') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_DIV_EQ.lexemeCode);
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_DIV.lexemeCode);
        }

        // + +=
        else if (sourceText[textPointer] == '+') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];

                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_PLUS_EQ.lexemeCode);
            }

            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_PLUS.lexemeCode);
        }

        // - -=
        else if (sourceText[textPointer] == '-') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_SUB.lexemeCode);
            }

            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_SUB.lexemeCode);
        }


        // &&
        else if (sourceText[textPointer] == '&') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '&') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_AND.lexemeCode);
            }
            currentLexemeSymbols[i] = sourceText[textPointer++];
            printError("", listToSting(currentLexemeSymbols));
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_ERROR.lexemeCode);
        }

        // ||
        else if (sourceText[textPointer] == '|') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '|') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_OR.lexemeCode);
            }

            currentLexemeSymbols[i] = sourceText[textPointer++];
            printError("", listToSting(currentLexemeSymbols));
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_ERROR.lexemeCode);
        }

        // = ==
        else if (sourceText[textPointer] == '=') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_EQUAL.lexemeCode);
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_EQ.lexemeCode);
        }

        // ! !=
        else if (sourceText[textPointer] == '!') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_NOT_EQUAL.lexemeCode);
            }

            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_NOT.lexemeCode);
        }

        // > >=
        else if (sourceText[textPointer] == '>') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_MORE_AND_EQUAL.lexemeCode);
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_MORE.lexemeCode);
        }

        // < <=
        else if (sourceText[textPointer] == '<') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            if (sourceText[textPointer] == '=') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_LESS_AND_EQUAL.lexemeCode);
            }
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_LESS.lexemeCode);
        }


        // Символьная константа
        else if (sourceText[textPointer] == '\'') {
            currentLexemeSymbols[i++] = sourceText[textPointer++];

            // Если сразу вторая кавычка
            if (sourceText[textPointer] == '\'') {
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_CONST_CHAR.lexemeCode);
            }

            // Если внутри кавычки символ и после него кавычка
            else if (sourceText[textPointer + 1] == '\'') {
                currentLexemeSymbols[i++] = sourceText[textPointer++];
                currentLexemeSymbols[i] = sourceText[textPointer++];
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_CONST_CHAR.lexemeCode);
            }
            // Иначе - ошибка
            else {
                currentLexemeSymbols[0] = '\'';
                printError("", listToSting(currentLexemeSymbols));
                return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_ERROR.lexemeCode);
            }
        } else if (sourceText[textPointer] == ',') {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_COM.lexemeCode);
        } else if (sourceText[textPointer] == ';') {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_SEMI.lexemeCode);
        } else if (sourceText[textPointer] == '(') {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_LEFT_ROUND_BR.lexemeCode);
        } else if (sourceText[textPointer] == ')') {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_RIGHT_ROUND_BR.lexemeCode);
        } else if (sourceText[textPointer] == '{') {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_LEFT_BRACE.lexemeCode);
        } else if (sourceText[textPointer] == '}') {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_RIGHT_BRACE.lexemeCode);
        } else {
            currentLexemeSymbols[i] = sourceText[textPointer++];
            printError("Неверный символ ", listToSting(currentLexemeSymbols));
            return new LexemeModel(listToSting(currentLexemeSymbols), Lexeme.T_ERROR.lexemeCode);
        }
    }

    public void printError(String description, String lexeme) {
        if (lexeme.isEmpty()) {
            System.out.println("Ошибка: " + description);
        } else {
            System.out.println("Ошибка: " + description + ". Неверный символ " + lexeme + "\nСтрока " + (lineNumber + 1));
        }

        System.exit(1);
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

            sourceText = (theString.toString() + "\0").toCharArray();
        } catch (IOException e) {
            printError("Ошибка чтения файла! " + e.toString(), "");
        }
    }

    /**
     * Проверка на игнорируемый символ
     *
     * @param lexemeSymb лексема в виде символа
     * @return флаг-результат проверки на игнорируемый символ
     */
    private boolean isEqualIgnoredSymbol(char lexemeSymb) {
        for (char symbol :
                ignoreSymbols) {
            if (symbol == lexemeSymb)
                return true;
        }
        return false;
    }

    private String listToSting(char[] list) {
        StringBuilder result = new StringBuilder();
        for (Character ch :
                list) {
            if (ch != '\u0000') {
                result.append(ch);
            }
        }
        return result.toString();
    }
}

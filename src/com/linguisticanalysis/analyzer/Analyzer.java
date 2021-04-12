package com.linguisticanalysis.analyzer;

import com.linguisticanalysis.scanner.Scanner;
import com.linguisticanalysis.scanner.enums.Lexeme;
import com.linguisticanalysis.scanner.scanner.LexemeModel;

public class Analyzer {
    private Scanner scanner;

    public Analyzer(Scanner scanner) {
        this.scanner = scanner;
    }


    // Проверка на лексемы
    private void checkLexeme(Lexeme[] lexemes, String errorText) {
        LexemeModel lexemeType = scanner.getNextLexeme();
        Boolean errorFlag = true;
        for (Lexeme lexeme:
             lexemes) {
            if (lexemeType.getCode() == lexeme.lexemeCode) {
                errorFlag = false;
                break;
            }
        }
        if (errorFlag) {
            scanner.printError(errorText, lexemeType.getName());
        }
    }

    // Проверка на лексемы
    private void checkLexeme(Lexeme lexeme, String errorText) {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != lexeme.lexemeCode) {
            scanner.printError(errorText, lexemeType.getName());
        }
    }

    // Устанавливает указатель сканера
    private void setPointerTo(int pointer, int line) {
        scanner.setTextPointer(pointer);
        scanner.setLineNumber(line);
    }


    /**
     * Аксиома
     */
    public void S() {
        int numberOfMains = 0;
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();
        while (lexemeType.getCode() != Lexeme.T_END.lexemeCode) {
            if (lexemeType.getCode() == Lexeme.T_CHAR.lexemeCode || lexemeType.getCode() == Lexeme.T_INT.lexemeCode) {
                scanner.getNextLexeme();
                lexemeType = scanner.getNextLexeme();

                if (lexemeType.getCode() == Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
                    setPointerTo(lastPointer, lastLine);
                    A();
                } else {
                    setPointerTo(lastPointer, lastLine);
                    B();
                }

            } else if (lexemeType.getCode() == Lexeme.T_VOID.lexemeCode) {
                lexemeType = scanner.getNextLexeme();
                if (lexemeType.getCode() == Lexeme.T_MAIN.lexemeCode) {
                    numberOfMains += 1;

                    checkLexeme(Lexeme.T_LEFT_ROUND_BR, "Ожидался символ (");
                    checkLexeme(Lexeme.T_RIGHT_ROUND_BR, "Ожидался символ )");

                    O();
                } else {
                    setPointerTo(lastPointer, lastLine);
                    A();
                }
            }
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            lexemeType = scanner.getNextLexeme();
        }

        if (numberOfMains == 0) {
            scanner.printError("Функция main отсутствует", lexemeType.getName());
        } else if (numberOfMains > 1) {
            scanner.printError("Функция main не является единственной", lexemeType.getName());
        }
    }

    /**
     * Описание функции
     */
    private void A() {
        LexemeModel lexemeType;
        TF();
        ID();
        checkLexeme(Lexeme.T_LEFT_ROUND_BR, "Ожидался символ (");
        P();
        checkLexeme(Lexeme.T_RIGHT_ROUND_BR, "Ожидался символ )");
        O();
    }

    /**
     * Тип данных
     */
    private void T() {
        checkLexeme(new Lexeme[]{Lexeme.T_CHAR, Lexeme.T_INT}, "Ожидался тип char или int");
    }

    /**
     * Тип функции
     */
    private void TF() {
        checkLexeme(new Lexeme[]{Lexeme.T_CHAR, Lexeme.T_INT, Lexeme.T_VOID}, "Ожидался тип функции char, int или void");
    }

    /**
     * Описание переменных одного типа
     */
    private void B() {
        T();
        L();
        checkLexeme(Lexeme.T_SEMI, "Ожидался символ ;");
    }

    /**
     * Список переменных при описании
     */
    private void L() {

        int lastPointer;
        int lastLine;

        LexemeModel lexemeType;

        do {
            ID();
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            lexemeType = scanner.getNextLexeme();
            if (lexemeType.getCode() == Lexeme.T_EQ.lexemeCode) {
                setPointerTo(lastPointer, lastLine);
                I();
            } else {
                setPointerTo(lastPointer, lastLine);
            }
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
        } while (scanner.getNextLexeme().getCode() == Lexeme.T_COM.lexemeCode);
        setPointerTo(lastPointer, lastLine);
    }

    /**
     * Блок { }
     */
    private void O() {
        checkLexeme(Lexeme.T_LEFT_BRACE, "Ожидался символ {");

        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_RIGHT_BRACE.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
            Z();
            checkLexeme(Lexeme.T_RIGHT_BRACE, "Ожидался символ }");
        }
    }

    /**
     * Операторы
     */
    private void Z() {
        LexemeModel lexemeType;
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        do {
            setPointerTo(lastPointer, lastLine);
            lexemeType = scanner.getNextLexeme();
            setPointerTo(lastPointer, lastLine);
            if (lexemeType.getCode() == Lexeme.T_CHAR.lexemeCode || lexemeType.getCode() == Lexeme.T_INT.lexemeCode) {
                B();
            } else {
                Q();
            }

            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
        } while (scanner.getNextLexeme().getCode() != Lexeme.T_RIGHT_BRACE.lexemeCode);

        setPointerTo(lastPointer, lastLine);
    }

    /**
     * Один оператор
     */
    private void Q() {
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();

        if (lexemeType.getCode() == Lexeme.T_IF.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
            IF();
        } else {
            if (lexemeType.getCode() == Lexeme.T_RETURN.lexemeCode) {
                A1();
            } else {
                if (scanner.getNextLexeme().getCode() == Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
                    setPointerTo(lastPointer, lastLine);
                    R();
                } else {
                    setPointerTo(lastPointer, lastLine);
                    F();
                }
            }
            checkLexeme(Lexeme.T_SEMI, "Ожидался символ ;");
        }
    }

    /**
     * Присваивание
     */
    private void F() {
        ID();
        E();
        A1();
    }

    /**
     * Блок условий
     */
    private void IF() {

        // if
        checkLexeme(Lexeme.T_IF, "Ожидалось ключевое слово if");
        checkLexeme(Lexeme.T_LEFT_ROUND_BR, "Ожидался символ (");
        A1();
        checkLexeme(Lexeme.T_RIGHT_ROUND_BR, "Ожидался символ )");
        O();

        // else if
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        while (scanner.getNextLexeme().getCode() == Lexeme.T_ELSE.lexemeCode && scanner.getNextLexeme().getCode() == Lexeme.T_IF.lexemeCode) {
            checkLexeme(Lexeme.T_LEFT_ROUND_BR, "Ожидался символ (");
            A1();
            checkLexeme(Lexeme.T_RIGHT_ROUND_BR, "Ожидался символ )");
            O();
            lastLine = scanner.getLineNumber();
            lastPointer = scanner.getTextPointer();
        }
        setPointerTo(lastPointer, lastLine);


        // else
        lastPointer = scanner.getTextPointer();
        lastLine = scanner.getLineNumber();
        if (scanner.getNextLexeme().getCode() == Lexeme.T_ELSE.lexemeCode) {
            O();
        } else {
            setPointerTo(lastPointer, lastLine);
        }
    }

    /**
     * Вызов функции
     */
    private void R() {
        ID();
        checkLexeme(Lexeme.T_LEFT_ROUND_BR, "Ожидался символ (");
        M();
        checkLexeme(Lexeme.T_RIGHT_ROUND_BR, "Ожидался символ )");
    }

    /**
     * Параметры функции
     */
    private void P() {
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        if (scanner.getNextLexeme().getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
            do {
                T();
                ID();
                lastPointer = scanner.getTextPointer();
                lastLine = scanner.getLineNumber();
            } while (scanner.getNextLexeme().getCode() == Lexeme.T_COM.lexemeCode);
            setPointerTo(lastPointer, lastLine);
        }
    }

    /**
     * Список аргументов функции
     */
    private void M() {
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        if (scanner.getNextLexeme().getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
            do {
                A1();
                lastPointer = scanner.getTextPointer();
                lastLine = scanner.getLineNumber();
            } while (scanner.getNextLexeme().getCode() == Lexeme.T_COM.lexemeCode);
            setPointerTo(lastPointer, lastLine);
        }
    }

    /**
     * Инициал
     */
    private void I() {
        checkLexeme(Lexeme.T_EQ, "Ожидался символ =");
        A1();
    }

    /**
     * Оператор присваивания
     */
    private void E() {
        Lexeme[] lexemes = new Lexeme[]{Lexeme.T_PLUS_EQ, Lexeme.T_SUB_EQ, Lexeme.T_DIV_EQ, Lexeme.T_MUL_EQ, Lexeme.T_EQ};
        checkLexeme(lexemes, "Ожидался оператор присваивания");
    }

    /**
     * Идентификатор (име переменной или функции)
     */
    private void ID() {
        checkLexeme(Lexeme.T_ID, "Ожидалось имя перемнной или функции");
    }

    /**
     * Логические операции
     */
    private void A1() {
        int lastPointer;
        int lastLine;
        do {
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            while (scanner.getNextLexeme().getCode() == Lexeme.T_NOT.lexemeCode) ;
            setPointerTo(lastPointer, lastLine);
            A2();
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
        } while (scanner.getNextLexeme().getCode() == Lexeme.T_AND.lexemeCode || scanner.getNextLexeme().getCode() == Lexeme.T_OR.lexemeCode);
        setPointerTo(lastPointer, lastLine);
    }

    /**
     * Операции сравнения
     */
    private void A2() {
        int lastPointer;
        int lastLine;

        A3();

        lastPointer = scanner.getTextPointer();
        lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() == Lexeme.T_MORE.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_LESS.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_MORE_AND_EQUAL.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_LESS_AND_EQUAL.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_EQUAL.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_NOT_EQUAL.lexemeCode) {
            A3();
        } else {
            setPointerTo(lastPointer, lastLine);
        }
    }

    /**
     * Арифметические операции
     */
    private void A3() {
        int lastPointer;
        int lastLine;
        LexemeModel lexemeType;
        do {
            A4();
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            lexemeType = scanner.getNextLexeme();
        } while (lexemeType.getCode() == Lexeme.T_SUB.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_MUL.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_PLUS.lexemeCode ||
                lexemeType.getCode() == Lexeme.T_DIV.lexemeCode);
        setPointerTo(lastPointer, lastLine);
    }

    /**
     * Значение (переменная, константы, результат вызова функции, выражение)
     */
    private void A4() {
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();

        if (lexemeType.getCode() == Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            A1();
            checkLexeme(Lexeme.T_RIGHT_ROUND_BR, "Ожидался символ )");
        } else if (lexemeType.getCode() == Lexeme.T_ID.lexemeCode) {
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            lexemeType = scanner.getNextLexeme();
            setPointerTo(lastPointer, lastLine);
            if (lexemeType.getCode() == Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
                R();
            }
        } else {
            setPointerTo(lastPointer, lastLine);
            Lexeme[] lexemes = new Lexeme[]{Lexeme.T_CONST_CHAR, Lexeme.T_CONST_INT};
            checkLexeme(lexemes, "Ожидалась целочисленная или символьная константа");
        }
    }
}

package com.linguisticanalysis.analyzer;

import com.linguisticanalysis.scanner.Scanner;
import com.linguisticanalysis.scanner.enums.Lexeme;
import com.linguisticanalysis.scanner.scanner.LexemeModel;

public class Analyzer {
    private Scanner scanner;

    public Analyzer(Scanner scanner) {
        this.scanner = scanner;
    }

    private void setPointerTo(int pointer, int line) {
        scanner.setTextPointer(pointer);
        scanner.setLineNumber(line);
    }

    public void S() {
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
                    lexemeType = scanner.getNextLexeme();
                    if (lexemeType.getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
                        scanner.printError("Ожидался символ (", lexemeType.getName());
                    }

                    lexemeType = scanner.getNextLexeme();
                    if (lexemeType.getCode() != Lexeme.T_RIGHT_ROUND_BR.lexemeCode) {
                        scanner.printError("Ожидался символ )", lexemeType.getName());
                    }

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
    }

    private void A() {
        LexemeModel lexemeType;

        TF();
        ID();

        lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            scanner.printError("Ожидался символ (", lexemeType.getName());
        }

        P();

        lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_RIGHT_ROUND_BR.lexemeCode) {
            scanner.printError("Ожидался символ )", lexemeType.getName());
        }

        O();
    }

    private void T() {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_CHAR.lexemeCode && lexemeType.getCode() != Lexeme.T_INT.lexemeCode) {
            scanner.printError("Ожидался тип char или int", lexemeType.getName());
        }
    }

    private void TF() {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_CHAR.lexemeCode && lexemeType.getCode() != Lexeme.T_INT.lexemeCode && lexemeType.getCode() != Lexeme.T_VOID.lexemeCode) {
            scanner.printError("Ожидался тип функции char, int или void", lexemeType.getName());
        }
    }

    private void B() {
        T();
        L();

        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_SEMI.lexemeCode) {
            scanner.printError("Ожидался символ ;", lexemeType.getName());
        }
    }

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

    private void O() {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_LEFT_BRACE.lexemeCode) {
            scanner.printError("Ожидался символ {", lexemeType.getName());
        }

        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_RIGHT_BRACE.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
            Z();
            lexemeType = scanner.getNextLexeme();
            if (lexemeType.getCode() != Lexeme.T_RIGHT_BRACE.lexemeCode) {
                scanner.printError("Ожидался символ }", lexemeType.getName());
            }
        }
    }

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

            lexemeType = scanner.getNextLexeme();
            if (lexemeType.getCode() != Lexeme.T_SEMI.lexemeCode) {
                scanner.printError("Ожидался символ ;", lexemeType.getName());
            }
        }
    }

    private void F() {
        ID();
        E();
        A1();
    }

    private void IF() {

        // if
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_IF.lexemeCode) {
            scanner.printError("Ожидалось ключевое слово if", lexemeType.getName());
        }


        // (
        lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            scanner.printError("Ожидалось символ (", lexemeType.getName());
        }

        A1();

        // )
        lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_RIGHT_ROUND_BR.lexemeCode) {
            scanner.printError("Ожидалось символ )", lexemeType.getName());
        }

        O();
    }

    private void R() {

        ID();

        // (
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            scanner.printError("Ожидалось символ (", lexemeType.getName());
        }

        M();

        // )
        lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_RIGHT_ROUND_BR.lexemeCode) {
            scanner.printError("Ожидалось символ )", lexemeType.getName());
        }
    }

    private void P() {
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
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


    //
    private void M() {
        int lastPointer = scanner.getTextPointer();
        int lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
            do {
                A1();
                lastPointer = scanner.getTextPointer();
                lastLine = scanner.getLineNumber();
            } while (scanner.getNextLexeme().getCode() == Lexeme.T_COM.lexemeCode);
            setPointerTo(lastPointer, lastLine);
        }
    }

    private void I() {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_EQ.lexemeCode) {
            scanner.printError("Ожидался символ =", lexemeType.getName());
        }

        A1();
    }

    private void E() {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_PLUS_EQ.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_SUB_EQ.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_DIV_EQ.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_MUL_EQ.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_EQ.lexemeCode) {
            scanner.printError("Ожидался оператор присваивания", lexemeType.getName());
        }
    }

    private void ID() {
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_ID.lexemeCode) {
            scanner.printError("Ожидалось имя перемнной или функции", lexemeType.getName());
        }
    }


    private void A1() {
        int lastPointer;
        int lastLine;
        do {
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            while (scanner.getNextLexeme().getCode() == Lexeme.T_NOT.lexemeCode);
            setPointerTo(lastPointer, lastLine);
            A2();
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
        } while (scanner.getNextLexeme().getCode() == Lexeme.T_AND.lexemeCode || scanner.getNextLexeme().getCode() == Lexeme.T_OR.lexemeCode);
        setPointerTo(lastPointer, lastLine);
    }

    private void A2() {
        int lastPointer;
        int lastLine;

        A3();

        lastPointer = scanner.getTextPointer();
        lastLine = scanner.getLineNumber();
        LexemeModel lexemeType = scanner.getNextLexeme();
        if (lexemeType.getCode() != Lexeme.T_MORE.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_LESS.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_MORE_AND_EQUAL.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_LESS_AND_EQUAL.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_EQUAL.lexemeCode &&
                lexemeType.getCode() != Lexeme.T_NOT_EQUAL.lexemeCode) {
            setPointerTo(lastPointer, lastLine);
        }


    }

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

    private void A4() {
        int lastPointer;
        int lastLine;
        LexemeModel lexemeType = scanner.getNextLexeme();

        if (lexemeType.getCode() == Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
            A1();
            lexemeType = scanner.getNextLexeme();
            if (lexemeType.getCode() != Lexeme.T_RIGHT_ROUND_BR.lexemeCode) {
                scanner.printError("Ожидался символ )", lexemeType.getName());
            }
        } else if (lexemeType.getCode() == Lexeme.T_ID.lexemeCode) {
            lastPointer = scanner.getTextPointer();
            lastLine = scanner.getLineNumber();
            lexemeType = scanner.getNextLexeme();
            setPointerTo(lastPointer, lastLine);
            if (lexemeType.getCode() == Lexeme.T_LEFT_ROUND_BR.lexemeCode) {
                R();
            }
        } else if (lexemeType.getCode() != Lexeme.T_CONST_CHAR.lexemeCode && lexemeType.getCode() != Lexeme.T_CONST_INT.lexemeCode) {
            scanner.printError("Ожидалась целочисленная или символьная константа", lexemeType.getName());
        }
    }


}

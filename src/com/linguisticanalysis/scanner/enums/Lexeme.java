package com.linguisticanalysis.scanner.enums;

/**
 * Лексема
 */
public enum Lexeme {
    T_ID(1),
    T_INT(10),
    T_CHAR(11),
    T_VOID(12),
    T_MAIN(13),
    T_RETURN(14),
    T_IF(15),

    // Знаки операций
    T_EQ(20),
    T_PLUS(21),
    T_SUB(22),
    T_MUL(23),
    T_DIV(24),
    T_PLUS_EQ(25),
    T_MUL_EQ(26),
    T_DIV_EQ(27),
    T_NOT(28),
    T_AND(29),
    T_OR(30),

    // Знаки сравнения
    T_MORE(31),
    T_LESS(32),
    T_EQUAL(33),
    T_NOT_EQUAL(34),
    T_MORE_AND_EQUAL(35),
    T_LESS_AND_EQUAL(36),

    T_CONST_INT(40),
    T_CONST_CHAR(41),
    T_SEMI(42),
    T_COM(43),
    T_LEFT_ROUND_BR(44),
    T_RIGHT_ROUND_BR(45),
    T_LEFT_BRACE(46),
    T_RIGHT_BRICE(47),

    T_END(1000),
    T_ERROR(2000);


    public final int lexemeCode;

    private Lexeme(int lexemeCode) {
        this.lexemeCode = lexemeCode;
    }
}

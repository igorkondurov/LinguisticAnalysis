package com.linguisticanalysis.enums;

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
    T_ELSE(16),

    // Знаки операций
    T_EQ(20),
    T_PLUS(21),
    T_SUB(22),
    T_MUL(23),
    T_DIV(24),
    T_PLUS_EQ(25),
    T_SUB_EQ(26),
    T_MUL_EQ(27),
    T_DIV_EQ(28),
    T_NOT(29),
    T_AND(30),
    T_OR(31),

    // Знаки сравнения
    T_MORE(35),
    T_LESS(36),
    T_EQUAL(37),
    T_NOT_EQUAL(38),
    T_MORE_AND_EQUAL(39),
    T_LESS_AND_EQUAL(40),

    T_CONST_INT(45),
    T_CONST_CHAR(46),
    T_SEMI(47),
    T_COM(48),
    T_LEFT_ROUND_BR(49),
    T_RIGHT_ROUND_BR(50),
    T_LEFT_BRACE(51),
    T_RIGHT_BRACE(52),

    T_END(1000),
    T_ERROR(2000);


    public final int lexemeCode;

    private Lexeme(int lexemeCode) {
        this.lexemeCode = lexemeCode;
    }
}

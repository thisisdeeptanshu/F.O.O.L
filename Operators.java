class Operators {
    public enum operators {
        EQUAL_TO,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        NONE
    }
    public operators getOperator(char sign) {
        if (sign == '=') return operators.EQUAL_TO;
        else if (sign == '+') return operators.PLUS;
        else if (sign == '-') return operators.MINUS;
        else if (sign == '*') return operators.MULTIPLY;
        else if (sign == '/') return operators.DIVIDE;
        else {
            return operators.NONE;
        }
    }
}
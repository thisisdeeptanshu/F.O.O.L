class Operators {
    public enum operators {
        EQUAL_TO,
        PLUS,
        MINUS,
        NONE
    }
    public operators getOperator(char sign) {
        if (sign == '=') return operators.EQUAL_TO;
        else if (sign == '+') return operators.PLUS;
        else if (sign == '-') return operators.MINUS;
        else {
            return operators.NONE;
        }
    }
}
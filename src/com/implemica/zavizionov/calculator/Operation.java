package com.implemica.zavizionov.calculator;

/**
 * Created by Suff on 14.07.2015.
 */
public enum Operation {
    NOOP,
    PLUS("+"), MINUS("-"), DIVIDE("/"), MULTIPLY("*"),
    INVERT, SQRT, PERCENT, REVERSE, MC,
    MR, MS, MPLUS, MMINUS;

    String sign;

    public String getSign() {
        return sign;
    }

    Operation(String sign) {
        this.sign = sign;
    }

    Operation() {
        this.sign = "";
    }


}

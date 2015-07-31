package com.implemica.zavizionov.calculator;

/**
 * Enumeration hold possible calculator operations.
 * Some of operations can also have their signs, all
 * others have empty string as sign.
 *
 * @author Zavizionov Andrii
 */
public enum Operation {
    NOOP,
    PLUS("+"), MINUS("-"), DIVIDE("/"), MULTIPLY("*"),
    INVERT, SQRT, PERCENT, REVERSE, MC,
    MR, MS, MPLUS, MMINUS;

    private final String sign;

    /**
     * Returns a sign of operation.
     *
     * @return sign of operation.
     */
    public String getSign() {
        return sign;
    }

    /**
     * Creates operation with sign.
     *
     * @param sign - sign of operation
     */
    Operation(String sign) {
        this.sign = sign;
    }

    /**
     * Creates operation without sign.
     */
    Operation() {
        this("");
    }
}

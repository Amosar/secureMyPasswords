package com.securemypasswords.secureMyPasswords;

public enum AppRequestVariables {
    passAddRequestCode(101),
    passModificationRequestCode(102),

    RESULT_OK(0),
    RESULT_ERROR(1),
    RESULT_CANCELLED(2);

    private final int value;

    AppRequestVariables(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}

package com.banco.bank.domain.model;

public enum TipoCuentaEnum {

    AHORROS("Ahorros"),
    CORRIENTE("Corriente");

    private final String displayName;

    TipoCuentaEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TipoCuentaEnum fromDisplayName(String displayName) {
        for (var value : values()) {
            if (value.displayName.equalsIgnoreCase(displayName)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Tipo de cuenta inválido: " + displayName);
    }
}

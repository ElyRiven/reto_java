package com.banco.bank.domain.model;

public enum TipoMovimientoEnum {

    DEPOSITO("Depósito"),
    RETIRO("Retiro");

    private final String displayName;

    TipoMovimientoEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TipoMovimientoEnum fromDisplayName(String value) {
        for (var tipo : values()) {
            if (tipo.displayName.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("El campo tipoMovimiento debe ser 'Retiro' o 'Depósito'");
    }
}

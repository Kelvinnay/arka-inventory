package com.arka.inventory.dto.enums;

import java.util.Map;

public enum InventoryMovementType {// ----------------------------------------------------
    // Entradas (Aumentos)
    // Constante(Código, Descripción)
    // ----------------------------------------------------
    RECEPCION_POR_COMPRA("A10","ENTRADA_POR_COMPRA","Entrada de mercancía de proveedor"),
    DEVOLUCION_DE_CLIENTE("A11","DEVOLUCION_DE_CLIENTE", "Reingreso de producto por cliente"),
    ENTRADA_POR_PRODUCCION("A12","ENTRADA_POR_PRODUCCION", "Producto terminado proveniente de fabricación"),
    AJUSTE_POSITIVO("A13","AJUSTE_POSITIVO", "Corrección de inventario (Sobrante)"),
    RECEPCION_POR_TRASPASO("A14", "RECEPCION_POR_TRASPASO","Entrada de stock desde otro almacén propio"),

    // ----------------------------------------------------
    // Salidas (Disminuciones)
    // ----------------------------------------------------
    VENTA_DESPACHO_CLIENTE("S20","VENTA_DESPACHO_CLIENTE", "Salida por venta y envío a cliente"),
    CONSUMO_EN_PRODUCCION("S21","CONSUMO_EN_PRODUCCION", "Salida de materia prima para fabricación"),
    DEVOLUCION_A_PROVEEDOR("S22","DEVOLUCION_A_PROVEEDOR", "Retorno de mercancía defectuosa al proveedor"),
    AJUSTE_NEGATIVO("S23","AJUSTE_NEGATIVO", "Corrección de inventario (Faltante, Pérdida)"),
    SALIDA_POR_DESECHO_MERMA("S24","SALIDA_POR_DESECHO_MERMA", "Eliminación de stock dañado/obsoleto"),
    ENTREGA_POR_TRASPASO("S25","ENTREGA_POR_TRASPASO", "Salida de stock hacia otro almacén propio");

    // Atributos (campos) que tendrá cada constante del enum
    private final String codigo;
    private final String descripcion;
    private final String detalle;

    InventoryMovementType(String codigo,String descripcion, String detalle) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.detalle = detalle;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        return codigo + ": " + descripcion+ ": " + detalle;
    }
}
package com.vozsocial.domain.enums;

/**
 * Enum que representa os tipos de filtros de voz disponíveis
 */
public enum TipoFiltroVoz {
    NATURAL("Natural"),
    ROBOTICO("Robótico");

    private final String descricao;

    TipoFiltroVoz(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

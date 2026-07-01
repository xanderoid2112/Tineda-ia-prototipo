package com.backend_aimarket.demo.dto;

import java.util.List;

public class PreferenciasDTO {

    private List<String> marcas_preferidas;  // Cambiar de marcasPreferidas a marcas_preferidas
    private List<String> dietas;
    private List<String> categorias_excluidas;  // Cambiar de categoriasExcluidas a categorias_excluidas

    // Constructores
    public PreferenciasDTO() {}

    public PreferenciasDTO(List<String> marcas_preferidas, List<String> dietas, List<String> categorias_excluidas) {
        this.marcas_preferidas = marcas_preferidas;
        this.dietas = dietas;
        this.categorias_excluidas = categorias_excluidas;
    }

    // Getters y Setters
    public List<String> getMarcas_preferidas() { return marcas_preferidas; }
    public void setMarcas_preferidas(List<String> marcas_preferidas) { this.marcas_preferidas = marcas_preferidas; }

    public List<String> getDietas() { return dietas; }
    public void setDietas(List<String> dietas) { this.dietas = dietas; }

    public List<String> getCategorias_excluidas() { return categorias_excluidas; }
    public void setCategorias_excluidas(List<String> categorias_excluidas) { this.categorias_excluidas = categorias_excluidas; }
}
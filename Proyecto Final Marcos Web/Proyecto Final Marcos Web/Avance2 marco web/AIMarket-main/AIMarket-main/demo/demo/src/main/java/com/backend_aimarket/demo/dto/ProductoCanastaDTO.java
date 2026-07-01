package com.backend_aimarket.demo.dto;

import java.math.BigDecimal;

public class ProductoCanastaDTO {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String marca;
    private String dieta;
    private String urlImagen;
    private String categoria;
    private Boolean esDeseado;
    private Double scoreRelevancia;

    // Constructores
    public ProductoCanastaDTO() {}

    public ProductoCanastaDTO(Long id, String nombre, BigDecimal precio, String marca,
                              String dieta, String urlImagen, String categoria,
                              Boolean esDeseado, Double scoreRelevancia) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.marca = marca;
        this.dieta = dieta;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.esDeseado = esDeseado;
        this.scoreRelevancia = scoreRelevancia;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getDieta() { return dieta; }
    public void setDieta(String dieta) { this.dieta = dieta; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Boolean getEsDeseado() { return esDeseado; }
    public void setEsDeseado(Boolean esDeseado) { this.esDeseado = esDeseado; }

    public Double getScoreRelevancia() { return scoreRelevancia; }
    public void setScoreRelevancia(Double scoreRelevancia) { this.scoreRelevancia = scoreRelevancia; }
}
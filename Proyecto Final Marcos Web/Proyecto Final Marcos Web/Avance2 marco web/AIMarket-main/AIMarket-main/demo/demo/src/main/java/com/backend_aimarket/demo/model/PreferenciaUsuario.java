package com.backend_aimarket.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "preferencias_usuarios")
public class PreferenciaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El tipo de preferencia es obligatorio")
    @Column(name = "tipo_preferencia", nullable = false, length = 50)
    private String tipoPreferencia;

    @NotBlank(message = "El valor de preferencia es obligatorio")
    @Column(nullable = false, length = 100)
    private String valor;

    // Constructores
    public PreferenciaUsuario() {}

    public PreferenciaUsuario(Usuario usuario, String tipoPreferencia, String valor) {
        this.usuario = usuario;
        this.tipoPreferencia = tipoPreferencia;
        this.valor = valor;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoPreferencia() { return tipoPreferencia; }
    public void setTipoPreferencia(String tipoPreferencia) { this.tipoPreferencia = tipoPreferencia; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}

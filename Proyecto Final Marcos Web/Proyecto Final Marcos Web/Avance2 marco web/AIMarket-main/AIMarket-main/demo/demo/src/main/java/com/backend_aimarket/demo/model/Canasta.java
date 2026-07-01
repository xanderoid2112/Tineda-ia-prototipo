package com.backend_aimarket.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "canastas")
public class Canasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal presupuesto;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "canasta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CanastaDetalle> detalles = new ArrayList<>();

    // Constructores
    public Canasta() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Canasta(Usuario usuario, BigDecimal presupuesto) {
        this();
        this.usuario = usuario;
        this.presupuesto = presupuesto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<CanastaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<CanastaDetalle> detalles) { this.detalles = detalles; }

    // Método helper para calcular total
    public void calcularTotal() {
        this.total = detalles.stream()
                .map(CanastaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

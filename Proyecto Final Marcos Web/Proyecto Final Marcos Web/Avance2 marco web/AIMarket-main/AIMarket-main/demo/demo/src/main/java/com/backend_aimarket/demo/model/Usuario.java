package com.backend_aimarket.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(length = 20)
    private String rol = "USER"; // USER o ADMIN


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreferenciaUsuario> preferencias = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Canasta> canastas = new ArrayList<>();

    // Constructores
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.rol = "USER";
    }

    public Usuario(String nombre, String email, String contrasena) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public List<PreferenciaUsuario> getPreferencias() { return preferencias; }
    public void setPreferencias(List<PreferenciaUsuario> preferencias) { this.preferencias = preferencias; }
    public List<Canasta> getCanastas() { return canastas; }
    public void setCanastas(List<Canasta> canastas) { this.canastas = canastas; }
}
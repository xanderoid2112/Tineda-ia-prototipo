package com.backend_aimarket.demo.service;

import com.backend_aimarket.demo.model.Usuario;
import com.backend_aimarket.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario crearUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioActualizado.getNombre());
                    usuario.setEmail(usuarioActualizado.getEmail());
                    if (usuarioActualizado.getContrasena() != null &&
                            !usuarioActualizado.getContrasena().isEmpty()) {
                        usuario.setContrasena(passwordEncoder.encode(usuarioActualizado.getContrasena()));
                    }
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario promoverAAdmin(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setRol("ADMIN");
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
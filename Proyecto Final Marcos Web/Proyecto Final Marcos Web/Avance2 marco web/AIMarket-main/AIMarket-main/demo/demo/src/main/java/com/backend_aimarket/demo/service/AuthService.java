package com.backend_aimarket.demo.service;

import com.backend_aimarket.demo.config.JwtUtil;
import com.backend_aimarket.demo.dto.AuthRequestDTO;
import com.backend_aimarket.demo.dto.AuthResponseDTO;
import com.backend_aimarket.demo.model.Usuario;
import com.backend_aimarket.demo.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO login(AuthRequestDTO authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(authRequest.getEmail());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

                return new AuthResponseDTO(
                        token,
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getRol());
            }

            throw new RuntimeException("Usuario no encontrado");

        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    public AuthResponseDTO register(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setRol("USER"); // Rol por defecto

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuarioGuardado.getEmail(), usuarioGuardado.getRol());

        return new AuthResponseDTO(
                token,
                usuarioGuardado.getId(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getRol());
    }

    public Usuario getUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        throw new RuntimeException("Usuario no autenticado");
    }
}

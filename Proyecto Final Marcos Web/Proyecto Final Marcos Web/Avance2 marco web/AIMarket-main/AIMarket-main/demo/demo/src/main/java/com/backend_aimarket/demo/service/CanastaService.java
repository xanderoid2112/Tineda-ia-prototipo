package com.backend_aimarket.demo.service;

import com.backend_aimarket.demo.dto.CanastaRequestDTO;
import com.backend_aimarket.demo.dto.CanastaResponseDTO;
import com.backend_aimarket.demo.model.Canasta;
import com.backend_aimarket.demo.model.CanastaDetalle;
import com.backend_aimarket.demo.model.Producto;
import com.backend_aimarket.demo.model.Usuario;
import com.backend_aimarket.demo.repository.CanastaRepository;
import com.backend_aimarket.demo.repository.ProductoRepository;
import com.backend_aimarket.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CanastaService {

    private static final Logger logger = LoggerFactory.getLogger(CanastaService.class);

    private final CanastaRepository canastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final IAService iaService;

    public CanastaService(CanastaRepository canastaRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository,
                          IAService iaService) {
        this.canastaRepository = canastaRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.iaService = iaService;
    }

    public CanastaResponseDTO generarCanastaInteligente(CanastaRequestDTO request) {
        logger.info("Generando canasta inteligente para usuario: {}", request.getUsuario_id());

        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuario_id())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar canasta usando IA
        CanastaResponseDTO canastaIA = iaService.generarCanastaIA(request);

        // Guardar canasta en base de datos
        guardarCanastaEnBD(usuario, request, canastaIA);

        return canastaIA;
    }

    private void guardarCanastaEnBD(Usuario usuario, CanastaRequestDTO request, CanastaResponseDTO canastaIA) {
        try {
            Canasta canasta = new Canasta(usuario, request.getPresupuesto());
            canasta.setTotal(canastaIA.getTotal());

            // Agregar detalles de la canasta
            canastaIA.getProductos().forEach(productoDTO -> {
                Optional<Producto> productoOpt = productoRepository.findById(productoDTO.getId());
                if (productoOpt.isPresent()) {
                    CanastaDetalle detalle = new CanastaDetalle(
                            canasta,
                            productoOpt.get(),
                            1 // cantidad por defecto
                    );
                    canasta.getDetalles().add(detalle);
                }
            });

            canastaRepository.save(canasta);
            logger.info("Canasta guardada en BD con ID: {}", canasta.getId());

        } catch (Exception e) {
            logger.error("Error guardando canasta en BD: {}", e.getMessage());
            // No lanzamos excepción para no afectar la respuesta al usuario
        }
    }
}

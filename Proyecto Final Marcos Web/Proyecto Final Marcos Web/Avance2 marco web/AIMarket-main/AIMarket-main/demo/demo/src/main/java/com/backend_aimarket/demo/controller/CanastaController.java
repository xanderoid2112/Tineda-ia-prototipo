package com.backend_aimarket.demo.controller;

import com.backend_aimarket.demo.dto.CanastaRequestDTO;
import com.backend_aimarket.demo.dto.CanastaResponseDTO;
import com.backend_aimarket.demo.service.CanastaService;
import com.backend_aimarket.demo.service.IAService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/canastas")
@CrossOrigin(origins = "*")
public class CanastaController {

    private static final Logger logger = LoggerFactory.getLogger(CanastaController.class);

    private final CanastaService canastaService;
    private final IAService iaService;

    public CanastaController(CanastaService canastaService, IAService iaService) {
        this.canastaService = canastaService;
        this.iaService = iaService;
    }

    @PostMapping("/generar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CanastaResponseDTO> generarCanastaInteligente(
            @Valid @RequestBody CanastaRequestDTO request) {

        logger.info("Recibida solicitud para generar canasta - Usuario: {}, Presupuesto: {}",
                request.getUsuario_id(), request.getPresupuesto());

        try {
            CanastaResponseDTO respuesta = canastaService.generarCanastaInteligente(request);
            logger.info("Canasta generada exitosamente - Total productos: {}, Total precio: {}",
                    respuesta.getProductos().size(), respuesta.getTotal());
            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            logger.error("Error generando canasta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error inesperado generando canasta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/salud-ia")
    public ResponseEntity<Map<String, Object>> verificarSaludIAService() {
        logger.info("Verificando salud del servicio de IA");

        Map<String, Object> respuesta = new HashMap<>();
        boolean iaDisponible = iaService.verificarConexionIA();

        respuesta.put("iaDisponible", iaDisponible);
        respuesta.put("timestamp", System.currentTimeMillis());
        respuesta.put("servicio", "Supermercado Spring Boot");

        if (iaDisponible) {
            respuesta.put("mensaje", "Servicio de IA conectado correctamente");
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "Servicio de IA no disponible");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "API de Canastas funcionando correctamente");
        respuesta.put("timestamp", String.valueOf(System.currentTimeMillis()));
        respuesta.put("version", "1.0.0");
        return ResponseEntity.ok(respuesta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Solicitud inválida: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Solicitud inválida");
        error.put("mensaje", ex.getMessage());
        error.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.badRequest().body(error);
    }
}
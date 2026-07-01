package com.backend_aimarket.demo.service;

import com.backend_aimarket.demo.dto.CanastaRequestDTO;
import com.backend_aimarket.demo.dto.CanastaResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IAService {

    private static final Logger logger = LoggerFactory.getLogger(IAService.class);

    @Value("${ia.service.url:http://localhost:8000}")
    private String iaServiceUrl;

    private final RestTemplate restTemplate;

    public IAService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CanastaResponseDTO generarCanastaIA(CanastaRequestDTO request) {
        try {
            String url = iaServiceUrl + "/generar_canasta";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CanastaRequestDTO> entity = new HttpEntity<CanastaRequestDTO>(request, headers);

            logger.info("Enviando solicitud a IA Service: {}", url);

            ResponseEntity<CanastaResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, CanastaResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Canasta generada exitosamente por IA");
                return response.getBody();
            } else {
                logger.error("Error en respuesta de IA Service: {}", response.getStatusCode());
                throw new RuntimeException("Error al generar canasta con IA");
            }

        } catch (Exception e) {
            logger.error("Error comunicándose con el servicio de IA: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servicio de IA", e);
        }
    }

    public boolean verificarConexionIA() {
        try {
            String url = iaServiceUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("Servicio de IA no disponible: {}", e.getMessage());
            return false;
        }
    }
}

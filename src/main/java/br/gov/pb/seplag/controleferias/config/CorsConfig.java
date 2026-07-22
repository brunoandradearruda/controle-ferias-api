package br.gov.pb.seplag.controleferias.config; // <-- Ajuste para o pacote real do seu projeto

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Libera para todos os endpoints da sua API
                .allowedOrigins("http://localhost:5173") // Permite apenas o seu React (Vite)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Libera os verbos do nosso script
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
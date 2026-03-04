package com.thesis.backend.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Cloudinary configuration class.
 *
 * Δημιουργεί ένα singleton Cloudinary bean που χρησιμοποιείται
 * από τον HotelController για upload εικόνων.
 *
 * Οι τιμές διαβάζονται από environment variables:
 *   CLOUDINARY_CLOUD_NAME
 *   CLOUDINARY_API_KEY
 *   CLOUDINARY_API_SECRET
 * που ορίζονται στο Render dashboard (ή τοπικά στο .env).
 */
@Configuration
public class CloudinaryConfig {

    /** Το όνομα του Cloudinary account (π.χ. "manos-k") */
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    /** Το public API key από το Cloudinary dashboard */
    @Value("${cloudinary.api-key}")
    private String apiKey;

    /** Το secret API key — δεν εκτίθεται ποτέ στο frontend */
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Δημιουργεί το Cloudinary client object.
     * Το Spring το κρατάει ως singleton και το παρέχει
     * μέσω @Autowired σε όποιο class το χρειάζεται.
     *
     * @return ένα έτοιμο Cloudinary instance
     */
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}

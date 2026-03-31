package com.example.ssldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Configuration
@EnableWebSecurity
public class SslConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enforce HTTPS for all requests
            .requiresChannel(channel -> channel
                .anyRequest().requiresSecure())
            // Permit all API endpoints without authentication (demo app)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll())
            // Disable CSRF for REST API demo usage (Postman compatibility)
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public SSLContext sslContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(
            new ClassPathResource("keystore.p12").getInputStream(),
            "changeit".toCharArray()
        );

        TrustManagerFactory trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
}
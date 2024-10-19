package ru.netology.cloudstorage.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.netology.cloudstorage.authentication.AuthenticationTokenFilter;
import ru.netology.cloudstorage.authentication.AuthenticationEntryPoint;
import ru.netology.cloudstorage.authentication.AuthenticationLoginFilter;
import ru.netology.cloudstorage.authentication.LogoutHandler;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableWebMvc

public class WebSecurityConfigurer implements WebMvcConfigurer {
    @Value("${cors.credentials}")
    private Boolean credentials;

    @Value("${cors.origins}")
    private String origins;

    @Value("${cors.methods}")
    private String methods;

    @Value("${cors.headers}")
    private String headers;

    private final AuthenticationEntryPoint authEntryPoint;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(credentials);
        configuration.setAllowedOrigins(List.of(origins));
        configuration.setAllowedMethods(List.of(methods));
        configuration.setAllowedHeaders(List.of(headers));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationTokenFilter authenticationTokenFilter,
                                                   AuthenticationLoginFilter authenticationLoginFilter) throws Exception {

        http.addFilterAt(authenticationTokenFilter, BasicAuthenticationFilter.class)
                .addFilterAt(authenticationLoginFilter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler()));

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint));

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutHandler();
    }

}

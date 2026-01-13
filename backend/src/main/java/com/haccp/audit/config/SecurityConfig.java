package com.haccp.audit.config;

import com.haccp.audit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Autowired
        private UserService userService;

        @Bean
        public UserDetailsService userDetailsService() {
                return username -> {
                        var user = userService.findByUsername(username)
                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                        "User not found: " + username));

                        return org.springframework.security.core.userdetails.User.builder()
                                        .username(user.getUsername())
                                        .password(user.getPasswordHash())
                                        .authorities(user.getRoles().stream()
                                                        .map(role -> "ROLE_" + role.getName())
                                                        .toArray(String[]::new))
                                        .disabled(!user.getEnabled())
                                        .build();
                };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/css/**", "/js/**", "/actuator/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/auditor/**").hasAnyRole("AUDITOR", "ADMIN")
                                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                                                .logoutSuccessUrl("/login?logout=true")
                                                .permitAll())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/actuator/**"));

                return http.build();
        }
}

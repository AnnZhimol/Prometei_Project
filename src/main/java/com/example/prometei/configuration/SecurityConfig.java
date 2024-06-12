package com.example.prometei.configuration;

import com.example.prometei.configuration.jwt.JwtAuthenticationFilter;
import com.example.prometei.services.baseServices.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserService userService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        /*.requestMatchers(
                                "/auth/getByToken",
                                "/auth/check",
                                "/auth/editPasswordRemembered",
                                "/auth/sendCode",
                                "/auth/editPasswordEmail",
                                "/auth/sign-up",
                                "/auth/sign-in",
                                "/email/htmlEmail",
                                "/flight/get",
                                "/flight/getAirports",
                                "/flight/search",
                                "/flight/all",
                                "/flight/getFlightFavors",
                                "/chat/classification",
                                "/chat/places",
                                "/chat/negative",
                                "/chat/positive",
                                "/payment/cancelPay",
                                "/payment/confirmPay",
                                "/purchase/get",
                                "/purchase/all",
                                "/purchase/create",
                                "/statistic/heatMap",
                                "/ticket/get",
                                "/ticket/all",
                                "/ticket/getByFlight",
                                "/ticket/getByPurchase",
                                "/ticket/getByUser",
                                "/ticket/getAdditionalFavors",
                                "/ticket/addAdditionalFavors",
                                "/user/sendCodeForReturn",
                                "/user/checkCodeForReturn",
                                "/user/returnTicket"
                        ).permitAll()
                        .requestMatchers(
                                "/flight/create",
                                "/flight/addFlightFavors",
                                "/flight/edit",
                                "/statistic/ageTicket",
                                "/statistic/popularFavor",
                                "/statistic/questionCount",
                                "/statistic/averageCost",
                                "/statistic/countSales",
                                "/statistic/topRoute"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/purchase/getByUser",
                                "/user/get",
                                "/user/editUser"
                        ).authenticated()*/
                        .anyRequest().permitAll()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}

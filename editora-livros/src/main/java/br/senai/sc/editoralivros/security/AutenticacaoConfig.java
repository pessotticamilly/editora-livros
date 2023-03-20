package br.senai.sc.editoralivros.security;

import br.senai.sc.editoralivros.security.service.GoogleService;
import br.senai.sc.editoralivros.security.service.JpaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor
public class AutenticacaoConfig {

    // Configurações de segurança

    private JpaService jpaService;

    private GoogleService googleService;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jpaService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    // Configurações do Cors
    private CorsConfigurationSource configurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // Origens permitidas
        corsConfiguration.setAllowedOrigins(List.of("https://localhost:3000"));
        // Métodos que a aplicação poderá realizar
        corsConfiguration.setAllowedMethods(List.of("POST", "DELETE", "PUT", "GET"));
        // Configura a troca de informações de cookies entre as aplicações
        corsConfiguration.setAllowCredentials(true);
        // Configura os cabeçalhos de requisições
        corsConfiguration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    // Configura as autorizações de acesso

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(jpaService);
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        httpSecurity.authenticationProvider(provider);

        httpSecurity.authorizeRequests()
                // Libera o acesso sem autenticação para /login
                .antMatchers("/login", "/login/auth", "/logout").permitAll()
                // Delimita que o acesso a essa rota seja feita apenas por autores
                .antMatchers(HttpMethod.POST, "/editora-livros-api/livro").hasAnyAuthority("Autor")
                .anyRequest().authenticated();
        httpSecurity.csrf().disable();
        httpSecurity.cors().configurationSource(configurationSource());
        httpSecurity.logout().permitAll();
        // Não permite que a sessão do usuário fique ativa
        httpSecurity.sessionManagement().sessionCreationPolicy(
                SessionCreationPolicy.STATELESS);
        // Toda vez que haja uma requisição ela deverá passar pelo AutenticacaoFiltro (onde acontece a validação do TOKEN)
        httpSecurity.addFilterBefore(new AutenticacaoFiltro(new TokenUtils(), jpaService),
                UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    // Serve para poder fazer a injeção de dependência no AuthenticationManager na Controller
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration ac) throws Exception {
        return ac.getAuthenticationManager();
    }


}

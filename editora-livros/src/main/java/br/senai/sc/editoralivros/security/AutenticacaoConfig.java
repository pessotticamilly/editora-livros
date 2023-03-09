package br.senai.sc.editoralivros.security;

import br.senai.sc.editoralivros.security.service.GoogleService;
import br.senai.sc.editoralivros.security.service.JpaService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class AutenticacaoConfig {

    // Configurações de segurança

    private JpaService jpaService;

    private GoogleService googleService;

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jpaService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
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
                .antMatchers("/editora-livros-api/", "/editora-livros-api/usuarios",
                        "/editora-livros-api/pessoa").permitAll()
                // Determina que todas as demais requisições terão de ser autenticadas
                .anyRequest().authenticated();
        httpSecurity.csrf().disable().cors().disable();
        httpSecurity.formLogin().permitAll()
                .and()
                .logout().permitAll();
        // Não permite que a sessão do usuário fique ativa
        httpSecurity.sessionManagement().sessionCreationPolicy(
                SessionCreationPolicy.STATELESS);
        // Toda vez que haja uma requisição ela deverá passar pelo AutenticacaoFiltro
        // (onde acontece a validação do TOKEN)
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

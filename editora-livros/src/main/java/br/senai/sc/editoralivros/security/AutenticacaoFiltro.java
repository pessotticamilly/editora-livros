package br.senai.sc.editoralivros.security;

import br.senai.sc.editoralivros.security.service.JpaService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Faz a validação do TOKEN
@AllArgsConstructor
public class AutenticacaoFiltro extends OncePerRequestFilter {

    private TokenUtils tokenUtils;
    private JpaService jpaService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/login/auth")
                || request.getRequestURI().equals("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenUtils.buscarCookie(request);
        Boolean valido = tokenUtils.validarToken(token);

        if (valido) {
            Long usuarioCPF = tokenUtils.getUsuarioCpf(token);
            UserDetails usuario = jpaService.loadUserByCpf(usuarioCPF);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usuario.getUsername(),
                    null, usuario.getAuthorities());
            // Define o usuário autenticado
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}

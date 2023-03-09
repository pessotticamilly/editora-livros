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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Verifica se exite um TOKEN
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = null;
        }
        Boolean valido = tokenUtils.validarToken(token);
        if (valido) {
            Long usuarioCPF = tokenUtils.getUsuarioCpf(token);
            UserDetails usuario = jpaService.loadUserByUsername(usuarioCPF.toString());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(usuario.getUsername(),
                            null, usuario.getAuthorities());
            // Define o usuário autenticado
            SecurityContextHolder.getContext().setAuthentication(
                    usernamePasswordAuthenticationToken
            );
            // Esses paths não precisam passar por autenticação
        } else if (!request.getRequestURI().equals("/editora-livros-api/login") ||
                !request.getRequestURI().equals("/editora-livros-api/usuarios")) {
            response.setStatus(401);
        }
        filterChain.doFilter(request, response);
    }
}

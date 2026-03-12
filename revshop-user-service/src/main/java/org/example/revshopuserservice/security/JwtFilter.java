package org.example.revshopuserservice.security;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List;
@Component public class JwtFilter extends OncePerRequestFilter
{ @Autowired private JwtProvider jwtProvider;
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    { String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
        { String token = header.substring(7);
            try { var claims = Jwts.parserBuilder() .setSigningKey(jwtProvider.getKey()) .build() .parseClaimsJws(token) .getBody();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null)
                { UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( email, null, List.of(new SimpleGrantedAuthority(role)) );
                    authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails(request) );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } }
            catch (Exception e)
            { response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); return;
            } }
        filterChain.doFilter(request, response);
    } }
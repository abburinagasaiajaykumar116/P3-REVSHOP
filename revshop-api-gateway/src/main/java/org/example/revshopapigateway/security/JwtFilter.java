package org.example.revshopapigateway.security;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono; import java.util.List;
@Component public class JwtFilter implements WebFilter {
    @Autowired private JwtProvider jwtProvider;
    @Override public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        String path = exchange.getRequest().getURI().getPath();
        if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
            return chain.filter(exchange);
        }
        if (path.startsWith("/auth") || path.startsWith("/user/security-question") || path.startsWith("/user/forgot-password") || path.startsWith("/products") || path.startsWith("/categories") || path.startsWith("/reviews/product/")) { return chain.filter(exchange); } String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION); if (header == null || !header.startsWith("Bearer "))
        { exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } try {
            String token = header.substring(7);
            var claims = Jwts.parserBuilder() .setSigningKey(jwtProvider.getKey()) .build() .parseClaimsJws(token) .getBody(); String email = claims.getSubject(); String role = claims.get("role", String.class);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken( email, null, List.of(new SimpleGrantedAuthority(role)) );
            return chain.filter(exchange) .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } catch (Exception e) {
            System.out.println("JWT FILTER EXCEPTION:");
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); 
        } 
    } 
}
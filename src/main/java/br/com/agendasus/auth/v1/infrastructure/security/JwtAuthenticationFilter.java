package br.com.agendasus.auth.v1.infrastructure.security;

import br.com.agendasus.auth.v1.domain.usecase.exceptions.AuthTokenException;
import br.com.agendasus.auth.v1.infrastructure.system.MessageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.agendasus.auth.v1.infrastructure.security.Constants.*;

@WebFilter(urlPatterns = "/api/count")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private LoginAccountDetailsService userDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private MessageSystem messageSystem;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String header = request.getHeader(HEADER_STRING);
            if (header == null || !header.startsWith(TOKEN_PREFIX)) {
                throw new AuthTokenException("error.invalid.token");
            }
            String authToken = header.replace(TOKEN_PREFIX, "");
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (username == null) {
                throw new AuthTokenException("error.invalid.token");
            }
            User userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthTokenException e) {
            logger.info(messageSystem.getMessage(e.getMessage()));
        } catch (Exception e) {
            logger.warn(e.getMessage());
            logger.error(messageSystem.getMessage("system.authorization"));
        }
        chain.doFilter(request, response);
    }

}

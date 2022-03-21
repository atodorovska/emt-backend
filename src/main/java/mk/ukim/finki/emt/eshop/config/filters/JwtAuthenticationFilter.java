package mk.ukim.finki.emt.eshop.config.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import mk.ukim.finki.emt.eshop.config.JwtAuthConstants;
import mk.ukim.finki.emt.eshop.model.User;
import mk.ukim.finki.emt.eshop.model.dto.UserDetailsDto;
import mk.ukim.finki.emt.eshop.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.emt.eshop.model.exceptions.UserNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        User creds = null;
        try {
            creds = new ObjectMapper().readValue(request.getInputStream(), User.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (creds ==null) {
            throw new UserNotFoundException("Invalid credentials");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(creds.getUsername());
        if (!passwordEncoder.matches(creds.getPassword(),userDetails.getPassword())) {
            throw new PasswordsDoNotMatchException();
        }
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),creds.getPassword(),userDetails.getAuthorities()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User userDetails = (User) authResult.getPrincipal();
        String token = JWT.create()
                .withSubject(new ObjectMapper().writeValueAsString(UserDetailsDto.of(userDetails)))
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtAuthConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(JwtAuthConstants.SECRET.getBytes()));
        response.addHeader(JwtAuthConstants.HEADER_STRING,JwtAuthConstants.TOKEN_PREFIX+token);
        response.getWriter().append(token);
    }
}

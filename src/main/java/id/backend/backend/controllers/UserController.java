package id.backend.backend.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import id.backend.backend.models.entities.Role;
import id.backend.backend.models.entities.User;
import id.backend.backend.services.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<User>>getUsers(){
    return ResponseEntity.ok().body(userService.getUsers());
  }

  @PostMapping("/user/save")
  public ResponseEntity<User>saveUser(@RequestBody User user){
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
    return ResponseEntity.created(uri).body(userService.saveUser(user));
  }

  @PostMapping("/role/save")
  public ResponseEntity<Role>saveRole(@RequestBody Role role){
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
    return ResponseEntity.created(uri).body(userService.saveRole(role));
  }

  @PostMapping("/role/addtouser")
  public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form){
    userService.addRoleToUser(form.getUsername(), form.getRoleName());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/user/{username}")
  public void removeOne(@PathVariable("username") String username){
    userService.removeOne(username);
  }

  private String getUsername(){
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetails) {
      String username = ((UserDetails)principal).getUsername();
      return username;
    } else {
      String username = principal.toString();
      return username;
    }    
  }

  @PostMapping("/user/phone")
  public ResponseEntity<?>updatePhoneNumberUser(@RequestBody PhoneToForm form){
    userService.updatePhoneNumberUser(form.getPhoneNumber(), getUsername());
    // URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/phone").toUriString());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response){
    String authorizationHeader = request.getHeader(AUTHORIZATION);
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        try {
          String refresh_token = authorizationHeader.substring("Bearer ".length());
          Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

          JWTVerifier verifier = JWT.require(algorithm).build();

          DecodedJWT decodedJWT = verifier.verify(refresh_token);
          String username = decodedJWT.getSubject();
          User user = userService.getUser(username);

          String access_token = JWT.create().withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .sign(algorithm);

          Map<String, String> tokens = new HashMap<>();
          tokens.put("access_token", access_token);
          tokens.put("refresh_token", refresh_token);

          response.setContentType(APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), tokens);

        } catch (Exception exception) {
          response.setHeader("error", exception.getMessage());
          response.setStatus(FORBIDDEN.value());
          Map<String, String> error = new HashMap<>();
          error.put("error_message", exception.getMessage());
          response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
          try {
            new ObjectMapper().writeValue(response.getOutputStream(), error);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } else {
        throw new RuntimeException("Refresh Token is missing");
      }

  }
}

@Data
  class RoleToUserForm{
    private String username;
    private String roleName;
  }

  @Data
  class PhoneToForm{
    // private String username;
    private String phoneNumber;
  }
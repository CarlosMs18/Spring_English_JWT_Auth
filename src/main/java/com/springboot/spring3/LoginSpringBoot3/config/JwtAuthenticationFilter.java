package com.springboot.spring3.LoginSpringBoot3.config;

import com.springboot.spring3.LoginSpringBoot3.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component //antoacion componente
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //8 creacio de este autenticacion filter para filtrar uno por uno los usuarios


    private final JwtService jwtService; //10 inyeccion de clase

    //23
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(  //llamara a cada filtro de la cadena cada vea que se ingrese
                                      @NonNull HttpServletRequest request,  //los parametros no debe de ser nulos
                                      @NonNull HttpServletResponse response,
                                      @NonNull FilterChain filterChain) throws ServletException, IOException {
                //9 necesitamso crear las cabceras de autorizacion
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String userEmail;
                if(authHeader == null || !authHeader.startsWith("Bearer ")){
                    filterChain.doFilter(request,response);  //pasamos la solicitud al siguiente filtro
                    return;
                }
                //en caso este bien, extraeremos el token
                jwt = authHeader.substring(7);
                userEmail = jwtService.extractUsername(jwt); //extraccion del correo electronico from JWT token, pasando como argumento el token

                //22
                if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){ //verificar que el usuario aun no este autenticado,no necesitamos realizar neuvamente tdoas las comprobacioenes y/o configuraciones

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    //27 validar si el token aun es valido
                    if(jwtService.isTokenValid(jwt, userDetails)){
                        //si es valido necesitamos crear un objeto de usuario, password, authentication
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, //COMO RECORDAREMOS CUANDO CREAMOS CREDECIALES LO CREAMOS NULOS
                                userDetails.getAuthorities()
                        ); //objeto necesario para Spring y el contexto para actualizar contexto de seguridad

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request) //crear detalles de solicitudes a partir de HTTP
                        ); //28 queremos dar mas detalles al token

                        //29 PASO FINAL ACTUALIZAR EL CONTEXTO DE SEGURIDAD
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
                //30
        filterChain.doFilter(request,response);
    }
}

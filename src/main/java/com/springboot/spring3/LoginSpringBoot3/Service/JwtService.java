package com.springboot.spring3.LoginSpringBoot3.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    //11 crecion de clase


    //13 GENERAR LLAVE PARA CODIGICIAR
    private static final String SECRET_KEY = "p19Xf4q/6jtjB5mws3Uq3ev6bcJ1FrQZ5QuL21zQXm/HxWWjygzx2xtGsRqUN2CX6aMA5EdYh1pibOZgAOSEUUprfmq+VoxO74g5wDGG3xUFgaKiq8xsGN+bKugRheh5eRFWAx1NFXnxBh8tHdGGjVJb2Vy/X16ujkXtOVL1Gw/3wgYI3/YF/BpR0CH3qfelG+dNF/47qUSoBhHG9IoxQ+y/KtU91uhbFIBMUyvLbIiDXK739b7D7EHEBgxP/yUsNfjyIkd4MEaKs/Bsq/M+Pt1Y2XyWKemiICQck479TDZprZCfFrkubYXeLqhZb5wYojzZj2C1cChc+fCesGbqUbcm2oxtAJwTFiIJ4V7j38o=";

    //16
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    //15 metodo que nos permite extraer un solo claim
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //18
    public String generateToken(UserDetails userDetails){ //metodo para generar token sin vlaims
        return generateToken(new HashMap<>(), userDetails); //enviando claims vacio
    }

    //17 metodo para generatToken
    public String generateToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) //configurar fecha de vencimiento
                .setExpiration(new Date(System.currentTimeMillis() +  1000 * 60 * 24)) //cuanto tiempo sera valido(1 dia)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //firmando token
                .compact(); //genrara y devolvera el token
    }


    //19 para validar token
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    //20
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //21
    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){//12. esxter todos los reclamos (Claims)
        return Jwts
                .parserBuilder() //codificar la llave secreta
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    //14 metodo para devolver llave
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

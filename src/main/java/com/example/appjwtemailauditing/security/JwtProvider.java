package com.example.appjwtemailauditing.security;

import com.example.appjwtemailauditing.entity.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtProvider {

    long expireTime = 36_000_000;
    Date expireDate = new Date(System.currentTimeMillis() + expireTime);
    String key = "SecretKey123";

    public String generateToken(String username, Set<Roles> roles) {
        String token = Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return token;
    }

    public String getEmailByToken(String token){
        try{
            String subject = Jwts
                    .parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return subject;
        }
        catch (Exception e){
            return null;
        }
    }
}

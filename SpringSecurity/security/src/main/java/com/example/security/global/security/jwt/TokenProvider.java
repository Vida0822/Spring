package com.example.security.global.security.jwt;

import com.example.security.global.security.jwt.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value; // lombok으로 자동인식 되는데 이걸로 바꿔주야함 !
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import io.jsonwebtoken.security.Keys;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth" ;
    private static final String BEARER_TYPE = "Bearer" ;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30 ; // access 토근 만료시간 30분 (훨씬 짧음)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7 ; // 유효기간 7일

    private final Key key ;

    public TokenProvider(@Value("${jwt.secret.key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey) ;
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Authentication authentication){
        String authorities = authentication.getAuthorities().stream() // 권한 정보들을 String Stream으로 쫙 가져와서
                .map(GrantedAuthority::getAuthority) // 그 권한 뭉치에서 돌아가면서 getAuthority해서 권한 객체로 꺼내고
                .collect(Collectors.joining(",")); // 각 권한들을 ','로 이어서 한 String 으로 반환
        long now = (new Date()).getTime() ;

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now+ACCESS_TOKEN_EXPIRE_TIME) ;
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())  // payload = "sub" : "name"
                .claim(AUTHORITIES_KEY , authorities) // payload "auth" : "ROLE_USER"
                .setExpiration(accessTokenExpiresIn) // payload "exp" : 151621022 (ex)
                .signWith(key, SignatureAlgorithm.HS512) // header "alg" : "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now+REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact() ;

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build() ;

    } // generateTokenDto

    public Authentication getAuthentication(String accessToken){
        // 토큰 복호화
        Claims claims = parseClaim(accessToken) ;

        // 클레임에서 권한 정보 가져오기
        if(claims.get(AUTHORITIES_KEY) == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다. ") ;
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority :: new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities) ;

        return new UsernamePasswordAuthenticationToken(principal,"",authorities) ;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalStateException e) {
            log.info("JWT 토큰이 잘못되었습니다. ");
        }
        return false;
    } // validateToken
    private Claims parseClaim(String accessToken){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(accessToken).getBody() ;
        }catch (ExpiredJwtException e){
            return e.getClaims() ;
        }
    } // parseClaim


}

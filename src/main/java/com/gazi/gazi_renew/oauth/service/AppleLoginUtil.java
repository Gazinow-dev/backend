package com.gazi.gazi_renew.oauth.service;

import com.gazi.gazi_renew.common.config.AppleProperties;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
@Service
@RequiredArgsConstructor
public class AppleLoginUtil {
    private final AppleProperties appleProperties;

    /**
     * client_secret 생성 Apple Document URL ‣
     * https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     *
     * @return client_secret(jwt)
     */
    public String generateClientSecret() {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, appleProperties.getKeyId())
                .setIssuer(appleProperties.getTeamId())
                .setAudience(appleProperties.getAuth())
                .setSubject(appleProperties.getClientId())
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .setIssuedAt(new Date())
                .signWith(readPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    /**
     * 파일에서 private key 획득
     *
     * @return Private Key
     */
    public PrivateKey readPrivateKey() {
        try {
            // 헤더와 푸터를 제거하고 Base64 인코딩된 키 데이터만 추출
            String privateKeyPEM = appleProperties.getPrivateKey()
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", ""); // 공백 및 줄바꿈 제거

            // Base64로 디코딩하여 바이트 배열로 변환
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);

            // PKCS#8 형식의 KeySpec 생성
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            // KeyFactory를 사용하여 PrivateKey 생성
            KeyFactory keyFactory = KeyFactory.getInstance("EC"); // 필요한 알고리즘으로 변경 가능 (예: "RSA")
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Error converting private key from String", e);
        }
    }

}

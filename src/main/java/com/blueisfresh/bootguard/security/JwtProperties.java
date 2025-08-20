package com.blueisfresh.bootguard.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for JWT.
 * <p>
 * These values are loaded from application.properties or application.yml
 * using the prefix <code>jwt</code>.
 * <p>
 * Example:
 * <pre>
 * jwt.secret=your-secret-key
 * jwt.expiration.milliseconds=900000
 * jwt.refresh-token-expiration.milliseconds=604800000
 * </pre>
 */

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private  String secret;
    private long expirationMilliseconds;
    private long refreshTokenExpirationMilliseconds;
}

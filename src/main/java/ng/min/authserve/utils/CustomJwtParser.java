package ng.min.authserve.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.*;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import io.jsonwebtoken.impl.crypto.JwtSignatureValidator;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.lang.Strings;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class CustomJwtParser extends DefaultJwtParser {
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private ObjectMapper objectMapper = new ObjectMapper();
    private byte[] keyBytes;
    private Key key;
    private SigningKeyResolver signingKeyResolver;
    private CompressionCodecResolver compressionCodecResolver = new DefaultCompressionCodecResolver();
    Claims expectedClaims = new DefaultClaims();
    private Clock clock;
    private long allowedClockSkewMillis;

    public CustomJwtParser() {
        this.clock = DefaultClock.INSTANCE;
        this.allowedClockSkewMillis = 0L;
    }

    public CustomJwtParser requireIssuedAt(Date issuedAt) {
        this.expectedClaims.setIssuedAt(issuedAt);
        return this;
    }

    public CustomJwtParser requireIssuer(String issuer) {
        this.expectedClaims.setIssuer(issuer);
        return this;
    }

    public JwtParser setSigningKey(String base64EncodedKeyBytes) {
        Assert.hasText(base64EncodedKeyBytes, "signing key cannot be null or empty.");
        this.keyBytes = TextCodec.BASE64.decode(base64EncodedKeyBytes);
        return this;
    }

    public Jwt parse(String jwt) throws ExpiredJwtException, MalformedJwtException, SignatureException {
        Assert.hasText(jwt, "JWT String argument cannot be null or empty.");
        String base64UrlEncodedHeader = null;
        String base64UrlEncodedPayload = null;
        String base64UrlEncodedDigest = null;
        int delimiterCount = 0;
        StringBuilder sb = new StringBuilder(128);
        char[] var7 = jwt.toCharArray();
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            char c = var7[var9];
            if(c == 46) {
                CharSequence tokenSeq = Strings.clean(sb);
                String token = tokenSeq != null?tokenSeq.toString():null;
                if(delimiterCount == 0) {
                    base64UrlEncodedHeader = token;
                } else if(delimiterCount == 1) {
                    base64UrlEncodedPayload = token;
                }

                ++delimiterCount;
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        if(delimiterCount != 2) {
            String msg = "JWT strings must contain exactly 2 period characters. Found: " + delimiterCount;
            throw new MalformedJwtException(msg);
        } else {
            if(sb.length() > 0) {
                base64UrlEncodedDigest = sb.toString();
            }

            if(base64UrlEncodedPayload == null) {
                throw new MalformedJwtException("JWT string '" + jwt + "' is missing a body/payload.");
            } else {
                Header header = null;
                CompressionCodec compressionCodec = null;
                String payload;
                if(base64UrlEncodedHeader != null) {
                    payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedHeader);
                    Map<String, Object> m = this.readValue(payload);
                    if(base64UrlEncodedDigest != null) {
                        header = new DefaultJwsHeader(m);
                    } else {
                        header = new DefaultHeader(m);
                    }

                    compressionCodec = this.compressionCodecResolver.resolveCompressionCodec((Header)header);
                }

                if(compressionCodec != null) {
                    byte[] decompressed = compressionCodec.decompress(TextCodec.BASE64URL.decode(base64UrlEncodedPayload));
                    payload = new String(decompressed, Strings.UTF_8);
                } else {
                    payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);
                }

                Claims claims = null;
                if(payload.charAt(0) == 123 && payload.charAt(payload.length() - 1) == 125) {
                    Map<String, Object> claimsMap = this.readValue(payload);
                    claims = new DefaultClaims(claimsMap);
                }

                if(base64UrlEncodedDigest != null) {
                    JwsHeader jwsHeader = (JwsHeader)header;
                    SignatureAlgorithm algorithm = null;
                    String object;
                    if(header != null) {
                        object = jwsHeader.getAlgorithm();
                        if(Strings.hasText(object)) {
                            algorithm = SignatureAlgorithm.forName(object);
                        }
                    }

                    if(algorithm == null || algorithm == SignatureAlgorithm.NONE) {
                        object = "JWT string has a digest/signature, but the header does not reference a valid signature algorithm.";
                        throw new MalformedJwtException(object);
                    }

                    if(this.key != null && this.keyBytes != null) {
                        throw new IllegalStateException("A key object and key bytes cannot both be specified. Choose either.");
                    }

                    if((this.key != null || this.keyBytes != null) && this.signingKeyResolver != null) {
                        object = this.key != null?"a key object":"key bytes";
                        throw new IllegalStateException("A signing key resolver and " + object + " cannot both be specified. Choose either.");
                    }

                    Key key = this.key;
                    if(key == null) {
                        byte[] keyBytes = this.keyBytes;
                        if(Objects.isEmpty(keyBytes) && this.signingKeyResolver != null) {
                            if(claims != null) {
                                key = this.signingKeyResolver.resolveSigningKey(jwsHeader, claims);
                            } else {
                                key = this.signingKeyResolver.resolveSigningKey(jwsHeader, payload);
                            }
                        }

                        if(!Objects.isEmpty(keyBytes)) {
                            Assert.isTrue(algorithm.isHmac(), "Key bytes can only be specified for HMAC signatures. Please specify a PublicKey or PrivateKey instance.");
                            key = new SecretKeySpec(keyBytes, algorithm.getJcaName());
                        }
                    }

                    Assert.notNull(key, "A signing key must be specified if the specified JWT is digitally signed.");
                    String jwtWithoutSignature = base64UrlEncodedHeader + '.' + base64UrlEncodedPayload;

                    JwtSignatureValidator validator;
                    try {
                        validator = this.createSignatureValidator(algorithm, (Key)key);
                    } catch (IllegalArgumentException var26) {
                        String algName = algorithm.getValue();
                        String msg = "The parsed JWT indicates it was signed with the " + algName + " signature algorithm, but the specified signing key of type " + key.getClass().getName() + " may not be used to validate " + algName + " signatures.  Because the specified signing key reflects a specific and expected algorithm, and the JWT does not reflect this algorithm, it is likely that the JWT was not expected and therefore should not be trusted.  Another possibility is that the parser was configured with the incorrect signing key, but this cannot be assumed for security reasons.";
                        throw new UnsupportedJwtException(msg, var26);
                    }

                    if(!validator.isValid(jwtWithoutSignature, base64UrlEncodedDigest)) {
                        String msg = "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.";
                        throw new SignatureException(msg);
                    }
                }

                Object body = claims != null?claims:payload;
                if(base64UrlEncodedDigest != null) {
                    return new DefaultJws((JwsHeader)header, body, base64UrlEncodedDigest);
                } else {
                    return new DefaultJwt((Header)header, body);
                }
            }
        }
    }
}
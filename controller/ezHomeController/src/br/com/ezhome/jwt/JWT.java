package br.com.ezhome.jwt;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class JWT {

   public static final String HS256 = "HmacSHA256", HS384 = "HmacSHA384", HS512 = "HmacSHA512";
   public static final String RS256 = "RS256", RS384 = "RS384", RS512 = "RS512";

   private byte[] secret;
   private String algorithm;

   private int notBeforeMillis, validUntilMillis;

   public JWT(byte[] secret, String algorithm) {
      this.secret = secret;
      this.algorithm = algorithm;
      this.notBeforeMillis = 0;
      this.validUntilMillis = 60 * 60 * 1000; // 1 hour
   }

   public JWT(byte[] secret) {
      this(secret, HS256);
   }

   public int getNotBeforeMillis() {
      return notBeforeMillis;
   }

   public void setNotBeforeMillis(int notBeforeMillis) {
      this.notBeforeMillis = notBeforeMillis;
   }

   public int getValidUntilMillis() {
      return validUntilMillis;
   }

   public void setValidUntilMillis(int validUntilMillis) {
      this.validUntilMillis = validUntilMillis;
   }

   private String urlsafeBase64Encode(byte[] input) {
      return new String(Base64.getUrlEncoder().encode(input), Charset.forName("UTF-8"));
   }

   private byte[] urlsafeBase64Decode(String input) {
      return Base64.getUrlDecoder().decode(input.getBytes());
   }

   public JSONObject validate(String token) throws JWTException {
      if (token == null) {
         throw new IllegalArgumentException("Token cannot be null");
      }
      String[] segments = token.split("\\.");
      if (segments.length != 3) {
         throw new IllegalArgumentException("Token must have all 3 segments " + segments.length + "\n " + segments[0]);
      }

      // TODO get algorithm from header
      // Verify the signature
      try {
         if (!segments[2].equals(encodedSignature(segments[0] + "." + segments[1], algorithm))) {
            throw new JWTSignatureException();
         }

         JSONObject result = new JSONObject(new String(urlsafeBase64Decode(segments[1]), "UTF-8"));
         // TODO verify expiration and notBefore
         long ctm = new Date().getTime();
         if (ctm < result.getLong("nbf")) {
            throw new JWTValidityException("Token not valid yet");
         }

         if (ctm > result.getLong("exp")) {
            throw new JWTValidityException("Token expired");
         }

         return result;

      } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException ex) {
         throw new JWTException(500, "Signature failure on server", ex);
      }
   }

   public String sign(JSONObject data) throws UnsupportedEncodingException, Exception {
      ArrayList<String> segments = new ArrayList<>();
      segments.add(encodedHeader(algorithm));
      segments.add(encodedPayload(data));
      segments.add(encodedSignature(join(segments, "."), algorithm));

      return join(segments, ".");
   }

   private String encodedHeader(String algorithm) throws UnsupportedEncodingException {
      JSONObject result = new JSONObject();
      result.put("typ", "JWT");
      result.put("alg", algorithm);
      return urlsafeBase64Encode(result.toString().getBytes("UTF-8"));
   }

   private String encodedPayload(JSONObject data) throws UnsupportedEncodingException {
      long iat = new Date().getTime();
      JSONObject result = new JSONObject();
      result.put("iss", "ezhome");
      result.put("iat", iat);
      result.put("nbf", iat + notBeforeMillis);
      result.put("exp", iat + validUntilMillis);
      result.put("jti", UUID.randomUUID().toString());
      result.put("data", data);
      return urlsafeBase64Encode(result.toString().getBytes("UTF-8"));
   }

   private String join(List<String> list, String glue) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < list.size(); i++) {
         if (i > 0) {
            sb.append(glue);
         }
         sb.append(list.get(i));
      }
      return sb.toString();
   }

   /**
    * Sign the header and payload
    */
   private String encodedSignature(String signingInput, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException {
      byte[] signature = sign(algorithm, signingInput, secret);
      return urlsafeBase64Encode(signature);
   }

   /**
    * Switch the signing algorithm based on input, RSA not supported
    */
   private static byte[] sign(String algorithm, String msg, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
      switch (algorithm) {
         case HS256:
         case HS384:
         case HS512:
            return signHmac(algorithm, msg, secret);
         case RS256:
         case RS384:
         case RS512:
         default:
            throw new UnsupportedOperationException("Unsupported signing method");
      }
   }

   /**
    * Sign an input string using HMAC and return the encrypted bytes
    */
   private static byte[] signHmac(String algorithm, String msg, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(secret, algorithm));
      return mac.doFinal(msg.getBytes());
   }
}

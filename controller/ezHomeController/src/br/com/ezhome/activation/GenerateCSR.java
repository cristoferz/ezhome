package br.com.ezhome.activation;

import java.security.*;
import java.io.*;
import java.util.Date;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x500.*;
import org.bouncycastle.asn1.pkcs.*;
import org.bouncycastle.openssl.*;
import org.bouncycastle.pkcs.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.crypto.util.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.bc.*;
import org.bouncycastle.operator.jcajce.*;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * Given a Keystore containing a private key and certificate and a Reader
 * containing a PEM-encoded Certificiate Signing Request (CSR), sign the CSR
 * with that private key and return the signed certificate as a PEM-encoded
 * PKCS#7 signedData object. The returned value can be written to a file and
 * imported into a Java KeyStore with "keytool -import -trustcacerts -alias
 * subjectalias -file file.pem"
 *
 * @param pemcsr a Reader from which will be read a PEM-encoded CSR (begins
 * "-----BEGIN NEW CERTIFICATE REQUEST-----")
 * @param validity the number of days to sign the Certificate for
 * @param keystore the KeyStore containing the CA signing key
 * @param alias the alias of the CA signing key in the KeyStore
 * @param password the password of the CA signing key in the KeyStore
 *
 * @return a String containing the PEM-encoded signed Certificate (begins
 * "-----BEGIN PKCS #7 SIGNED DATA-----")
 */
public class GenerateCSR {

   public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      KeyPair key = keyGen.generateKeyPair();
      PrivateKey priv = key.getPrivate();
      PublicKey pub = key.getPublic();
      String privateKey = new String(Base64.encode(priv.getEncoded(), 0, priv.getEncoded().length));
      String publicKey1 = new String(Base64.encode(pub.getEncoded(), 0, pub.getEncoded().length));
      String publicKey = new String(Base64.encode(publicKey1.getBytes(), 0, publicKey1.getBytes().length));
      System.out.println(publicKey);
      System.out.println(privateKey);
      return key;
   }

   public static void main(String[] args) throws OperatorCreationException, NoSuchAlgorithmException, FileNotFoundException, IOException, InvalidKeyException {
      KeyPair pair = generateKeyPair();
      JcaPKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(new X500Principal("CN=Requested Test Certificate"), pair.getPublic());
      JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
      ContentSigner signer = csBuilder.build(pair.getPrivate());
      PKCS10CertificationRequest csr = p10Builder.build(signer);
      FileWriter fw = new FileWriter("/tmp/test.csr");
        PEMWriter pm = new PEMWriter(fw);
        pm.writeObject(csr);
        pm.close();
        fw.close();
      
        
      PEMParser reader = new PEMParser(new FileReader("/tmp/test.csr"));
      JcaPKCS10CertificationRequest csrRead = new JcaPKCS10CertificationRequest((PKCS10CertificationRequest) reader.readObject());
      System.out.println(new String(csrRead.getPublicKey().getEncoded()));
   }
}

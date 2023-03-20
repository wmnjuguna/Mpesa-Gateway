package app.fortuneconnect.payments.Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CredentialsEncrypt {
    public static String generateSecurityCredentials(String certificate, String initiatorPassword) {
        try {
            InputStream is = new ByteArrayInputStream(certificate.getBytes());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);

            PublicKey pubKey = cert.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] cipherBytes = cipher.doFinal(initiatorPassword.getBytes());
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

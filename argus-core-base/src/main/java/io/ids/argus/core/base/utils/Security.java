package io.ids.argus.core.base.utils;

import org.apache.commons.lang3.RandomStringUtils;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Security {

    private static final String ALGORITHM     = "SHA256WithRSA";
    private static final String RSA           = "RSA";
    private static final String BEGIN_PRIVATE_LABEL = "-----BEGIN PRIVATE KEY-----\n";
    private static final String END_PRIVATE_LABEL = "-----END PRIVATE KEY-----\n";
    private static final String SEP           = "\n";
    private static final String X509          = "X.509";
    private static final String RANDOM_STR    = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_=;~";

    private Security() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sign string.
     *
     * @param privatePath the private path
     * @param source      the source
     * @return the string
     * @throws InvalidKeyException      the invalid key exception
     * @throws IOException              the io exception
     * @throws SignatureException       the signature exception
     * @throws InvalidKeySpecException  the invalid key spec exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static String sign(String privatePath, String source) throws
            InvalidKeyException,
            IOException,
            SignatureException,
            InvalidKeySpecException,
            NoSuchAlgorithmException {
        var privateKey = new String(Files.readAllBytes(Paths.get(privatePath)));
        privateKey = privateKey.replace(BEGIN_PRIVATE_LABEL, "");
        privateKey = privateKey.replace(END_PRIVATE_LABEL, "");
        privateKey = privateKey.replace(SEP, "");

        var priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        var factory = KeyFactory.getInstance(RSA);
        var key = factory.generatePrivate(priPKCS8);

        var signature = Signature.getInstance(ALGORITHM);
        signature.initSign(key);
        var random = RandomStringUtils.random(16, RANDOM_STR);
        source = source + "-" + random;
        signature.update(source.getBytes(StandardCharsets.UTF_8));
        var signed = signature.sign();
        var base64 = Base64.getEncoder().encodeToString(signed);
        return random.substring(0, 8) + base64 + random.substring(8);
    }

    /**
     * Verify boolean.
     *
     * @param publicPath the public path
     * @param source     the source
     * @param signed     the signed
     * @return the boolean
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws CertificateException     the certificate exception
     * @throws InvalidKeySpecException  the invalid key spec exception
     * @throws InvalidKeyException      the invalid key exception
     * @throws SignatureException       the signature exception
     * @throws FileNotFoundException    the file not found exception
     */
    public static boolean verify(String publicPath, String source, String secret)
            throws NoSuchAlgorithmException, CertificateException,
            InvalidKeySpecException, InvalidKeyException,
            SignatureException, FileNotFoundException {
        var signed = "";
        var random = "";
        random = secret.substring(0, 8) + secret.substring(secret.length() - 8);
        signed = secret.substring(8, secret.length() - 8);
        var certificate = (X509Certificate) CertificateFactory.getInstance(X509)
                .generateCertificate(new FileInputStream(publicPath));

        var keyFactory = KeyFactory.getInstance(RSA);
        var pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(certificate.getPublicKey().getEncoded()));
        var signature = Signature.getInstance(ALGORITHM);
        source = source + "-" + random;
        signature.initVerify(pubKey);
        signature.update(source.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signed));
    }
}

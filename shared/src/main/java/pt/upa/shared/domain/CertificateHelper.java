package pt.upa.shared.domain;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Contains helper for certificate manipulation
 */
public class CertificateHelper {
    public static final String DEFAULT_BASE_KEY_DIR = "../keys/";
    public static final String DIR_SEPARATOR = "/";
    public static final String CERT_EXT = ".cer";
    public static final String CERT_TYPE = "X.509";
    private static final String KEYSTORE_EXT = ".jks";

    // much secure, so wow
    private static final char[] KEYSTORE_PASS = "ins3cur3".toCharArray();
    private static final char[] KEY_PASS = "1nsecure".toCharArray();
    private static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";


    /**
     * Reads the certificate from path
     * @param certificatePath
     * @return
     * @throws Exception
     */
    public static Certificate readCertificateFile(String certificatePath) throws Exception {
        FileInputStream fis;
        try {
            fis = new FileInputStream(certificatePath);
            BufferedInputStream bis = new BufferedInputStream(fis);

            CertificateFactory cf = CertificateFactory.getInstance(CERT_TYPE);

            if (bis.available() > 0) {
                Certificate cert = cf.generateCertificate(bis);
                bis.close();
                fis.close();
                return cert;
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (CertificateException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return null;
    }

    public static Certificate readCertificateFromByteArray(byte[] cert) throws CertificateException {
        final CertificateFactory cf = CertificateFactory.getInstance(CERT_TYPE);
        return cf.generateCertificate(new ByteArrayInputStream(cert));
    }

    /**
     * Get private key associated with the server wit the specified name.
     * NOTE: this method is tightly coupled with the ./gen-keys.sh script, it's really here just for the
     * simplicity.
     * @param name name of the server whose private key to get
     * @return {@link PrivateKey} of the server identified by name
     */
    public static PrivateKey getPrivateKey(String name) throws CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // yes, this is tied to the naming in gen-keys.sh, but we're gonna keep it simple here
        final String path = DEFAULT_BASE_KEY_DIR + name + DIR_SEPARATOR + name + KEYSTORE_EXT;
        return getPrivateKeyFromKeyStore(path, KEYSTORE_PASS, name, KEY_PASS);
    }

    /**
     * Retrieves a {@link PrivateKey} from the keystore. This method is more generic than {@link #getPrivateKey(String)},
     * since it's not ./get-keys.sh dependent and is used by {@link #getPrivateKey(String)} internally.
     */
    public static PrivateKey getPrivateKeyFromKeyStore(String keyStoreFilePath, char[] keystorePwd, String keyAlias,
                                                       char[] keyPwd) throws CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = readKeyStoreFile(keyStoreFilePath, keystorePwd);
        return (PrivateKey) keyStore.getKey(keyAlias, keyPwd);
    }

    private static KeyStore readKeyStoreFile(String filePath, char[] keystorePwd) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            System.err.println("Keystore file <" + filePath + "> not found");
            return null;
        }
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(fis, keystorePwd);
        return keyStore;
    }

    public static byte[] makeDigitalSignature(String text, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        return makeDigitalSignature(text.getBytes(), privateKey);
    }

    public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initSign(privateKey);
        sig.update(bytes);
        return sig.sign();
    }

    public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(bytes);
        try {
            return sig.verify(cipherDigest);
        } catch (SignatureException e) {
            System.err.println("Error verifying signature: " + e.getMessage());
            return false;
        }
    }
}

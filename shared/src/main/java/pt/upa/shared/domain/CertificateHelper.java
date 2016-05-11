package pt.upa.shared.domain;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Contains helper for certificate manipulation
 */
public class CertificateHelper {
    private static final String CERT_TYPE = "X.509";

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
}

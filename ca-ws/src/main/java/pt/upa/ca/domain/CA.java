package pt.upa.ca.domain;

import pt.upa.ca.exception.CertificateNotFound_Exception;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;

public final class CA {
    private static final String DEFAULT_BASE_KEY_DIR = "../keys/";
    private static final String DIR_SEPARATOR = "/";
    private static final String CERT_EXT = ".cer";

    private static final String CERT_TYPE = "X.509";

    private final String BASE_KEY_DIR;

    public CA(String baseDir) {
        BASE_KEY_DIR = baseDir;
    }

    public CA() { this (DEFAULT_BASE_KEY_DIR); }

    public UpaCertificate getCertificateByName(String name) throws CertificateNotFound_Exception, CertificateException, IOException {
        final String certPath = BASE_KEY_DIR + name + DIR_SEPARATOR + name + CERT_EXT;
        return getCertificateByPath(certPath);

    }

    public UpaCertificate getCertificateByPath(String path) throws CertificateNotFound_Exception, CertificateException, IOException {
        Certificate cert = readCertificateFile(path);
        return new UpaCertificate(cert);
    }
    private Certificate readCertificateFile(String certificatePath) throws CertificateNotFound_Exception, CertificateException, IOException {
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
            throw new CertificateNotFound_Exception("Certificate with path " + certificatePath + " was not found");
        } catch (CertificateException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return null;
    }
}

package pt.upa.ca.domain;

import pt.upa.ca.exception.CertificateReadException;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class CA {
    private static final String DEFAULT_BASE_KEY_DIR = "../keys/";
    private static final String DIR_SEPARATOR = "/";
    private static final String CERT_EXT = ".cer";

    private final String BASE_KEY_DIR;

    public CA(String baseDir) {
        BASE_KEY_DIR = baseDir;
    }

    public CA() { this (DEFAULT_BASE_KEY_DIR); }

    public byte[] getCertificateByName(String name) throws CertificateReadException {
        final String certPath = BASE_KEY_DIR + name + DIR_SEPARATOR + name + CERT_EXT;
        return getCertificateByPath(certPath);

    }

    public byte[] getCertificateByPath(String path) throws CertificateReadException {
        return readCertificateFile(path);
    }
    private byte[] readCertificateFile(String certificatePath) throws CertificateReadException {
        try {
            return Files.readAllBytes(Paths.get(certificatePath));
        } catch (IOException e) {
            throw new CertificateReadException("Error reading " + certificatePath + " :" + e.getMessage());
        }
    }


}

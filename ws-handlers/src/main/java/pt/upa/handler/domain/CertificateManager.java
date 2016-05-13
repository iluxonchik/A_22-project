package pt.upa.handler.domain;


import com.sun.org.apache.regexp.internal.RE;
import pt.upa.ca.ws.CertificateReadException_Exception;
import pt.upa.ca.ws.cli.CAClient;
import pt.upa.shared.domain.CertificateHelper;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;

public class CertificateManager {

    private class CertificateTime {
        public Certificate cert;
        public long time;

        public CertificateTime(Certificate cert, long time) {
            this.cert = cert;
            this.time = time;
        }

    }
    private static boolean DEBUG = false;
    private static long CACHE_TIME = 10 * 1000; // 10 seconds
    private HashMap<String, CertificateTime> cache = new HashMap<>();

    public CertificateManager(int cacheTimeInSeconds) {
        CACHE_TIME = cacheTimeInSeconds;
    }

    public CertificateManager() { this(10 * 1000); /* 10 seconds*/ }

    public Certificate getCertificate(String name, CAClient ca) throws CertificateReadException_Exception, CertificateException {
        if (cache.containsKey(name)) {
            if (System.currentTimeMillis() - cache.get(name).time  <= CACHE_TIME) {
                // cache is valid
                log("[USE LOCAL CACHE] Got certificate from CA");
                return cache.get(name).cert;
            } else {
                return getCertificateFromCA(name, ca);
            }
        } else {
            return getCertificateFromCA(name, ca);
        }
    }

    private Certificate getCertificateFromCA(String name, CAClient ca) throws CertificateReadException_Exception, CertificateException {
        //CAClient ca = new CAClient();
        Certificate cert  = CertificateHelper
                .readCertificateFromByteArray(ca.getCertificate(name));
        cache.put(name, new CertificateTime(cert, System.currentTimeMillis()));
        log("[REMOTE CALL] Got certificate from CA");
        return cert;
    }

    // because loggers are too mainstream
    public void log(String msg) {
        if (DEBUG) {
            System.out.println("CertificateManager: " + msg);
        }
    }

}

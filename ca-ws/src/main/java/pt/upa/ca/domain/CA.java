package pt.upa.ca.domain;

public final class CA {
    private static final String DEFAULT_BASE_KEY_DIR = "../keys/";

    private final String BASE_KEY_DIR;

    public CA(String baseDir) {
        BASE_KEY_DIR = baseDir;
    }

    public CA() { this (DEFAULT_BASE_KEY_DIR); }


}

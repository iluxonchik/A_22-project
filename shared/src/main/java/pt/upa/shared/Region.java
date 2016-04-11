package pt.upa.shared;

import pt.upa.shared.exception.InvalidTransporterNameException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to deal with regions (destinations and origins). <br />
 *
 * <strong>North:</strong> Porto, Braga, Viana do Castelo, Vila Real, Bragança <br />
 * <strong>Center:</strong> Lisboa, Leiria, Santaŕem, Castelo Branco, Coimbra, Aveiro, Viseu, Guarda <br />
 * <strong>South:</strong> Setúbal, Evora, Portalegre, Beja, Faro <br />
 */
public class Region {
    public final static Set<String> NORTH = new HashSet<String>(Arrays.asList("Porto", "Braga", "Viana do Castelo",
            "Vila Real", "Bragança"));
    public final static Set<String> CENTER = new HashSet<String>(Arrays.asList("Lisboa", "Leiria", "Santaŕem",
            "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"));
    public final static Set<String> SOUTH = new HashSet<String>(Arrays.asList("Setúbal", "Evora", "Portalegre",
            "Beja", "Faro"));

    public static boolean isNorth(String value) { return NORTH.contains(value); }
    public static boolean isCenter(String value) { return CENTER.contains(value); }
    public static boolean isSouth(String value) { return SOUTH.contains(value); }

    // Helpers for Broker
    /**
     * Returns true if the provided region is known, false otherwise.
     * @param value the region to check
     * @return true if region is known, false otherwise.
     */

    // Helpers for Transporter
    public static boolean isKnownRegion(String value) { return isNorth(value) || isCenter(value) || isSouth(value); }

    /**
     * Returns true if an even-numbered transporter operates in the provided region, false otherwise.
     * @param value the region to check
     * @return true if the transporter operates in the specified region, false otherwise
     */
    public static boolean isKnownByEvenTransporter(String value) { return isNorth(value) || isCenter(value); }
    /**
     * Returns true if an odd-numbered transporter operates in the provided region, false otherwise.
     * @param value the region to check
     * @return true if the transporter operates in the specified region, false otherwise
     */
    public static boolean isKnownByOddTransporter(String value) { return isCenter(value) || isSouth(value); }

    /**
     * Checks if the given transporter name operates in the specified region. Assumes transporter names are in the form
     * UpaTransporterXXX, where XXX is an integer number.
     * @param name transporter's name
     * @param value region to check
     * @return true if the transporter operates in the specified region, false otherwise
     */
    public static boolean isKnownByTransporter(String name, String value) {
        return (isEvenTransporter(name)) ? isKnownByEvenTransporter(value) : isKnownByOddTransporter(value);
    }

    public static boolean isEvenTransporter(String name) {
        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(name);
        if (m.find()) {
            int number = Integer.valueOf(m.group());
            return number % 2 == 0;
        } else {
            throw new InvalidTransporterNameException("'" + name + "' is not a valid name for a transporter.");
        }
    }
}

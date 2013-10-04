package ch.hasselba.xpages.localization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import com.ibm.xsp.context.FacesContextEx;

/**
 * "Loader" class to access the entries
 * of a .property file
 * 
 * @author Sven Hasselbach
 * @version 1.0
 */
public class Locale {

    private Vector<LocaleEntry> locales;

    /**
     * @return vector containing the LocalEntry objects
     */
    public Vector<LocaleEntry> getLocales() {
        return locales;
    }

    /**
     * sets vector containing the LocalEntry objects
     * @param Vector
     */
    public void setLocales(Vector<LocaleEntry> locales) {
        this.locales = locales;
    }

    /**
     * wrapper for the static method call
     * 
     * @param fileName name of the property file to load
     */
    public void loadFile(final String fileName) {
        Map<String, String> m = getPropertiesFromFile(fileName);
        this.locales = parseMap(m);
    }

    /**
     * loads a property file and parses it to a key/value map
     * 
     * @param fileName name of the property file to load
     * @return Map containing the key/values
     * 
     * The loading routine is shamelessly copied from Ulrich Krause:
     * http://openntf.org/XSnippets.nsf/snippet.xsp?id=access-.properties-files
     */
    @SuppressWarnings("unchecked")
	public static Map<String, String> getPropertiesFromFile(String fileName) {
        Properties prop = new Properties();

        try {
            prop.load(FacesContextEx.getCurrentInstance().getExternalContext()
                    .getResourceAsStream(fileName));
            Map<String, String> map = new HashMap<String, String>((Map) prop);

            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * parses a property map and create a vector
     * with LocalEntry objects
     * 
     * @param map to parse
     * @return Vector containing the LocalEntry objects
     */
    public static Vector<LocaleEntry> parseMap(final Map<String, String> map) {

        // init new vector
        Vector<LocaleEntry> localEntries = new Vector<LocaleEntry>();
        String key;
        String value;

        // get key set for iteration
        Iterator<?> it = map.keySet().iterator();

        while (it.hasNext()) {

            // extract key & value
            key = (String) it.next();
            value = (String) map.get(key);

            // create new entry and add to vector
            LocaleEntry lEntry = new LocaleEntry();
            lEntry.setName(key);
            lEntry.setValue(value);
            localEntries.add(lEntry);

        }

        // return vector
        return localEntries;

    }

    /**
     * dumps current object data to console
     * Just for debugging
     * 
     * @category Debug
     */
    public void dump() {
        for (int i = 0; i < this.locales.size(); i++) {

        }

    }

}
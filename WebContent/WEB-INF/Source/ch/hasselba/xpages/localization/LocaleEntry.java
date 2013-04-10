package ch.hasselba.xpages.localization;

import java.util.UUID;

/**
 * Locale Entry of a locale file
 * 
 * @author Sven Hasselbach
 * @category Localization
 * @version 1.0
 * 
 */

public class LocaleEntry {

    private String id;
    private String name;
    private String value;

    /**
     * initializes the object and
     * sets an unique identifier
     */
    public LocaleEntry(){
        this.id = UUID.randomUUID().toString();
        this.name = "";
        this.value = "";
    }

    /**
     * returns unique identifier of the object
     * @return String unique id
     */
    public String getId() {
        return id;
    }

    /**
     * sets the unique identifier of the entry
     * @param id String unique id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * returns the name of the entry
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the entry
     * @param String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the value of the entry
     * @return String
     */
    public String getValue() {
        return value;
    }

    /**
     * sets the value of the entry
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
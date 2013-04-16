package ch.hasselba.xpages.localization;

import java.util.Vector;
import javax.faces.context.FacesContext;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NoteCollection;
import lotus.domino.NotesException;

public class DesignElements {

    private final String EMPTY_STRING = "";
    private final String[] FLAG_PROPERTIES = { "gC~4K2P", "gC~4K2", "gC~4;2" };
    private final String FIELD_$FLAGS = "$Flags";
    private final String FIELD_TITLE = "$TITLE";

    /**
     * returns Vector containing all property files of a database
     * 
     * No error handling included!
     * 
     * @category Domino
     * @author Sven Hasselbach
     * @category Tools
     * @version 0.2
     */
    public Vector<String> getPropertFileList() {

        FacesContext fc = FacesContext.getCurrentInstance();

        Vector<String> data = new Vector<String>();
        try {

            // get DB
            Database db = (Database) fc.getApplication().getVariableResolver()
                    .resolveVariable(fc, "database");

            // get all design docs
            NoteCollection nc = db.createNoteCollection(false);
            nc.selectAllDesignElements(true);
            nc.buildCollection();

            // process all notes
            String noteId = "";
            noteId = nc.getFirstNoteID();

            Document doc = null;
            // 
            while (!(EMPTY_STRING.equals(noteId))) {

                // get design doc
                doc = db.getDocumentByID(noteId);

                // check if its a property file
                for (int i = 0; i < FLAG_PROPERTIES.length; i++) {
                    if (FLAG_PROPERTIES[i].equals(doc
                            .getItemValueString(FIELD_$FLAGS))) {
                        // add to Vector
                        data.add(doc.getItemValueString(FIELD_TITLE));
                    }
                }

                // next one
                noteId = nc.getNextNoteID(noteId);

                // recycle doc
                recycleObject(doc);
            }

        } catch (NotesException e) {
            e.printStackTrace();
        }

        return data;

    }

    /**
     * recycles a domino document instance
     * 
     * @param lotus
     *            .domino.Base obj to recycle
     * @category Domino
     * @author Sven Hasselbach
     * @category Tools
     * @version 1.1
     */
    public static void recycleObject(lotus.domino.Base obj) {
        if (obj != null) {
            try {
                obj.recycle();
            } catch (Exception e) {
            }
        }
    }
}
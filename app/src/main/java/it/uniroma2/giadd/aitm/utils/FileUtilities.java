package it.uniroma2.giadd.aitm.utils;

import android.support.design.widget.Snackbar;

import it.uniroma2.giadd.aitm.R;

/**
 * Created by Alessandro Di Diego on 10/09/16.
 */

public class FileUtilities {

    private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};


    public static Character getInvalidCharacter(String filename) {
        for (Character character : ILLEGAL_CHARACTERS) {
            if (filename.indexOf(character) != -1) {
                return character;
            }
        }
        return null;
    }
}

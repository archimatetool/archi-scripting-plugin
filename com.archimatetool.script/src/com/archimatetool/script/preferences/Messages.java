package com.archimatetool.script.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.script.preferences.messages"; //$NON-NLS-1$

    public static String ScriptPreferencePage_0;

    public static String ScriptPreferencePage_1;

    public static String ScriptPreferencePage_2;

    public static String ScriptPreferencePage_3;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}

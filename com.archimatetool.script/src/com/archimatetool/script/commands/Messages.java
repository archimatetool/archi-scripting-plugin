package com.archimatetool.script.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.script.commands.messages"; //$NON-NLS-1$

    public static String CommandHandler_0;

    public static String CommandHandler_1;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}

package com.archimatetool.script.premium;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.script.premium.messages"; //$NON-NLS-1$

    public static String RestoreExampleScriptsHandler_0;

    public static String RestoreExampleScriptsHandler_1;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}

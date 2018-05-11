package com.archimatetool.script.dom.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.script.dom.ui.messages"; //$NON-NLS-1$

    public static String Browser_0;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}

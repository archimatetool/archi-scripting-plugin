package com.archimatetool.script.commandline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.script.commandline.messages"; //$NON-NLS-1$

    public static String RunScriptProvider_0;

    public static String RunScriptProvider_1;

    public static String RunScriptProvider_2;

    public static String RunScriptProvider_3;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}

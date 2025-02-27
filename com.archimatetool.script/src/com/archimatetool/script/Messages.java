package com.archimatetool.script;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.archimatetool.script.messages"; //$NON-NLS-1$

    public static String JSProvider_0;

    public static String JSProvider_1;

    public static String JSProvider_2;

    public static String JSProvider_3;

    public static String ScriptsContextMenuContributionItem_0;

    public static String WorkbenchNotRunningException_0;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}

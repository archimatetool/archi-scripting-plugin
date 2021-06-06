/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.premium;

import org.eclipse.ui.plugin.AbstractUIPlugin;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
public class ArchiScriptPremiumPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.archimatetool.script.premium"; //$NON-NLS-1$
    
    /**
     * The shared instance
     */
    public static ArchiScriptPremiumPlugin INSTANCE;

    public ArchiScriptPremiumPlugin() {
        INSTANCE = this;
    }

}

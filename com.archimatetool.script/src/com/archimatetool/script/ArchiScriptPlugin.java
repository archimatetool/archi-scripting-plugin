/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.preferences.IPreferenceConstants;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
public class ArchiScriptPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.archimatetool.script"; //$NON-NLS-1$
    
    /**
     * The shared instance
     */
    public static ArchiScriptPlugin INSTANCE;

    public ArchiScriptPlugin() {
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        INSTANCE = this;
    }

    /**
     * @return The folder where we store user scripts
     */
    public File getUserScriptsFolder() {
        // Get from preferences
        String path = getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER);
        
        if(StringUtils.isSet(path)) {
            return new File(path);
        }
        
        // Default
        path = getPreferenceStore().getDefaultString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER);
        return new File(path);
    }
    
}

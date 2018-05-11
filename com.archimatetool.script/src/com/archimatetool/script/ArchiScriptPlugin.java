/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.preferences.IPreferenceConstants;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
public class ArchiScriptPlugin extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "com.archimatetool.script"; //$NON-NLS-1$
    
    public static final String SCRIPT_EXTENSION = ".ajs";  //$NON-NLS-1$
    public static final String SCRIPT_WILDCARD_EXTENSION = "*.ajs";  //$NON-NLS-1$
    
    /**
     * The shared instance
     */
    public static ArchiScriptPlugin INSTANCE;

    public ArchiScriptPlugin() {
        INSTANCE = this;
    }

    /**
     * @return The folder where we store user scripts
     */
    public File getUserScriptsFolder() {
        // Get from preferences
        String path = getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER);
        
        if(StringUtils.isSet(path)) {
            File file = new File(path);
            if(file.canWrite()) {
                return file;
            }
        }
        
        // Default
        path = getPreferenceStore().getDefaultString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER);
        return new File(path);
    }
    
    public void earlyStartup() {
        // Do nothing
    }

}

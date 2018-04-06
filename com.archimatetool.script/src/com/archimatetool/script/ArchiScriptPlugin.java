/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.archimatetool.editor.ArchiPlugin;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
public class ArchiScriptPlugin extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "com.archimatetool.script"; //$NON-NLS-1$
    
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
        return new File(ArchiPlugin.INSTANCE.getUserDataFolder(), "scripts"); //$NON-NLS-1$
    }
    
    public void earlyStartup() {
        // Do nothing
    }

}

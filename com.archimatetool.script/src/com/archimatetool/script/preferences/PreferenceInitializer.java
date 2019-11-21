/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.script.ArchiScriptPlugin;



/**
 * Class used to initialize default preference values
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class PreferenceInitializer extends AbstractPreferenceInitializer implements IPreferenceConstants {

    @Override
    public void initializeDefaultPreferences() {
		IPreferenceStore store = ArchiScriptPlugin.INSTANCE.getPreferenceStore();
        
		if(PlatformUtils.isWindows()) {
	        store.setDefault(PREFS_EDITOR, "notepad.exe");
		}
		else if(PlatformUtils.isMac()) {
            store.setDefault(PREFS_EDITOR, "TextEdit");
        }
		else if(PlatformUtils.isLinux()) {
            store.setDefault(PREFS_EDITOR, "gedit");
        }
		
		store.setDefault(PREFS_DOUBLE_CLICK_BEHAVIOUR, 0);
		
		store.setDefault(PREFS_SCRIPTS_FOLDER, new File(ArchiPlugin.INSTANCE.getUserDataFolder(), "scripts").getAbsolutePath());
		
		store.setDefault(PREFS_CONSOLE_WORD_WRAP, true);
		store.setDefault(PREFS_CONSOLE_SCROLL_LOCK, false);
		
		store.setDefault(PREFS_REFRESH_UI_WHEN_RUNNING_SCRIPT, false);
		
		store.setDefault(PREFS_SCRIPTS_SUPPORT, 0);
    }
}

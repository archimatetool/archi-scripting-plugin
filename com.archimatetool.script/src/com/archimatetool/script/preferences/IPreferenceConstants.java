/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.preferences;




/**
 * Constant definitions for plug-in preferences
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public interface IPreferenceConstants {
    
    String PREFS_EDITOR = "scriptEditor";
    String PREFS_DOUBLE_CLICK_BEHAVIOUR = "doubleClickBehaviour";
    String PREFS_SCRIPTS_FOLDER = "scriptsFolder";
    
    String PREFS_CONSOLE_WORD_WRAP = "consoleWordWrap";
    String PREFS_CONSOLE_SCROLL_LOCK = "consoleScrollLock";
    
    String PREFS_REFRESH_UI_WHEN_RUNNING_SCRIPT = "refreshUIWhenRunningScript";
    
    String PREFS_SCRIPTS_SUPPORT = "scriptsSupport";
    
    int PREFS_JAVASCRIPT_ES5 = 0; 
    int PREFS_JAVASCRIPT_ES6 = 1;
    int PREFS_GROOVY = 2;
}

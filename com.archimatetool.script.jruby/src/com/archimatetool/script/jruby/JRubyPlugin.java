/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.jruby;

import org.eclipse.ui.plugin.AbstractUIPlugin;



/**
 * Activator
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class JRubyPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.archimatetool.script.jruby";
    
    // The shared instance
    private static JRubyPlugin instance;

    /**
     * @return the shared instance
     */
    public static JRubyPlugin getInstance() {
        return instance;
    }

    public JRubyPlugin() {
        instance = this;
    }
}

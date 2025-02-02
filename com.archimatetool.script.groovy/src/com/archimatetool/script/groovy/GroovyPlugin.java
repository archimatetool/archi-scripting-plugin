/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.groovy;

import org.eclipse.ui.plugin.AbstractUIPlugin;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class GroovyPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.archimatetool.script.groovy";
    
    /**
     * The shared instance
     */
    public static GroovyPlugin INSTANCE;

    public GroovyPlugin() {
        INSTANCE = this;
    }

}

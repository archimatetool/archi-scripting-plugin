/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.groovy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.script.IScriptEngineProvider;


/**
 * Groovy Provider
 */
@SuppressWarnings("nls")
public class GroovyProvider implements IScriptEngineProvider {
    
    public static final String ID = "com.archimatetool.script.provider.groovy";
    
    @Override
    public void run(File file, ScriptEngine engine) throws IOException, ScriptException {
        // Init script
        URL initURL = GroovyPlugin.getInstance().getBundle().getEntry("groovy/init.groovy");
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream())) {
            engine.eval(initReader);
        }
        
        // Evaluate the script
        try(FileReader scriptReader = new FileReader(file)) {
            engine.eval(scriptReader);
        }
    }

    @Override
    public ScriptEngine createScriptEngine() {
        return new ScriptEngineManager().getEngineByName("groovy");
    }
    
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return "Groovy";
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { ".groovy" };
    }

    @Override
    public Image getImage() {
        return IGroovyImages.ImageFactory.getImage(IGroovyImages.ICON_GROOVY);
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return IGroovyImages.ImageFactory.getImageDescriptor(IGroovyImages.ICON_GROOVY);
    }

    @Override
    public URL getNewFile() {
        return GroovyPlugin.getInstance().getBundle().getEntry("templates/new.groovy");
    }
}

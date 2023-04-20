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
public class GroovyProvider implements IScriptEngineProvider {
    
    public static String ID = "com.archimatetool.script.provider.groovy"; //$NON-NLS-1$
    
    @Override
    public void run(File file, ScriptEngine engine) throws IOException, ScriptException {
        // Init script
        init(engine);
        
        // Evaluate the script
        try(FileReader scriptReader = new FileReader(file.getAbsolutePath())) {
            engine.eval(scriptReader);
        }
    }

    @Override
    public void run(String script, ScriptEngine engine) throws IOException, ScriptException {
        init(engine);
        engine.eval(script);
    }
    
    // Initialize jArchi using the provided init.groovy script
    private void init(ScriptEngine engine) throws IOException, ScriptException {
        URL initURL = GroovyPlugin.INSTANCE.getBundle().getEntry("groovy/init.groovy"); //$NON-NLS-1$
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
            engine.eval(initReader);
        }
    }

    @Override
    public ScriptEngine createScriptEngine() {
        return new ScriptEngineManager().getEngineByName("groovy"); //$NON-NLS-1$
    }
    
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return "Groovy"; //$NON-NLS-1$
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { ".groovy" }; //$NON-NLS-1$
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
        return GroovyPlugin.INSTANCE.getBundle().getEntry("templates/new.groovy"); //$NON-NLS-1$
    }
}

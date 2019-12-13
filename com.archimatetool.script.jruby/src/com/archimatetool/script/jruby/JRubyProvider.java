/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.jruby;

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
 * JRuby Provider
 */
public class JRubyProvider implements IScriptEngineProvider {
    
    public static String ID = "com.archimatetool.script.provider.jruby"; //$NON-NLS-1$
    
    @Override
    public void run(File file, ScriptEngine engine) throws IOException, ScriptException {
        // Init script
        URL initURL = JRubyPlugin.INSTANCE.getBundle().getEntry("jruby/init.rb"); //$NON-NLS-1$
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
            engine.eval(initReader);
        }
        
        // Evaluate the script
        try(FileReader scriptReader = new FileReader(file.getAbsolutePath())) {
            engine.eval(scriptReader);
        }
    }

    @Override
    public ScriptEngine createScriptEngine() {
        return new ScriptEngineManager().getEngineByName("jruby"); //$NON-NLS-1$
    }
    
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return "JRuby"; //$NON-NLS-1$
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { ".rb" }; //$NON-NLS-1$
    }

    @Override
    public Image getImage() {
        return IJRubyImages.ImageFactory.getImage(IJRubyImages.ICON_JRUBY);
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return IJRubyImages.ImageFactory.getImageDescriptor(IJRubyImages.ICON_JRUBY);
    }

    @Override
    public URL getNewFile() {
        return JRubyPlugin.INSTANCE.getBundle().getEntry("templates/new.rb"); //$NON-NLS-1$
    }
}

package com.archimatetool.script;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.views.console.ConsoleOutput;

public class GroovyStarter implements ScriptStarter {

	@Override
	public void start(ScriptEngine engine, File file)  throws ScriptException, IOException {
		
		// Start the console
        ConsoleOutput.start();
        
		engine.getBindings(ScriptContext.ENGINE_SCOPE).put("out", System.out);
		engine.getBindings(ScriptContext.ENGINE_SCOPE).put("err", System.err);
		
		
        //Initialize jArchi using the provided init.js script
        URL initURL = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("groovy/init.groovy"); //$NON-NLS-1$
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
            engine.eval(initReader);
        }
    	
        // Initialise CommandHandler
        CommandHandler.init();
        
        if(ScriptFiles.isLinkedFile(file)) {
            file = ScriptFiles.resolveLinkFile(file);
        }
        
        // Normalize filename so that nashorn's load() can run it
        String scriptPath = PlatformUtils.isWindows() ? file.getAbsolutePath().replace('\\', '/') : file.getAbsolutePath();
        
        // Initialise RefreshUIHandler
        RefreshUIHandler.init();
        
        // Evaluate the script
        engine.eval(new FileReader(scriptPath));  //$NON-NLS-1$//$NON-NLS-2$
	}

}

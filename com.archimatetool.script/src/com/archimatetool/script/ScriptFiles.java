/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * ScriptFiles
 */
public class ScriptFiles {
	
    public static final String SCRIPT_EXTENSION = ".ajs";  //$NON-NLS-1$
    public static final String SCRIPT_WILDCARD_EXTENSION = "*.ajs";  //$NON-NLS-1$
    public static final String LINK_EXTENSION = ".link";  //$NON-NLS-1$
    
	public static boolean isScriptFile(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(SCRIPT_EXTENSION);
    }
	
    public static boolean isLinkedFile(File file) {
	    return file.isFile() && file.getName().toLowerCase().endsWith(LINK_EXTENSION);
	}
	
	public static File resolve(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new File(new String(bytes));
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return file;
	}
	
	public static void write(File linkFile, File linked) {
	    try {
            Files.write(linkFile.toPath(), linked.getAbsolutePath().getBytes());
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
	}
	
}

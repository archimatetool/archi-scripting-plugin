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
@SuppressWarnings("nls")
public class ScriptFiles {
	
    public static final String LINK_EXTENSION = ".link";
    
    public static final String HIDDEN_MARKER_FILE = ".hidden";
    
	public static boolean isScriptFile(File file) {
        return file != null && file.isFile() && IScriptEngineProvider.INSTANCE.getProviderForFile(file) != null;
    }
	
    public static boolean isLinkedFile(File file) {
	    return file != null && file.isFile() && file.getName().toLowerCase().endsWith(LINK_EXTENSION);
	}
	
	public static File resolveLinkFile(File file) throws IOException {
	    byte[] bytes = Files.readAllBytes(file.toPath());
	    return new File(new String(bytes));
	}
	
	public static void writeLinkFile(File linkFile, File linked) throws IOException {
	    Files.write(linkFile.toPath(), linked.getAbsolutePath().getBytes());
	}
	
}

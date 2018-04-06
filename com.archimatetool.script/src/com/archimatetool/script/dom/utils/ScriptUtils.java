/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 * Represents the ScriptUtils dom object
 */
public class ScriptUtils {
    
    public ScriptUtils() {
    }
    
    /**
     * Write text to file
     * @param text
     * @param path
     * @throws IOException
     */
    public void writeTextToFile(String text, String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        
        FileWriter writer = new FileWriter(file); 
        writer.write(text); 
        writer.flush();
        writer.close();
    }
}

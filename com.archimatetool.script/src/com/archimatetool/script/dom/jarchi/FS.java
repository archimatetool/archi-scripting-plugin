/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.jarchi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Comparator;

import com.archimatetool.script.dom.model.CurrentModel;

/**
 * File services
 * 
 * @author jbsarrodie
 */
public class FS {
    /**
     * Write text to file (using UTF-8)
     * @param path
     * @param text
     * @throws IOException
     */
    public void writeFile(String path, String text) throws IOException {
    	writeFile(path, text, "UTF-8"); //$NON-NLS-1$
    }
    
    /**
     * Write text to file
     * @param path
     * @param text
     * @param encoding use BASE64 to write a binary file
     * @throws IOException
     */
    public void writeFile(String path, String text, String encoding) throws IOException {
        if(encoding.equals("BASE64")) { //$NON-NLS-1$
            writeBinFile(path, text);
        }
        else {
            File file = new File(path);
            createParentFolder(file);

            try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), encoding)) {
                writer.write(text);
                writer.flush();
            }
        }
    }

    /**
     * Write a binary file
     * @param path
     * @param base64text
     * @throws IOException
     */
    private void writeBinFile(String path, String base64text) throws IOException {
        File file = new File(path);
        createParentFolder(file);
        
        try(FileOutputStream writer = new FileOutputStream(file)) {
            writer.write(Base64.getDecoder().decode(base64text));
            writer.flush();
        }
    }
    
    /**
     * Ensure parent folder exists by creating it
     */
    private boolean createParentFolder(File file) {
        File parent = file.getParentFile();
        return parent != null ? parent.mkdirs() : false;
    }
    
    /**
     * Removes all files from folder
     * @param path
     * @throws IOException
     */
    public void cleanFolder(String path) throws IOException {
    	CurrentModel model = new CurrentModel();
    	File file = new File(model.getPath()); // /<base>/.git/temp.archimate
    	File gitpath = file.getParentFile(); // /<base>/.git/
    	File base = gitpath.getParentFile(); // /<base>/.git/
    	File folder = new File(path);
    	if (folder.isDirectory() && isSubDirectory(base, folder) && !isSubDirectory(gitpath, folder)) {
	    	Path pathToBeDeleted = Path.of(path);
	
	    	Files.walk(pathToBeDeleted)
		        .sorted(Comparator.reverseOrder())
		        .map(Path::toFile)
		        .forEach(File::delete);
    	} else {
    		System.err.println("Could not empty folder '" + path +"', not a directory or sub-directory of '"+ base.toString()+"'");
    		//throw new IOException("Could not empty folder '" + path +"', not a directory or sub-directory of '"+ base.toString()+"'");
    	}
    	
    }
    
    /**
     * Checks, whether the child directory is a subdirectory of the base 
     * directory.
     *
     * @param base the base directory.
     * @param child the suspected child directory.
     * @return true, if the child is a subdirectory of the base directory.
     * @throws IOException if an IOError occured during the test.
     */
    private boolean isSubDirectory(File base, File child)
        throws IOException {
        base = base.getCanonicalFile();
        child = child.getCanonicalFile();

        File parentFile = child;
        while (parentFile != null) {
            if (base.equals(parentFile)) {
                return true;
            }
            parentFile = parentFile.getParentFile();
        }
        return false;
    }
    
}

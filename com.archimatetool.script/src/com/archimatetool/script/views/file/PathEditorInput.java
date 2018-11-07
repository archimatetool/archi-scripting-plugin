/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.file;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;

/**
 * EditorInput that stores a Path.
 */
public class PathEditorInput implements IPathEditorInput {
	
    /**
	 * The File
	 */
	private File fFile;
	
	/**
	 * The Path
	 */
	private IPath fPath;
	
	/**
	 * Creates an editor input based of the given path resource.
	 *
	 * @param path the IPath
	 */
	public PathEditorInput(IPath path) {
		if(path == null) {
			throw new IllegalArgumentException();
		}
		fPath = path;
		fFile = fPath.toFile();
	}
	
	/**
	 * Creates an editor input based of the given file resource.
	 *
	 * @param file the file
	 */
	public PathEditorInput(File file) {
		if(file == null) {
			throw new IllegalArgumentException();
		}
		fFile = file;
		fPath = new Path(file.getAbsolutePath());
	}

	@Override
    public int hashCode() {
		return fFile.hashCode();
	}
	
	@Override
    public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof PathEditorInput)) {
			return false;
		}
		
		PathEditorInput other = (PathEditorInput)obj;
		
		return fFile.equals(other.fFile);
	}
	
	@Override
    public boolean exists() {
		return fFile.exists();
	}
	
	@Override
    public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(fFile.toString());
	}
	
	@Override
    public String getName() {
		return fFile.getName();
	}
	
	@Override
    public String getToolTipText() {
		return fFile.toString();
	}
	
	@Override
    public IPath getPath() {
		return fPath;
	}

	/**
	 * @return The File
	 */
	public File getFile() {
		return fFile;
	}

	@Override
    public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
    public IPersistableElement getPersistable() {
		// no persistence
		return null;
	}
}

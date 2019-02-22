/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.components.TreeTextCellEditor;


/**
 * File Tree Viewer
 */
public abstract class FileTreeViewer extends TreeViewer {
    /**
     * The Root Folder we are exploring
     */
    private File fRootFolder;
    
    /**
     * Constructor
     */
    public FileTreeViewer(File rootFolder, Composite parent) {
        super(parent, SWT.MULTI);
        
        fRootFolder = rootFolder;
        
        setup();
        
        setContentProvider(new FileTreeContentProvider());
        setLabelProvider(getUserLabelProvider());
        
        fRootFolder.mkdirs();
        setInput(fRootFolder);
        
        ColumnViewerToolTipSupport.enableFor(this);
        
        //expandToLevel(ALL_LEVELS);
    }
    
    public void setRootFolder(File rootFolder) {
        fRootFolder = rootFolder;
        setInput(fRootFolder);
    }

    /**
     * Set things up.
     */
    protected void setup() {
        // Sort folders first, files second, alphabetical
        setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                File f1 = (File)e1;
                File f2 = (File)e2;
                if(f1.isDirectory() && !f2.isDirectory()) {
                    // Directory before non-directory
                    return -1;
                }
                else if(!f1.isDirectory() && f2.isDirectory()) {
                    // Non-directory after directory
                    return 1;
                }
                else {
                    // Alphabetic order otherwise
                    return f1.getAbsolutePath().compareToIgnoreCase(f2.getAbsolutePath());
                }
            }
        });
        
        // Cell Editor
        TreeTextCellEditor cellEditor = new TreeTextCellEditor(getTree());
        setColumnProperties(new String[]{ "col1" }); //$NON-NLS-1$
        setCellEditors(new CellEditor[]{ cellEditor });
        
        // Edit cell programmatically, not on mouse click
        TreeViewerEditor.create(this, new ColumnViewerEditorActivationStrategy(this){
            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }  
            
        }, ColumnViewerEditor.DEFAULT);
        
        setCellEditors(new CellEditor[]{ cellEditor });
        
        setCellModifier(new ICellModifier() {
            @Override
            public void modify(Object element, String property, Object value) {
                if(element instanceof TreeItem) {
                    Object data = ((TreeItem)element).getData();
                    if(data instanceof File) {
                        File renamedFile = new File(((File)data).getParent(), (String)value);
                        boolean ok = ((File)data).renameTo((renamedFile));
                        if(ok) {
                            refresh();
                            setSelection(new StructuredSelection(renamedFile));
                        }
                    }
                }
            }
            
            @Override
            public Object getValue(Object element, String property) {
                if(element instanceof File) {
                    return ((File)element).getName();
                }
                return null;
            }
            
            @Override
            public boolean canModify(Object element, String property) {
                return true;
            }
        });
    }
    
    protected IBaseLabelProvider getUserLabelProvider() {
        return new FileTreeLabelProvider(); 
    }
    
    /* 
     * Over-ride - make sure we have a folder!
     */
    @Override
    public void refresh() {
        fRootFolder.mkdirs();
        super.refresh();
    }
    
    /* 
     * Over-ride - make sure we have a folder!
     */
    @Override
    public void refresh(final Object element) {
        fRootFolder.mkdirs();
        super.refresh(element);
    }
    
    /**
     * Dispose of stuff
     */
    public void dispose() {
    }
    
    // ===============================================================================================
	// ===================================== Content Provider ========================================
	// ===============================================================================================
    
    /**
     * The Tree Model for the Tree.
     */
    protected class FileTreeContentProvider implements ITreeContentProvider {
        
        @Override
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        
        @Override
        public void dispose() {
        }
        
        @Override
        public Object[] getElements(Object parent) {
            return getChildren(parent);
        }
        
        @Override
        public Object getParent(Object child) {
            if(child instanceof File) {
                return ((File)child).getParentFile();
            }
            return null;
        }
        
        @Override
        public Object [] getChildren(Object parent) {
            if(parent instanceof File) {
                return ((File)parent).listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        try {
                            return !Files.isHidden(pathname.toPath());
                        }
                        catch(IOException ex) {
                            ex.printStackTrace();
                        }
                        return false;
                    }
                });
            }
            return new Object[0];
        }
        
        @Override
        public boolean hasChildren(Object parent) {
            if(parent instanceof File) {
                File f = (File)parent;
                return f.isDirectory() && f.listFiles().length > 0;
            }
            return false;
        }
    }
    
    // ===============================================================================================
	// ===================================== Label Provider ==========================================
	// ===============================================================================================

    protected class FileTreeLabelProvider extends CellLabelProvider {
        
        @Override
        public void update(ViewerCell cell) {
            if(cell.getElement() instanceof File) {
                File file = (File)cell.getElement();
                cell.setText(getText(file));
                cell.setImage(getImage(file));
            }
        }
        
        public String getText(File file) {
        	return file.getName();
        }
        
        public Image getImage(File file) {
            if(file.isDirectory()) {
                return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
            }
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
        }
    }
}

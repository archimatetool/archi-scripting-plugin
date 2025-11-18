/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.Collator;

import org.eclipse.jface.dialogs.MessageDialog;
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
import com.archimatetool.script.IArchiScriptImages;


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
        fRootFolder.mkdirs();
        
        setup();
        
        setContentProvider(new FileTreeContentProvider());
        setLabelProvider(getUserLabelProvider());
        
        ColumnViewerToolTipSupport.enableFor(this);
        
        setUseHashlookup(true);
        
        setInput(fRootFolder);
        //expandToLevel(ALL_LEVELS);
    }
    
    public void setRootFolder(File rootFolder) {
        fRootFolder = rootFolder;
        fRootFolder.mkdirs();
        setInput(fRootFolder);
    }

    /**
     * Set things up.
     */
    protected void setup() {
        // Sort folders first, files second, alphabetical
        setComparator(new ViewerComparator() {
            private final Collator collator = Collator.getInstance();
            {
                collator.setStrength(Collator.SECONDARY); // case-insensitive, accent-sensitive
            }

            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                File f1 = (File) e1;
                File f2 = (File) e2;

                if(f1.isDirectory() != f2.isDirectory()) {
                    return f1.isDirectory() ? -1 : 1;
                }

                return collator.compare(f1.getName(), f2.getName());
            }
        });
        
        // Cell Editor
        TreeTextCellEditor cellEditor = new TreeTextCellEditor(getTree());
        setColumnProperties(new String[]{ "col1" }); //$NON-NLS-1$
        setCellEditors(new CellEditor[]{ cellEditor });
        
        // Edit cell programmatically, not on mouse click
        TreeViewerEditor.create(this, new ColumnViewerEditorActivationStrategy(this) {
            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }  
            
        }, ColumnViewerEditor.DEFAULT);
        
        setCellModifier(new ICellModifier() {
            @Override
            public void modify(Object element, String property, Object value) {
                if(element instanceof TreeItem treeItem && treeItem.getData() instanceof File file && value instanceof String newValue) {
                    File renamedFile = new File(file.getParent(), newValue);
                    if(file.renameTo(renamedFile)) {
                        refresh();
                        setSelection(new StructuredSelection(renamedFile));
                    }
                    else {
                        MessageDialog.openError(getControl().getShell(), Messages.FileTreeViewer_0, Messages.FileTreeViewer_1);
                    }
                }
            }
            
            @Override
            public Object getValue(Object element, String property) {
                return element instanceof File file ? file.getName() : null;
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
    
    // ===============================================================================================
	// ===================================== Content Provider ========================================
	// ===============================================================================================
    
    /**
     * The Tree Model for the Tree.
     */
    protected class FileTreeContentProvider implements ITreeContentProvider {
        @Override
        public Object[] getElements(Object parent) {
            return getChildren(parent);
        }
        
        @Override
        public Object getParent(Object child) {
            return child instanceof File file ? file.getParentFile() : null;
        }
        
        @Override
        public Object[] getChildren(Object parent) {
            if(parent instanceof File file) {
                File[] files = file.listFiles(pathname -> {
                    try {
                        return !Files.isHidden(pathname.toPath());
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                });
                
                if(files != null) {
                    return files;
                }
            }
            
            return new Object[0];
        }
        
        @Override
        public boolean hasChildren(Object parent) {
            return parent instanceof File file && file.isDirectory() && file.listFiles().length > 0;
        }
    }
    
    // ===============================================================================================
	// ===================================== Label Provider ==========================================
	// ===============================================================================================

    protected static class FileTreeLabelProvider extends CellLabelProvider {
        
        @Override
        public void update(ViewerCell cell) {
            if(cell.getElement() instanceof File file) {
                cell.setText(getText(file));
                cell.setImage(getImage(file));
            }
            else {
                cell.setText(cell.getElement().toString());
            }
        }
        
        protected String getText(File file) {
        	return file.getName();
        }
        
        protected Image getImage(File file) {
            if(file.isDirectory()) {
                return IArchiScriptImages.ImageFactory.getImage(IArchiScriptImages.ICON_FOLDER);
            }
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
        }
    }
}

/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.script.ScriptFiles;




/**
 * Drag Drop Handler
 * 
 * @author Phillip Beauvoir
 */
public class ScriptsTreeViewerDragDropHandler {

    private StructuredViewer fViewer;
    
    /**
     * Drag operations we support
     */
    private int fDragOperations = DND.DROP_MOVE | DND.DROP_COPY;

    /**
     * Drop operations we support on the tree
     */
    private int fDropOperations = DND.DROP_MOVE | DND.DROP_COPY;

    /**
     * Whether we have a valid tree selection
     */
    private boolean fIsValidTreeSelection;
    
    // Can only drag local type
    Transfer[] sourceTransferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    
    // Can drop local and file types
    Transfer[] targetTransferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };
    
    public ScriptsTreeViewerDragDropHandler(StructuredViewer viewer) {
        fViewer = viewer;
        registerDragSupport();
        registerDropSupport();
    }
    
    /**
     * Register drag support that starts from the Tree
     */
    private void registerDragSupport() {
        fViewer.addDragSupport(fDragOperations, sourceTransferTypes, new DragSourceListener() {
            
            @Override
            public void dragFinished(DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(null);
                fIsValidTreeSelection = false; // Reset to default
            }

            @Override
            public void dragSetData(DragSourceEvent event) {
                // For consistency set the data to the selection even though
                // the selection is provided by the LocalSelectionTransfer
                // to the drop target adapter.
                event.data = LocalSelectionTransfer.getTransfer().getSelection();
            }

            @Override
            public void dragStart(DragSourceEvent event) {
                // Drag started from the Tree
                IStructuredSelection selection = (IStructuredSelection)fViewer.getSelection();
                fIsValidTreeSelection = isValidTreeSelection(selection);

                LocalSelectionTransfer.getTransfer().setSelection(selection);
                event.doit = true;
            }
        });
    }
    
    private void registerDropSupport() {
        fViewer.addDropSupport(fDropOperations, targetTransferTypes, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetEvent event) {
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragOperationChanged(DropTargetEvent event) {
                event.detail = getEventDetail(event);
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.detail = getEventDetail(event);
                
                if(event.detail == DND.DROP_NONE) {
                    event.feedback = DND.FEEDBACK_NONE;
                    return;
                }
                
                if(isFileDragOperation(event.currentDataType)) {
                    event.detail |= DND.DROP_COPY;
                }
                else {
                    event.detail |= DND.DROP_MOVE;
                }

                event.feedback |= DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
            }

            @Override
            public void drop(DropTargetEvent event) {
                doDropOperation(event);
            }

            @Override
            public void dropAccept(DropTargetEvent event) {
            }
            
            private int getEventDetail(DropTargetEvent event) {
                return isValidSelection(event) && isValidDropTarget(event) ? DND.DROP_MOVE : DND.DROP_NONE;
            }
            
        });
    }
    
    private boolean isValidSelection(DropTargetEvent event) {
        return fIsValidTreeSelection || isValidFileSelection(event);
    }

    /**
     * Determine whether we have a valid selection of objects dragged from the Tree
     */
    private boolean isValidTreeSelection(IStructuredSelection selection) {
        return selection != null && !selection.isEmpty();
    }
    
    /**
     * Determine whether we have a valid selection of objects dragged from the desktop
     */
    private boolean isValidFileSelection(DropTargetEvent event) {
        return isFileDragOperation(event.currentDataType);
    }

    private void doDropOperation(DropTargetEvent event) {
        //boolean move = event.detail == DND.DROP_MOVE;
        
        // Local
        if(isLocalTreeDragOperation(event.currentDataType)) {
            Object parent = getTargetParent(event);
            if(parent instanceof File) {
                IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
                moveTreeObjects((File)parent, selection.toArray());
            }
        }
        // External File
        else if(isFileDragOperation(event.currentDataType)) {
            addFileObjects(getTargetParent(event), (String[])event.data);
        }
    }
    
    /**
     * Add external file objects dragged from the desktop
     */
    private void addFileObjects(File parent, String[] paths) {
        if(parent == null || paths == null) {
            return;
        }
        
        boolean hasScriptFile = false;
        boolean doLink = false;
        
        // Do we have a script file?
        for(String path : paths) {
            File file = new File(path);
            if(ScriptFiles.isScriptFile(file)) {
                hasScriptFile = true;
                break;
            }
        }
        
        // If we do, offer to link as well as copy files
        if(hasScriptFile) {
            MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(),
                    Messages.ScriptsTreeViewerDragDropHandler_0,
                    null,
                    Messages.ScriptsTreeViewerDragDropHandler_1,
                    MessageDialog.QUESTION,
                    new String[] {
                        Messages.ScriptsTreeViewerDragDropHandler_2,
                        Messages.ScriptsTreeViewerDragDropHandler_3,
                        Messages.ScriptsTreeViewerDragDropHandler_4 },
                    0);
            
            int result = dialog.open();
            
            // cancel
            if(result == 2) {
                return;
            }
            
            // link
            if(result == 1) {
                doLink = true;
            }
        }
        
        for(String path : paths) {
            File file = new File(path);
            
            if(file.isFile()) {
                if(doLink && ScriptFiles.isScriptFile(file)) {
                    try {
                        ScriptFiles.writeLinkFile(new File(parent, FileUtils.getFileNameWithoutExtension(file) + ScriptFiles.LINK_EXTENSION), file);
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    File target = new File(parent, file.getName());
                    if(!target.exists()) {
                        try {
                            Files.copy(file.toPath(), target.toPath());
                        }
                        catch(IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        
        fViewer.refresh();
    }
    
    /**
     * Move Tree Objects
     */
    private void moveTreeObjects(File newParent, Object[] objects) {
        for(Object o : objects) {
            File file = (File)o;
            try {
                Files.move(file.toPath(), new File(newParent, file.getName()).toPath());
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            finally {
                fViewer.refresh();
            }
        }
    }
    
    /**
     * Determine the target parent from the drop event
     * 
     * @param event
     * @return
     */
    private File getTargetParent(DropTargetEvent event) {
        // Dropped on blank area = root folder
        if(event.item == null) {
            return (File)fViewer.getInput();
        }
        
        File fileDroppedOn = (File)event.item.getData();
        
        return fileDroppedOn.isDirectory() ? fileDroppedOn : fileDroppedOn.getParentFile();
    }

    /**
     * @return True if target is valid
     */
    private boolean isValidDropTarget(DropTargetEvent event) {
        // File from desktop
        if(isFileDragOperation(event.currentDataType)) {
            return true;
        }

        // Local Tree Selection...
        
        // Dragging onto a Folder
        File parent = getTargetParent(event);
        if(parent != null) {
            IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
            for(Object object : selection.toList()) {
                if(!canDropObject(object, (TreeItem)event.item)) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Return true if object can be dropped on a target tree item
     */
    private boolean canDropObject(Object object, TreeItem targetTreeItem) {
        if(targetTreeItem == null) {  // Root tree
            return true;
        }
        
        if(object == targetTreeItem.getData()) {  // Cannot drop onto itself
            return false;
        }
        
        // If moving a folder check that target folder is not a descendant of the source folder
        while((targetTreeItem = targetTreeItem.getParentItem()) != null) {
            if(targetTreeItem.getData() == object) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isLocalTreeDragOperation(TransferData dataType) {
        return LocalSelectionTransfer.getTransfer().isSupportedType(dataType);
    }
    
    private boolean isFileDragOperation(TransferData dataType) {
        return FileTransfer.getInstance().isSupportedType(dataType);
    }
}

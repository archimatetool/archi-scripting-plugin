/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.file;

import java.io.File;
import java.io.IOException;

import org.eclipse.help.IContextProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.script.IArchiScriptImages;


/**
 * Abstract File ViewPart for viewing files in a given system folder
 */
public abstract class AbstractFileView
extends ViewPart
implements IContextProvider {
    /**
     * The Tree Viewer
     */
    private FileTreeViewer fTreeViewer;
    
    /*
     * Actions
     */
    protected IAction fActionEdit;
    protected IAction fActionRename;
    protected IAction fActionDelete;
    protected IAction fActionSelectAll;
    protected IAction fActionRefresh;
    protected IAction fActionNewFile;
    protected IAction fActionNewFolder;
    protected IAction fActionProperties;
    

    /**
     * @return the Root Folder to display
     */
    public abstract File getRootFolder();
    
    /**
     * @return Tree Viewer
     */
    protected abstract FileTreeViewer createTreeViewer(Composite parent);
    
    
    @Override
    public void createPartControl(Composite parent) {
        // Create the Tree Viewer first
        fTreeViewer = createTreeViewer(parent);
        
        makeActions();
        hookContextMenu();
        makeLocalMenuActions();
        makeLocalToolBarActions();
        
        // Register us as a selection provider so that Actions can pick us up
        getSite().setSelectionProvider(getViewer());
        
        /*
         * Listen to Selections to update local Actions
         */
        getViewer().addSelectionChangedListener(event -> {
            updateActions(event.getStructuredSelection());
        });
        
        /*
         * Listen to Double-click Action
         */
        getViewer().addDoubleClickListener(event -> {
            handleDoubleClickAction();
        });
    }
    
    /**
     * Make local actions
     */
    protected void makeActions() {
        // New File
        fActionNewFile = new Action() {
            {
                ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
                setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
                setText(Messages.AbstractFileView_0);
                setToolTipText(Messages.AbstractFileView_1);
            }
            
            @Override
            public void run() {
                handleNewFileAction();
            }
        };
        
        // New Folder
        fActionNewFolder = new Action() {
            {
                ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
                setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
                setText(Messages.AbstractFileView_2);
                setToolTipText(Messages.AbstractFileView_3);
            }
            
            @Override
            public void run() {
                handleNewFolderAction();
            }
        };

        // Refresh
        fActionRefresh = new Action() {
            {
                setText(Messages.AbstractFileView_4);
                setToolTipText(Messages.AbstractFileView_4);
                setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_REFRESH));
                setActionDefinitionId("org.eclipse.ui.file.refresh"); //$NON-NLS-1$
            }
            
            @Override
            public void run() {
                handleRefreshAction();
            }
        };
        
        // Delete
        fActionDelete = new Action() {
            {
                ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
                setText(Messages.AbstractFileView_7);
                setActionDefinitionId("org.eclipse.ui.edit.delete"); // Ensures key binding is displayed //$NON-NLS-1$
                setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
                setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
                setEnabled(false);
            }
            
            @Override
            public void run() {
                handleDeleteAction();
            }
        };
        
        // Rename
        fActionRename = new Action() {
            {
                setText(Messages.AbstractFileView_8);
                setActionDefinitionId("org.eclipse.ui.edit.rename"); // Ensures key binding is displayed //$NON-NLS-1$
                setEnabled(false);
            }
            
            @Override
            public void run() {
                File file = (File)((IStructuredSelection)getViewer().getSelection()).getFirstElement();
                if(file != null && file.exists()) {
                    getViewer().editElement(file, 0);
                }
            }
        };
        
        // Edit
        fActionEdit = new Action() {
            {
                setText(Messages.AbstractFileView_9);
                setToolTipText(Messages.AbstractFileView_10);
                setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_EDIT));
                setEnabled(false);
            }
            
            @Override
            public void run() {
                handleEditAction();
            }
        };
        
        // Select All
        fActionSelectAll = new Action() {
            {
                setText(Messages.AbstractFileView_11);
                setActionDefinitionId("org.eclipse.ui.edit.selectAll"); // Ensures key binding is displayed //$NON-NLS-1$
            }
            
            @Override
            public void run() {
                getViewer().getTree().selectAll();
            }
        };
        
        
        // Properties
        fActionProperties = new Action(Messages.AbstractFileView_5) {
            @Override
            public void run() {
                ViewManager.showViewPart(ViewManager.PROPERTIES_VIEW, false);
            }
            
            @Override
            public String getActionDefinitionId() {
                return IWorkbenchCommandConstants.FILE_PROPERTIES; // Ensures key binding is displayed
            }
        };

        // Register the Keybindings for these actions
        IHandlerService service = getViewSite().getService(IHandlerService.class);
        service.activateHandler(fActionRefresh.getActionDefinitionId(), new ActionHandler(fActionRefresh));
        service.activateHandler(fActionRename.getActionDefinitionId(), new ActionHandler(fActionRename));
        service.activateHandler(fActionDelete.getActionDefinitionId(), new ActionHandler(fActionDelete));
        service.activateHandler(fActionSelectAll.getActionDefinitionId(), new ActionHandler(fActionSelectAll));
        service.activateHandler(fActionProperties.getActionDefinitionId(), new ActionHandler(fActionProperties));
    }

    /**
     * Hook into a right-click menu
     */
    protected void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#FileViewerPopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        
        menuMgr.addMenuListener(manager -> {
            fillContextMenu(manager);
        });
        
        Menu menu = menuMgr.createContextMenu(getViewer().getControl());
        getViewer().getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, getViewer());
    }
    
    /**
     * Make Any Local Bar Menu Actions
     */
    protected void makeLocalMenuActions() {
    }

    /**
     * Make Local Toolbar items
     */
    protected void makeLocalToolBarActions() {
    }
    
    /**
     * Fill context menu
     */
    protected void fillContextMenu(IMenuManager manager) {
    }

    /**
     * Update the Local Actions depending on the selection 
     * @param selection
     */
    public void updateActions(IStructuredSelection selection) {
        File file = (File)selection.getFirstElement();
        boolean isEmpty = selection.isEmpty();
        
        fActionDelete.setEnabled(!isEmpty);
        fActionRename.setEnabled(!isEmpty);
        fActionEdit.setEnabled(!isEmpty && !file.isDirectory() && file.exists());
        fActionProperties.setEnabled(!isEmpty);
    }
    
    /**
     * @return The Viewer
     */
    public FileTreeViewer getViewer() {
        return fTreeViewer;
    }
    
    @Override
    public void setFocus() {
        if(getViewer() != null) {
            getViewer().getControl().setFocus();
        }
    }

    /**
     * New Folder event happened
     */
    protected void handleNewFolderAction() {
        File parent = (File)getViewer().getStructuredSelection().getFirstElement();

        if(parent == null) {
            parent = getRootFolder();
        }
        else if(!parent.isDirectory()) {
            parent = parent.getParentFile();
        }
        
        if(parent.exists()) {
            NewFolderDialog dialog = new NewFolderDialog(getViewSite().getShell(), parent);
            if(dialog.open()) {
                File newFolder = dialog.getFolder();
                if(newFolder != null) {
                    newFolder.mkdirs();
                    getViewer().expandToLevel(parent, 1);
                    getViewer().refresh();
                }
            }
        }
    }
    
    /**
     * Refresh event happened
     */
    protected void handleRefreshAction() {
        getViewer().setRootFolder(getRootFolder());
        getViewer().refresh();
    }
    
    /**
     * Delete event happened
     */
    protected void handleDeleteAction() {
        IStructuredSelection selection = getViewer().getStructuredSelection();
        Object[] objects = selection.toArray();
        
        // Make sure we didn't get the empty selection
        if(objects.length == 0) {
            return;
        }
        
        // Confirmation dialog
        boolean ok = MessageDialog.openQuestion(
                getViewSite().getShell(),
                Messages.AbstractFileView_16,
                objects.length == 1 ?
                        Messages.AbstractFileView_17 
                        : 
                        Messages.AbstractFileView_18);
        
        if(!ok) {
            return;
        }
        
        // Store next node to be selected
        File sel = ((File)objects[0]).getParentFile();
        
        // Delete
        for(int i = 0; i < objects.length; i++) {
            File file = (File)objects[i];
            try {
                if(file.isDirectory()) {
                    FileUtils.deleteFolder(file);
                }
                else if(file.exists()) {
                    file.delete();
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        
        // Update tree
        getViewer().refresh();
        
        if(sel != null) {
            getViewer().setSelection(new StructuredSelection(sel));
        }
    }
    
    /**
     * Double click event happened
     */
    protected void handleDoubleClickAction() {
    }

    /**
     * Edit event happened
     */
    protected void handleEditAction() {
    }
    
    /**
     * New File event happened
     */
    protected void handleNewFileAction() {
    }
}

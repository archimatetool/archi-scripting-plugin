/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.scripts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.ScriptFiles;
import com.archimatetool.script.preferences.IPreferenceConstants;
import com.archimatetool.script.views.file.AbstractFileView;
import com.archimatetool.script.views.file.FileTreeViewer;
import com.archimatetool.script.views.file.NewFileDialog;
import com.archimatetool.script.views.file.PathEditorInput;



/**
 * File Viewer ViewPart for viewing files in a given system folder.
 */
public class ScriptsFileViewer
extends AbstractFileView  {
    
    public static String ID = ArchiScriptPlugin.PLUGIN_ID + ".scriptsView"; //$NON-NLS-1$
    public static String HELP_ID = ArchiScriptPlugin.PLUGIN_ID + ".scriptsViewHelp"; //$NON-NLS-1$
    
    private RunScriptAction fActionRun;
    private IAction fActionShowConsole;
    
    /**
     * Application Preferences Listener
     */
    private IPropertyChangeListener prefsListener = new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if(IPreferenceConstants.PREFS_SCRIPTS_FOLDER.equals(event.getProperty())) {
                handleRefreshAction();
            }
        }
    };

    
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        
        // Listen to Prefs
        ArchiScriptPlugin.INSTANCE.getPreferenceStore().addPropertyChangeListener(prefsListener);
    }
    
    @Override
    public File getRootFolder() {
        return ArchiScriptPlugin.INSTANCE.getUserScriptsFolder();
    }
    
    @Override
    protected FileTreeViewer createTreeViewer(Composite parent) {
        return new ScriptsTreeViewer(getRootFolder(), parent);
    }
    
    @Override
    protected void makeActions() {
        super.makeActions();
        
        // Run
        fActionRun = new RunScriptAction();
        fActionRun.setEnabled(false);
        
        // Script
        fActionNewFile.setText(Messages.ScriptsFileViewer_0);
        fActionNewFile.setToolTipText(Messages.ScriptsFileViewer_1);
        
        // Show Console
        fActionShowConsole = new ShowConsoleAction();
        
        // Icon
        fActionNewFile.setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_SCRIPT));
    }
    
    @Override
    protected void makeLocalToolBarActions() {
        super.makeLocalToolBarActions();

        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();

        manager.appendToGroup(IWorkbenchActionConstants.NEW_GROUP, new Separator("extra")); //$NON-NLS-1$
        
        manager.add(fActionRun);
        manager.add(new Separator());
        manager.add(fActionShowConsole);
    }
    
    @Override
    protected void makeLocalMenuActions() {
        IActionBars actionBars = getViewSite().getActionBars();
        
        // Local menu items go here
        IMenuManager manager = actionBars.getMenuManager();
        
        manager.add(new Action(Messages.ScriptsFileViewer_3, IAction.AS_CHECK_BOX) {
            {
                //setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_REFRESH_UI_WHEN_RUNNING));
                setChecked(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_REFRESH_UI_WHEN_RUNNING_SCRIPT));
                setToolTipText(getText());
            }
            
            @Override
            public void run() {
                ArchiScriptPlugin.INSTANCE.getPreferenceStore().setValue(IPreferenceConstants.PREFS_REFRESH_UI_WHEN_RUNNING_SCRIPT, isChecked());
            }
        });

    }
    
    @Override
    public void updateActions(ISelection selection) {
        super.updateActions(selection);
        
        File file = (File)((IStructuredSelection)selection).getFirstElement();
        fActionRun.setFile(file);
    }
    
    @Override
    protected void fillContextMenu(IMenuManager manager) {
        super.fillContextMenu(manager);
        boolean isEmpty = getViewer().getSelection().isEmpty();

        if(!isEmpty) {
            manager.appendToGroup(IWorkbenchActionConstants.EDIT_START, fActionRun);
        }
    }
    
    @Override
    protected void handleDoubleClickAction() {
        int option = ArchiScriptPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.PREFS_DOUBLE_CLICK_BEHAVIOUR);
        switch(option) {
            case 0:
                fActionRun.run();
                break;

            case 1:
                handleEditAction();
                break;
                
            default:
                break;
        }
    }
    
    @Override
    protected void handleEditAction() {
        File file = (File)((IStructuredSelection)getViewer().getSelection()).getFirstElement();

        if(file != null && file.isFile()) {
            if(ScriptFiles.isLinkedFile(file)) {
                try {
                    file = ScriptFiles.resolveLinkFile(file);
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            String editor = ArchiScriptPlugin.INSTANCE.getPreferenceStore().getString(IPreferenceConstants.PREFS_EDITOR);
            if(StringUtils.isSet(editor)) {
                try {
                    // Windows / Linux
                    String[] paths = new String[] { editor, file.getAbsolutePath() };
                    
                    // Mac
                    if(PlatformUtils.isMac()) {
                        paths = new String[] { "open", "-a", editor, file.getAbsolutePath() }; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    
                    Runtime.getRuntime().exec(paths);
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
                IWorkbenchPage page = window.getActivePage();
                PathEditorInput input = new PathEditorInput(file);
                
                try {
                    page.openEditor(input, IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
                    // Internal Editor
                    //page.openEditor(input, ScriptTextEditor.ID);
                }
                catch(PartInitException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    @Override
    /**
     * New File event happened
     */
    protected void handleNewFileAction() {
        File parent = (File)((IStructuredSelection)getViewer().getSelection()).getFirstElement();

        if(parent == null) {
            parent = getRootFolder();
        }
        else if(!parent.isDirectory()) {
            parent = parent.getParentFile();
        }
        
        if(parent.exists()) {
            NewFileDialog dialog = new NewFileDialog(getViewSite().getShell(), parent);
            dialog.setDefaultExtension(ScriptFiles.SCRIPT_EXTENSION);
            
            if(dialog.open()) {
                File newFile = dialog.getFile();
                if(newFile != null) {
                    // Copy new template file over
                    try {
                        URL urlNewFile = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("templates/new.ajs"); //$NON-NLS-1$
                        InputStream in = urlNewFile.openStream();
                        Files.copy(in, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        in.close();
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                    }
                    
                    // Refresh tree
                    getViewer().expandToLevel(parent, 1);
                    getViewer().refresh();
                    getViewer().setSelection(new StructuredSelection(newFile));
                    
                    // Edit file
                    handleEditAction();
                }
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        // Unregister to Prefs
        ArchiScriptPlugin.INSTANCE.getPreferenceStore().removePropertyChangeListener(prefsListener);
    }

    // =================================================================================
    //                       Contextual Help support
    // =================================================================================
    
    @Override
    public int getContextChangeMask() {
        return NONE;
    }

    @Override
    public IContext getContext(Object target) {
        return HelpSystem.getContext(HELP_ID);
    }

    @Override
    public String getSearchExpression(Object target) {
        return Messages.ScriptsFileViewer_2;
    }
}

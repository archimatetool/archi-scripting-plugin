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
import java.util.List;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.archimatetool.editor.actions.AbstractDropDownAction;
import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.IScriptEngineProvider;
import com.archimatetool.script.JSProvider;
import com.archimatetool.script.RefreshUICommandHandler;
import com.archimatetool.script.RunScriptCommandHandler;
import com.archimatetool.script.ScriptFiles;
import com.archimatetool.script.WorkbenchPartTracker;
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
    
    /*
     * Ensure part tracker is activated so that we can get active part that is not this part when this gains focus
     */
    static {
        WorkbenchPartTracker.INSTANCE.getActivePart();
    }
    
    public static String ID = ArchiScriptPlugin.PLUGIN_ID + ".scriptsView"; //$NON-NLS-1$
    public static String HELP_ID = ArchiScriptPlugin.PLUGIN_ID + ".scriptsViewHelp"; //$NON-NLS-1$
    
    private RunScriptAction fActionRun;
    private IAction fActionShowConsole;
    private MarkFolderHiddenAction fActionHideFolder;

    
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
        ArchiScriptPlugin.getInstance().getPreferenceStore().addPropertyChangeListener(prefsListener);
        
        // Register Help Context
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getViewer().getControl(), HELP_ID);
    }
    
    @Override
    public File getRootFolder() {
        return ArchiScriptPlugin.getInstance().getUserScriptsFolder();
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
        
        // Mark folder as hidden/visible in context menu
        fActionHideFolder = new MarkFolderHiddenAction();
        
        // Icon
        IScriptEngineProvider provider = IScriptEngineProvider.INSTANCE.getProviderByID(JSProvider.ID);
        fActionNewFile.setImageDescriptor(provider.getImageDescriptor());
    }
    
    @Override
    protected void makeLocalToolBarActions() {
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();

        manager.add(new Separator(IWorkbenchActionConstants.NEW_GROUP));
        
        List<IScriptEngineProvider> installedProviders = IScriptEngineProvider.INSTANCE.getInstalledProviders();
        
        // If we have more than one installed provider show a drop-down box
        if(installedProviders.size() > 1) {
            AbstractDropDownAction dropDownAction = new AbstractDropDownAction(Messages.ScriptsFileViewer_4) {
                @Override
                public ImageDescriptor getImageDescriptor() {
                    return IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_NEW);
                }
            };
            manager.add(dropDownAction);

            for(IScriptEngineProvider provider : installedProviders) {
                dropDownAction.add(new Action(NLS.bind(Messages.ScriptsFileViewer_5, provider.getName()), provider.getImageDescriptor()) {
                    @Override
                    public void run() {
                        handleNewScriptAction(provider);
                    }
                });
            }

            dropDownAction.add(new Separator());
            dropDownAction.add(fActionNewFolder);
        }
        // Else show simple menu items if we have only one provider
        else {
            IScriptEngineProvider provider = installedProviders.get(0);
            manager.add(new Action(NLS.bind(Messages.ScriptsFileViewer_6, provider.getName()), provider.getImageDescriptor()) {
                @Override
                public void run() {
                    handleNewScriptAction(provider);
                }
            });
            
            manager.add(fActionNewFolder);
        }

        manager.add(new Separator(IWorkbenchActionConstants.EDIT_START));
        
        manager.add(fActionEdit);
        
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
        
        // Refresh UI when running scripts Command
        CommandContributionItemParameter param = new CommandContributionItemParameter(PlatformUI.getWorkbench(),
                null, RefreshUICommandHandler.ID,
                null,
                null,
                null, null, Messages.ScriptsFileViewer_3, null, Messages.ScriptsFileViewer_3,
                CommandContributionItem.STYLE_CHECK,
                null, false);
        
        CommandContributionItem item = new CommandContributionItem(param);
        manager.add(item);
    }
    
    @Override
    public void updateActions(ISelection s) {
        super.updateActions(s);
        
        IStructuredSelection selection = (IStructuredSelection)s;
        
        fActionRun.setFile((File)selection.getFirstElement());        
        fActionHideFolder.setSelection(selection.toArray());
    }
    
    @Override
    protected void fillContextMenu(IMenuManager manager) {
        IStructuredSelection selection = (IStructuredSelection)getViewer().getSelection();

        IMenuManager newMenu = new MenuManager(Messages.ScriptsFileViewer_4, "new"); //$NON-NLS-1$
        manager.add(newMenu);

        for(IScriptEngineProvider provider : IScriptEngineProvider.INSTANCE.getInstalledProviders()) {
            newMenu.add(new Action(NLS.bind(Messages.ScriptsFileViewer_5, provider.getName()), provider.getImageDescriptor()) {
                @Override
                public void run() {
                    handleNewScriptAction(provider);
                }
            });
        }

        newMenu.add(new Separator());
        newMenu.add(fActionNewFolder);
        manager.add(new Separator());

        if(!selection.isEmpty()) {
            manager.add(new Separator(IWorkbenchActionConstants.EDIT_START));
            manager.add(fActionEdit);
            manager.add(fActionRun);
            manager.add(new Separator(IWorkbenchActionConstants.EDIT_END));
            manager.add(fActionDelete);
            manager.add(fActionRename);
            
            if(fActionHideFolder.shouldShow(selection.toArray())) {
                manager.add(fActionHideFolder);
                manager.add(new Separator());
            }
            
            // If selection is a script file show key bindings sub-menu
            if(selection.getFirstElement() instanceof File && ScriptFiles.isScriptFile((File)selection.getFirstElement())) {
                manager.add(new Separator());
                IMenuManager bindingsMenu = new MenuManager(Messages.ScriptsFileViewer_7, "bindings"); //$NON-NLS-1$
                manager.add(bindingsMenu);
                
                File file = (File)selection.getFirstElement();
                IPreferenceStore store = ArchiScriptPlugin.getInstance().getPreferenceStore();
                
                // Show the key binding slots
                for(String paramValue : RunScriptCommandHandler.getParameters().values()) {
                    String text = paramValue;

                    // Show the script already assigned, if any
                    File refFile = new File(store.getString(RunScriptCommandHandler.PREFS_PREFIX + paramValue));
                    if(refFile.exists()) {
                        text += " " + StringUtils.escapeAmpersandsInText(FileUtils.getFileNameWithoutExtension(refFile)); //$NON-NLS-1$
                    }
                    
                    // Show the accelerator text, if any
                    String accelText = RunScriptCommandHandler.getAcceleratorText(paramValue);
                    if(accelText != null) {
                        text += "\t" + accelText; //$NON-NLS-1$
                    }
                    
                    // The action updates preferences with the slot number and the file path
                    IAction action = new Action(text, IAction.AS_RADIO_BUTTON) {
                        @Override
                        public void run() {
                            // Already assigned to this one
                            if(file.equals(refFile)) {
                                return;
                            }
                            
                            // Ask user if the slot is already taken
                            if(refFile.exists()) {
                                if(!MessageDialog.openConfirm(getSite().getShell(), Messages.ScriptsFileViewer_7,
                                        Messages.ScriptsFileViewer_10 )) {
                                    return;
                                }
                            }
                            
                            // Reset old one, if any
                            String currentParamValue = RunScriptCommandHandler.getParameterValueForScriptFile(file);
                            if(currentParamValue != null) {
                                store.setToDefault(RunScriptCommandHandler.PREFS_PREFIX + currentParamValue);
                            }
                            
                            // Store new one
                            store.putValue(RunScriptCommandHandler.PREFS_PREFIX + paramValue, file.getAbsolutePath());
                        }
                    };
                    
                    bindingsMenu.add(action);
                    
                    // Set checked if the selected file == referenced file
                    action.setChecked(file.equals(refFile));
                }
                
                bindingsMenu.add(new Separator());
                
                // Unassign the key binding if set
                String paramValue = RunScriptCommandHandler.getParameterValueForScriptFile(file);
                
                IAction action = new Action(Messages.ScriptsFileViewer_8) {
                    @Override
                    public void run() {
                        if(paramValue != null) {
                            store.setToDefault(RunScriptCommandHandler.PREFS_PREFIX + paramValue);
                        }
                    }
                };
                bindingsMenu.add(action);
                action.setEnabled(paramValue != null);
                
                // Edit key bindings sub-menu
                action = new Action(Messages.ScriptsFileViewer_9) {
                    @Override
                    public void run() {
                        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getViewSite().getShell(),
                                "org.eclipse.ui.preferencePages.Keys", null, null); //$NON-NLS-1$
                        if(dialog != null) {
                            dialog.open();
                        }
                    }
                };
                bindingsMenu.add(action);
                
                manager.add(new Separator());
            }
        }
        
        manager.add(fActionRefresh);
        
        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    @Override
    protected void handleDoubleClickAction() {
        int option = ArchiScriptPlugin.getInstance().getPreferenceStore().getInt(IPreferenceConstants.PREFS_DOUBLE_CLICK_BEHAVIOUR);
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
            
            String editor = ArchiScriptPlugin.getInstance().getPreferenceStore().getString(IPreferenceConstants.PREFS_EDITOR);
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
    
    /**
     * New Script
     */
    protected void handleNewScriptAction(IScriptEngineProvider provider) {
        File parent = (File)((IStructuredSelection)getViewer().getSelection()).getFirstElement();

        if(parent == null) {
            parent = getRootFolder();
        }
        else if(!parent.isDirectory()) {
            parent = parent.getParentFile();
        }
        
        if(parent.exists()) {
            NewFileDialog dialog = new NewFileDialog(getViewSite().getShell(), parent,
                    NLS.bind(Messages.ScriptsFileViewer_6, provider.getName()));
            dialog.setDefaultExtension(provider.getSupportedFileExtensions()[0]);
            
            if(dialog.open()) {
                File newFile = dialog.getFile();
                if(newFile != null) {
                    // Copy new template file over
                    try {
                        URL urlNewFile = provider.getNewFile();
                        if(urlNewFile != null) {
                            InputStream in = urlNewFile.openStream();
                            Files.copy(in, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            in.close();
                        }
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
        ArchiScriptPlugin.getInstance().getPreferenceStore().removePropertyChangeListener(prefsListener);
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

/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.editor.utils.StringUtils;


/**
 * Contribute Menu Items to context menu
 * 
 * @author Phillip Beauvoir
 */
public class ScriptsContextMenuContributionItem extends ContributionItem implements IWorkbenchContribution {
    
    private MenuManager menuManager;
    
    public ScriptsContextMenuContributionItem() {
    }

    public ScriptsContextMenuContributionItem(String id) {
        super(id);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
    
    @Override
    public void fill(Menu menu, int index) {
        if(menuManager != null) {
            menuManager.dispose();
        }
        
        menuManager = new MenuManager();
        
        fillItems(menuManager, ArchiScriptPlugin.INSTANCE.getUserScriptsFolder().listFiles());
        
        for(IContributionItem item : menuManager.getItems()) {
            item.fill(menu, index++);
        }
    }

    private void fillItems(MenuManager menuManager, File[] files) {
        if(files == null) {
            return;
        }
        
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
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
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
            }
        });
        
        for(File file : files) {
            if(doShowFile(file)) {
                if(file.isDirectory()) {
                    MenuManager subMenu = new MenuManager(StringUtils.escapeAmpersandsInText(file.getName()));
                    subMenu.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
                    menuManager.add(subMenu);
                    fillItems(subMenu, file.listFiles());
                }
                else {
                    IScriptEngineProvider provider = IScriptEngineProvider.INSTANCE.getProviderForFile(file);
                    ImageDescriptor imageDescriptor = provider.getImageDescriptor();
                    
                    menuManager.add(new Action(StringUtils.escapeAmpersandsInText(FileUtils.getFileNameWithoutExtension(file)), imageDescriptor) {
                        @Override
                        public void run() {
                            try {
                                RunArchiScript runner = new RunArchiScript(file);
                                runner.run();
                            }
                            catch(Exception ex) {
                                MessageDialog.openError(null, Messages.ScriptsContextMenuContributionItem_0, ex.getMessage());
                            }
                        }
                    });
                }
            }
        }
    }
    
    private boolean doShowFile(File file) {
        if(file.isDirectory()) {
            for(File f : file.listFiles()) {
                // Don't show folder if marked as hidden
                if(ScriptFiles.HIDDEN_MARKER_FILE.equals(f.getName())) {
                    return false;
                }

                if(doShowFile(f)) {
                    return true;
                }
            }
            return false;
        }
        
        return IScriptEngineProvider.INSTANCE.getProviderForFile(file) != null;
    }
    
    @Override
    public void initialize(IServiceLocator serviceLocator) {
    }
}

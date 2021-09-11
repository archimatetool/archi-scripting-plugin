/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.premium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.views.scripts.ScriptsFileViewer;




/**
 * Command Action Handler to restore example scripts
 * 
 * @author Phillip Beauvoir
 */
public class RestoreExampleScriptsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        File targetExamplesFolder = new File(ArchiScriptPlugin.INSTANCE.getUserScriptsFolder(), "examples"); //$NON-NLS-1$
        if(targetExamplesFolder.exists() && targetExamplesFolder.list().length > 0) {
            boolean confirm = MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Archi", //$NON-NLS-1$
                    Messages.RestoreExampleScriptsHandler_0);
            if(!confirm) {
                return null;
            }
        }
        
        try {
            copyFiles("*.*js"); //$NON-NLS-1$
        }
        catch(IOException ex) {
            ex.printStackTrace();
            MessageDialog.openError(HandlerUtil.getActiveShell(event), "Archi", Messages.RestoreExampleScriptsHandler_1 + //$NON-NLS-1$
                        "\n\n" + ex.getMessage()); //$NON-NLS-1$
        }
        finally {
            ScriptsFileViewer viewer = (ScriptsFileViewer)HandlerUtil.getActivePart(event);
            if(viewer != null) {
                viewer.getViewer().refresh();
            }
        }
        
        return null;
    }

    private void copyFiles(String filePattern) throws IOException {
        // Note - if a directory has a "." in it, it is treated as a file!
        Enumeration<URL> enm = ArchiScriptPremiumPlugin.INSTANCE.getBundle().findEntries("examples", filePattern, true); //$NON-NLS-1$
        if(enm == null) {
            return;
        }
        
        while(enm.hasMoreElements()) {
            URL url = enm.nextElement();
            
            File file = new File(ArchiScriptPlugin.INSTANCE.getUserScriptsFolder(), url.getPath());
            file.getParentFile().mkdirs();
            
            try(InputStream in = url.openStream()) {
                Files.copy(in, new File(ArchiScriptPlugin.INSTANCE.getUserScriptsFolder(), url.getFile()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}

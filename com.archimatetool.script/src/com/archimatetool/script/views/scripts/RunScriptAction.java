/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.scripts;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.RunArchiScript;


/**
 * Run Script Action
 */
public class RunScriptAction extends Action {

    private IWorkbenchWindow fWindow;
    private File fFile;

    public RunScriptAction(IWorkbenchWindow window) {
        fWindow = window;
        setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_RUN_16));
        setText(Messages.RunScriptAction_0);
        setToolTipText(Messages.RunScriptAction_1);
    }
    
    public void setFile(File file) {
        fFile = file;
        setEnabled(file != null && !file.isDirectory() && file.exists());
    }

    @Override
    public void run() {
        if(isEnabled()) {
            RunArchiScript script = new RunArchiScript(fFile, fWindow);
            script.run();
        }
    }
}
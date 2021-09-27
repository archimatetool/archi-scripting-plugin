/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.script.views.console.ConsoleView;
import com.archimatetool.script.views.scripts.Messages;

/**
 * Show Scripts View
 * 
 * @author Phillip Beauvoir
 */
public class RunScriptHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ViewManager.showViewPart(ConsoleView.ID, false);
        String scriptName = event.getParameter("com.archimatetool.scripts.command.runScript.param1");
        if (scriptName.isEmpty()) {
        	return null;
        }
        try {
            File fFile = new File(ArchiScriptPlugin.INSTANCE.getUserScriptsFolder(), scriptName);
            RunArchiScript runner = new RunArchiScript(fFile);
            runner.run();
        }
        catch(Exception ex) {
            MessageDialog.openError(null, Messages.RunScriptAction_1, ex.getMessage());
        }
        return null;
    }


}

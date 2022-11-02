package com.archimatetool.script.cheatsheets;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.RunArchiScript;

public class RunArchiScriptCheatSheetAction
extends Action
implements ICheatSheetAction {

    @Override
    public void run(String[] params, ICheatSheetManager manager) {
        if(params != null && params.length > 0) {
        	File fFile = new File(ArchiScriptPlugin.INSTANCE.getUserScriptsFolder().getAbsolutePath() + File.separator + params[0]);
        	if(fFile.isFile()) {
	        	RunArchiScript runner = new RunArchiScript(fFile);
	            runner.run();
        	}
        }
    }
}

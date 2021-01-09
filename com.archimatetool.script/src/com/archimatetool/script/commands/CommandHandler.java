/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.RefreshUIHandler;

/**
 * CommandHandler
 * 
 * @author Phillip Beauvoir
 */
public class CommandHandler {
    
    private static Map<CommandStack, CompoundCommand> compoundcommands;
    
    // The name of the script to display in Undo/Redo command
    private static String name;
    
    public static void init(String scriptName) {
        compoundcommands = new HashMap<CommandStack, CompoundCommand>();
        name = NLS.bind(Messages.CommandHandler_1, scriptName);
    }

    public static void executeCommand(ScriptCommand cmd) {
        if(!cmd.canExecute()) {
            return;
        }
        
        IArchimateModel model = cmd.getModel();
        CommandStack stack = (CommandStack)model.getAdapter(CommandStack.class);
        
        if(stack != null) {
            CompoundCommand compound = compoundcommands.get(stack);
            if(compound == null) {
                compound = new NonNotifyingCompoundCommand(name);
                compoundcommands.put(stack, compound);
            }
            compound.add(cmd);
        }
        
        cmd.perform();
        
        // Take this opportunity to update the UI if set
        RefreshUIHandler.refresh();
    }

    public static void finalise() {
        if(compoundcommands == null) {
            return;
        }
        
        // This simply calls empty execute() methods since perform() has already been called
        // It puts the commmands on the CommandStack for each model so that Undo/Redo is enabled
        for(Entry<CommandStack, CompoundCommand> e : compoundcommands.entrySet()) {
            e.getKey().execute(e.getValue());
        }
        
        // Set this to null so that it can be garbage collected, otherwise we will have a memory leak
        compoundcommands = null;
    }
}

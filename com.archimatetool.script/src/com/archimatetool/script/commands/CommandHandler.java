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

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.RefreshUIHandler;

/**
 * CommandHandler
 * 
 * @author Phillip Beauvoir
 */
public class CommandHandler {
    
    private static Map<CommandStack, CompoundCommand> compoundcommands;
    
    public static void init() {
        compoundcommands = new HashMap<CommandStack, CompoundCommand>();
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
                compound = new CompoundCommand(Messages.CommandHandler_0);
                compoundcommands.put(stack, compound);
            }
            compound.add(cmd);
        }
        
        cmd.perform();
        
        // Take this opportunity to update the UI if set
        RefreshUIHandler.refresh();
    }

    public static void finalise(String scriptName) {
        if(compoundcommands == null) {
            return;
        }
        
        // This simply calls empty execute() methods since perform() has already been called, but puts the commmands on the stack
        for(Entry<CommandStack, CompoundCommand> e : compoundcommands.entrySet()) {
            e.getValue().setLabel(NLS.bind(Messages.CommandHandler_1, scriptName));
            e.getKey().execute(e.getValue());
        }
    }
    
    
}

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

import com.archimatetool.model.IArchimateModel;

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
                compound = new CompoundCommand("Script"); //$NON-NLS-1$
                compoundcommands.put(stack, compound);
            }
            compound.add(cmd);
        }
        
        cmd.perform();
    }

    public static void finalise() {
        for(Entry<CommandStack, CompoundCommand> e : compoundcommands.entrySet()) {
            e.getKey().execute(e.getValue());
        }
    }
    
    
}

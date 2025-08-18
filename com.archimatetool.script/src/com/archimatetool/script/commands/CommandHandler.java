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

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.script.RefreshUIHandler;

/**
 * CommandHandler
 * 
 * @author Phillip Beauvoir
 */
public class CommandHandler {
    
    private static Map<CommandStack, CompoundCommand> compoundcommands;
    
    public static void init() {
        compoundcommands = new HashMap<>();
    }

    public static void executeCommand(ScriptCommand cmd) {
        if(!cmd.canExecute()) {
            return;
        }
        
        // Get the CommandStack
        CommandStack stack = (CommandStack)cmd.getModel().getAdapter(CommandStack.class);
        // Get the Compound Command for this stack and add the command to it
        if(stack != null) {
            CompoundCommand compound = compoundcommands.computeIfAbsent(stack, commandStack -> new ScriptNonNotifyingCompoundCommand());
            compound.add(cmd);
        }
        
        // Perform the command
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
    
    // Always return true for these so that all commands do their dummy execute() command and undo/redo always runs
    private static class ScriptNonNotifyingCompoundCommand extends NonNotifyingCompoundCommand {
        
        private ScriptNonNotifyingCompoundCommand() {
            super(Messages.CommandHandler_0);
        }
        
        @Override
        public boolean canExecute() {
            return true;
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }
    }
}

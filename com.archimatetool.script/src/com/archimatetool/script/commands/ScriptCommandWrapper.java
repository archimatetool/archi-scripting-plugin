/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

/**
 * Script Command Wrapper around another non-jArchi Command
 * 
 * @author Phillip Beauvoir
 */
public class ScriptCommandWrapper extends ScriptCommand {
    private Command cmd;

    public ScriptCommandWrapper(Command cmd, EObject eObject) {
        super(cmd.getLabel(), eObject);
        this.cmd = cmd;
    }
    
    public ScriptCommandWrapper(Command cmd) {
        super(cmd.getLabel());
        this.cmd = cmd;
    }
    
    @Override
    public void perform() {
        cmd.execute();
    }
    
    @Override
    public boolean canExecute() {
        return cmd.canExecute();
    }
    
    @Override
    public boolean canRedo() {
        return cmd.canRedo();
    }
    
    @Override
    public boolean canUndo() {
        return cmd.canUndo();
    }
    
    @Override
    public void undo() {
        cmd.undo();
    }
    
    @Override
    public void redo() {
        cmd.redo();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        cmd.dispose();
        cmd = null;
    }

}
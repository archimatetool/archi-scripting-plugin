/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * Move the position of an EObject in its containing EList
 * 
 * @author Phillip Beauvoir
 */
public class MoveListObjectCommand extends ScriptCommand {
    
    private EList<?> list;
    private int oldPosition;
    private int newPosition;
    
    /**
     * @param list The list containing eObject
     * @param eObject The object whose position should be moved
     * @param newPosition The object's new position. -1 means "end of list"
     */
    public MoveListObjectCommand(EList<?> list, EObject eObject, int newPosition) {
        super("position", eObject); //$NON-NLS-1$
        this.list = list;
        oldPosition = list.indexOf(eObject);
        // -1 is end of list
        this.newPosition = (newPosition == -1) ? (list.size() > 0 ? list.size() - 1 : 0) : newPosition;
    }

    @Override
    public void perform() {
        list.move(newPosition, oldPosition);
    }
    
    @Override
    public void undo() {
        list.move(oldPosition, newPosition);
    }
    
    @Override
    public boolean canExecute() {
        return oldPosition != newPosition;
    }
    
    @Override
    public void dispose() {
        list = null;
    }
}
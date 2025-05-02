/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;

/**
 * Move Diagram Object Command
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class MoveDiagramObjectCommand extends ScriptCommand {
   
    private IDiagramModelContainer oldParent, newParent;
    private IDiagramModelObject object;
    private int oldPosition;

    public MoveDiagramObjectCommand(IDiagramModelContainer newParent, IDiagramModelObject object) {
        super("move", newParent.getArchimateModel());
        this.newParent = newParent;
        this.object = object;
        oldParent = (IDiagramModelContainer)object.eContainer();
        oldPosition = oldParent.getChildren().indexOf(object);
    }
    
    @Override
    public void perform() {
        oldParent.getChildren().remove(object);
        newParent.getChildren().add(object);
    }
    
    @Override
    public void undo() {
        newParent.getChildren().remove(object);
        oldParent.getChildren().add(oldPosition, object);
    }

    @Override
    public void dispose() {
        super.dispose();
        oldParent = null;
        newParent = null;
        object = null;
    }
}

/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelObject;

/**
 * Change x,y position of bounds by x,y offset
 * 
 * @author Phillip Beauvoir
 */
public class ChangePositionCommand extends ScriptCommand {
   
    private IDiagramModelObject dmo;
    private IBounds oldBounds, newBounds;
    
    public ChangePositionCommand(IDiagramModelObject dmo, int xOffset, int yOffset) {
        super("Change Bounds", dmo); //$NON-NLS-1$
        this.dmo = dmo;
        oldBounds = dmo.getBounds();
        newBounds = IArchimateFactory.eINSTANCE.createBounds(oldBounds.getX() + xOffset,
                oldBounds.getY() + yOffset, oldBounds.getWidth(), oldBounds.getHeight());
    }

    @Override
    public void perform() {
        dmo.setBounds(newBounds);
    }
    
    @Override
    public void undo() {
        dmo.setBounds(oldBounds);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        dmo = null;
        oldBounds = null;
        newBounds = null;
    }
}

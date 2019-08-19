/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.editor.preferences.IPreferenceConstants;
import com.archimatetool.editor.preferences.Preferences;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelContainer;

/**
 * Set the Diagram Model Object's element to the given element
 * 
 * @author Phillip Beauvoir
 */
public class SetElementOnDiagramModelObjectCommand extends ScriptCommand {
   
    private IArchimateElement element;
    private IDiagramModelArchimateObject dmo;
    private IArchimateElement oldElement;
    private IDiagramModelContainer parent;
    private int index;
    
    // The figure type needs to be as set in user preferences for this type of concept
    int oldType;
    int newType;

    /**
     * @param element The element to set on the dmo
     * @param dmo The dmo to set the element on
     */
    public SetElementOnDiagramModelObjectCommand(IArchimateElement element, IDiagramModelArchimateObject dmo) {
        super("setConcept", element.getArchimateModel()); //$NON-NLS-1$
        
        this.element = element;
        this.dmo = dmo;
        oldElement = dmo.getArchimateElement();
        
        oldType = dmo.getType();
        newType = Preferences.STORE.getInt(IPreferenceConstants.DEFAULT_FIGURE_PREFIX + element.eClass().getName());
        
        // Store current state
        parent = (IDiagramModelContainer)dmo.eContainer();
        index = parent.getChildren().indexOf(dmo);
    }
    
    @Override
    public void perform() {
        // Remove the dmo in case it is open in the UI with listeners attached to the underlying concept
        // This will effectively remove the concept listener from the Edit Part
        parent.getChildren().remove(dmo);
        
        // Have to remove referenced dmo first
        // TODO: Remove this line because in Archi 4.5 and greater this is done in dmo.setArchimateElement(element)
        oldElement.getReferencingDiagramObjects().remove(dmo);
        
        // Set it
        dmo.setArchimateElement(element);
        
        // Set figure type
        dmo.setType(newType);
        
        // And re-attach which will also update the UI
        parent.getChildren().add(index, dmo);
    }
    
    @Override
    public void undo() {
        // Remove the dmo in case it is open in the UI with listeners attached to the underlying concept
        // This will effectively remove the concept listener from the Edit Part
        parent.getChildren().remove(dmo);
        
        // Set it back
        dmo.setArchimateElement(oldElement);
        
        // Set figure type
        dmo.setType(oldType);
        
        // And re-attach which will also update the UI
        parent.getChildren().add(index, dmo);
    }

    @Override
    public void dispose() {
        element = null;
        dmo = null;
        oldElement = null;
        parent = null;
    }
}

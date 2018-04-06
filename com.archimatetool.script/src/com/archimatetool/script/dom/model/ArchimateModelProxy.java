/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;


/**
 * ArchiMate Model object wrapper
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateModelProxy {
    
    private IArchimateModel fModel;

	public ArchimateModelProxy(IArchimateModel model) {
	    fModel = model;
    }

	public String getPurpose() {
	    return fModel.getPurpose();
	}

    public File getFile() {
        return fModel.getFile();
    }

    public List<ArchimateElementProxy> getAllElements() {
        List<ArchimateElementProxy> elements = new ArrayList<ArchimateElementProxy>();
        
        for(Iterator<EObject> iter = fModel.eAllContents(); iter.hasNext();) {
            EObject element = iter.next();
            if(element instanceof IArchimateElement) {
                elements.add(new ArchimateElementProxy((IArchimateElement)element));
            }
        }

        return elements;
    }
}

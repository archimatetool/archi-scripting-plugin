/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimateElement;


/**
 * ArchiMate Element Data object wrapper
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateElementProxy {
    
    private IArchimateElement fElement;

	public ArchimateElementProxy(IArchimateElement element) {
	    fElement = element;
    }

	public String getName() {
	    return fElement.getName();
	}
}

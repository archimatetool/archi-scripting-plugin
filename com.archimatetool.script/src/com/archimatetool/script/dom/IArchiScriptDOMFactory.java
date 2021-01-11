/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom;


/**
 * Optional DOM Factory Interface
 * 
 * If a DOM binding class implements this then its getDOMroot() method will be used to return the dom object rather than it being the DOM object.
 * This can be useful if the creation of the dom object needs to be delegated.
 */
public interface IArchiScriptDOMFactory {
    
	public Object getDOMroot();
	
}

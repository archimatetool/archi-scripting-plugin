/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.groovy;

import com.archimatetool.editor.ui.ImageFactory;




/**
 * Image Factory for this application
 * 
 * @author Phillip Beauvoir
 */
public interface IGroovyImages {
    
    ImageFactory ImageFactory = new ImageFactory(GroovyPlugin.INSTANCE);

    String IMGPATH = "img/"; //$NON-NLS-1$
    
    String ICON_GROOVY = IMGPATH + "groovy.png"; //$NON-NLS-1$
}

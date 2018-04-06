/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import com.archimatetool.editor.ui.ImageFactory;




/**
 * Image Factory for this application
 * 
 * @author Phillip Beauvoir
 */
public interface IArchiScriptImages {
    
    ImageFactory ImageFactory = new ImageFactory(ArchiScriptPlugin.INSTANCE);

    String IMGPATH = "img/"; //$NON-NLS-1$
    
    String ICON_CLEAR_CONSOLE_16 = IMGPATH + "clear.png"; //$NON-NLS-1$
    String ICON_CONSOLE_16 = IMGPATH + "console_view.png"; //$NON-NLS-1$
    String ICON_EDIT_16 = IMGPATH + "edit.png"; //$NON-NLS-1$
    String ICON_EXAMPLES_16 = IMGPATH + "examples.png"; //$NON-NLS-1$
    String ICON_REFRESH_16 = IMGPATH + "refresh.png"; //$NON-NLS-1$
    String ICON_RUN_16 = IMGPATH + "run_exc.png"; //$NON-NLS-1$
    
}

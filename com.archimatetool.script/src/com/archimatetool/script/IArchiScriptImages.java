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
    
    String ICON_CLEAR_CONSOLE = IMGPATH + "clear.png"; //$NON-NLS-1$
    String ICON_CONSOLE = IMGPATH + "console_view.png"; //$NON-NLS-1$
    String ICON_CONSOLE_WRAP = IMGPATH + "wordwrap.png"; //$NON-NLS-1$
    String ICON_CONSOLE_SCROLL_LOCK = IMGPATH + "lock_co.png"; //$NON-NLS-1$
    String ICON_EDIT = IMGPATH + "edit.png"; //$NON-NLS-1$
    String ICON_EXAMPLES = IMGPATH + "examples.png"; //$NON-NLS-1$
    String ICON_LINK_OVERLAY = IMGPATH + "link_ovr.png"; //$NON-NLS-1$
    String ICON_LINK_WARN_OVERLAY = IMGPATH + "linkwarn_ovr.png"; //$NON-NLS-1$
    String ICON_REFRESH = IMGPATH + "refresh.png"; //$NON-NLS-1$
    String ICON_RUN = IMGPATH + "run_exc.png"; //$NON-NLS-1$
    String ICON_SCRIPT = IMGPATH + "script.png"; //$NON-NLS-1$
    
    String ICON_REFRESH_UI_WHEN_RUNNING = IMGPATH + "writeout_co.png"; //$NON-NLS-1$
}

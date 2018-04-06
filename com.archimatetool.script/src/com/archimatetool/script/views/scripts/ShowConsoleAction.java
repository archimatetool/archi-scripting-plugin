/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.scripts;

import org.eclipse.jface.action.Action;

import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.views.console.ConsoleView;

/**
 * Show Console Action
 */
public class ShowConsoleAction extends Action {

    public ShowConsoleAction() {
        setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_CONSOLE_16));
        setText(Messages.ShowConsoleAction_0);
        setToolTipText(Messages.ShowConsoleAction_0);
    }
    
    @Override
    public void run() {
        ViewManager.showViewPart(ConsoleView.ID, true);
    }
}
/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.menus.UIElement;


/**
 * Refresh UI when running script command handler
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class RefreshUICommandHandler extends AbstractHandler implements IElementUpdater {
    
    public static final String ID = "com.archimatetool.script.command.refreshUI";
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        HandlerUtil.toggleCommandState(event.getCommand());
        
        // Update the CommandContributionItem menu
        ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
        commandService.refreshElements(ID, Collections.EMPTY_MAP);
        
        return null;
    }
    
    public static boolean getState() {
        ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
        Command command = commandService.getCommand(ID);
        State state = command.getState(RegistryToggleState.STATE_ID);
        return (boolean)state.getValue();
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
        // Update the CommandContributionItem menu
        element.setChecked(getState());
    }
}

/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.keys.IBindingService;

import com.archimatetool.editor.utils.StringUtils;


/**
 * Run a script from a Command and its key binding
 * 
 * @author Phillip Beauvoir
 */
public class RunScriptCommandHandler extends AbstractHandler {
    
    public static final String ID = "com.archimatetool.script.run"; //$NON-NLS-1$
    public static final String PARAMETER = "com.archimatetool.script.runParam"; //$NON-NLS-1$
    public static final String PREFS_PREFIX = "keybinding"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String parameterValue = event.getParameter(PARAMETER);
        
        File file = getScriptFileForParameterValue(parameterValue);
        if(file != null) {
            try {
                RunArchiScript runner = new RunArchiScript(file);
                runner.run();
            }
            catch(Exception ex) {
                MessageDialog.openError(null, "Archi Script", ex.getMessage()); //$NON-NLS-1$
            }
        }
        
        return null;
    }
    
    public static File getScriptFileForParameterValue(String parameterValue) {
        IPreferenceStore store = ArchiScriptPlugin.INSTANCE.getPreferenceStore();
        String scriptPath = store.getString(PREFS_PREFIX + parameterValue);
        
        if(StringUtils.isSet(scriptPath)) {
            File file = new File(scriptPath);
            return file.exists() ? file : null;
        }
        
        return null;
    }
    
    public static String getParameterValueForScriptFile(File scriptFile) {
        for(String parameterValue : getParameterValues()) {
            File file = getScriptFileForParameterValue(parameterValue);
            if(scriptFile.equals(file)) {
                return parameterValue;
            }
        }
        
        return null;
    }
    
    public static String getAcceleratorText(String parameterValue) {
        if(parameterValue == null) {
            return null;
        }
        
        try {
            Command command = PlatformUI.getWorkbench().getAdapter(ICommandService.class).getCommand(ID);
            if(command == null) {
                return null;
            }
            
            IParameter parameter = command.getParameter(PARAMETER);
            if(parameter == null) {
                return null;
            }
            
            Parameterization[] params = new Parameterization[] { new Parameterization(parameter, parameterValue) };
            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command, params);
            
            TriggerSequence ts = PlatformUI.getWorkbench().getAdapter(IBindingService.class).getBestActiveBindingFor(parameterizedCommand);
            return ts != null ? ts.format() : null;
        }
        catch(NotDefinedException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public static List<String> getParameterValues() {
        return new ArrayList<>(new ParamValues().getParameterValues().values());
    }
    
    /**
     * Used by the Run Command for different parameter values
     */
    public static class ParamValues implements IParameterValues {
        private static int NUM_PARAMS = 10;
        
        private static LinkedHashMap<String, String> paramValues;
        
        public ParamValues() {
        }

        @Override
        public Map<String, String> getParameterValues() {
            if(paramValues == null) {
                paramValues = new LinkedHashMap<>();
                for(int i = 1; i <= NUM_PARAMS; i++) {
                    paramValues.put(String.valueOf(i), String.valueOf(i));
                }
            }
            
            return paramValues;
        }
    }

}

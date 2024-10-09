/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterizedCommand;
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
@SuppressWarnings("nls")
public class RunScriptCommandHandler extends AbstractHandler implements IParameterValues {
    
    public static final String ID = "com.archimatetool.script.run";
    public static final String PARAMETER = "com.archimatetool.script.runParam";
    public static final String PREFS_PREFIX = "keybinding";

    private static LinkedHashMap<String, String> paramValues;
    private static int NUM_PARAMS = 20;
    
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
                MessageDialog.openError(null, "Archi Script", ex.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public Map<String, String> getParameterValues() {
        return getParameters();
    }
    
    public static Map<String, String> getParameters() {
        if(paramValues == null) {
            paramValues = new LinkedHashMap<>();
            
            for(int i = 1; i <= NUM_PARAMS; i++) {
                // The Keys Preference page orders alphanumerically so add a leading zero so it displays as "Run Script (01)"
                paramValues.put(String.format("%02d", i), String.valueOf(i));
            }
        }
        
        return paramValues;
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
        for(String parameterValue : getParameters().values()) {
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
        
        Command command = PlatformUI.getWorkbench().getAdapter(ICommandService.class).getCommand(ID);
        if(command == null) {
            return null;
        }
        
        ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, Map.of(PARAMETER, parameterValue));
        if(parameterizedCommand == null) {
            return null;
        }
        
        TriggerSequence ts = PlatformUI.getWorkbench().getAdapter(IBindingService.class).getBestActiveBindingFor(parameterizedCommand);
        return ts != null ? ts.format() : null;
    }
}

/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commandline;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.archimatetool.commandline.AbstractCommandLineProvider;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.RunArchiScript;

/**
 * Command Line interface for running a script
 * 
 * Usage - (should be all on one line):
 * 
 * Archi -consoleLog -nosplash -application com.archimatetool.commandline.app
   --script.runScript "file"
 * 
 * 
 * @author Phillip Beauvoir
 */
public class RunScriptProvider extends AbstractCommandLineProvider {

    static final String PREFIX = Messages.RunScriptProvider_0;
    
    static final String OPTION_RUN_SCRIPT = "script.runScript"; //$NON-NLS-1$
    
    public RunScriptProvider() {
    }
    
    @Override
    public void run(CommandLine commandLine) throws Exception {
        if(!hasCorrectOptions(commandLine)) {
            return;
        }
        
        String sFile = commandLine.getOptionValue(OPTION_RUN_SCRIPT);
        if(!StringUtils.isSet(sFile)) {
            logError(Messages.RunScriptProvider_1);
            return;
        }
        
        File scriptFile = new File(sFile);

        RunArchiScript runner = new RunArchiScript(scriptFile);
        runner.run();
    }
    
    @Override
    public Options getOptions() {
        Options options = new Options();
        
        Option option = Option.builder()
                .longOpt(OPTION_RUN_SCRIPT)
                .hasArg()
                .argName(Messages.RunScriptProvider_2)
                .desc(Messages.RunScriptProvider_3)
                .build();
        options.addOption(option);
        
        return options;
    }
    
    private boolean hasCorrectOptions(CommandLine commandLine) {
        return commandLine.hasOption(OPTION_RUN_SCRIPT);
    }
    
    @Override
    public int getPriority() {
        return PRIORITY_RUN_SCRIPT;
    }
    
    @Override
    protected String getLogPrefix() {
        return PREFIX;
    }
}

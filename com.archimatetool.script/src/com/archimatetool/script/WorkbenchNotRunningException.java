/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

/**
 * Archi Script Exception
 * 
 * @author Phillip Beauvoir
 */
public class WorkbenchNotRunningException extends ArchiScriptException {

    public WorkbenchNotRunningException() {
        super(Messages.WorkbenchNotRunningException_0);
    }

    public WorkbenchNotRunningException(String message) {
        super(message);
    }

    public WorkbenchNotRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkbenchNotRunningException(Throwable cause) {
        super(cause);
    }
    
}
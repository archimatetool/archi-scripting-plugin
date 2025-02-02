/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import org.junit.jupiter.api.Test;

/**
 * Run Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class RunArchiScriptTests {
    
    @Test
    public void testModelScript() {
        runScript("test-model.ajs");
    }

    @Test
    public void testDuplicateScript() {
        runScript("test-duplicate.ajs");
    }

    @Test
    public void testDeleteKeepChildrenScript() {
        runScript("test-deletekeepchildren.ajs");
    }

    @Test
    public void testDiagramObjectsScript() {
        runScript("test-diagramobjects.ajs");
    }

    private void runScript(String scriptFile) {
        RunArchiScript rs = new RunArchiScript(TestFiles.getTestFile(scriptFile));
        rs.throwExceptions = true;
        rs.run();
    }
}

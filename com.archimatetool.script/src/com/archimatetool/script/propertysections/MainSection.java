/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.propertysections;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.editor.propertysections.AbstractArchiPropertySection;
import com.archimatetool.script.ScriptFiles;


/**
 * Property Section for Script
 * 
 * @author Phillip Beauvoir
 */
public class MainSection extends AbstractArchiPropertySection {
    
    public static class Filter implements IFilter {
        @Override
        public boolean select(Object object) {
            return object instanceof File;
        }
    }
    
    private Text textFile;
    private Label labelPreview;
    private StyledText textPreview;
    
    public MainSection() {
    }

    @Override
    protected void createControls(Composite parent) {
        createLabel(parent, Messages.MainSection_0, STANDARD_LABEL_WIDTH, SWT.CENTER);
        textFile = createSingleTextControl(parent, SWT.READ_ONLY);
        
        labelPreview = createLabel(parent, Messages.MainSection_1, STANDARD_LABEL_WIDTH, SWT.NONE);
        textPreview = new StyledText(parent, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL); // Don't use SWT.WRAP as it can be too slow on large files
        textPreview.setTabs(4);
        textPreview.setFont(JFaceResources.getTextFont());
        GridDataFactory.create(GridData.FILL_BOTH).hint(100, 100).applyTo(textPreview); // hint(100, 100) stops excess size if the control contains a lot of text
    }

    @Override
    protected void handleSelection(IStructuredSelection selection) {
        if(selection == getSelection()) {
            return;
        }
        
        if(selection.getFirstElement() instanceof File file) {
            if(ScriptFiles.isLinkedFile(file)) {
                file = ScriptFiles.resolveLinkFile(file);
            }
            
            textFile.setText(file.getAbsolutePath());
            
            if(file.isFile()) {
                labelPreview.setVisible(true);
                textPreview.setVisible(true);
                try {
                    String content = Files.readString(file.toPath());
                    textPreview.setText(content);
                }
                catch(IOException ex) {
                    textPreview.setText(Messages.MainSection_2);
                }
            }
            else {
                labelPreview.setVisible(false);
                textPreview.setVisible(false);
                textPreview.setText(""); //$NON-NLS-1$
            }
        }
    }
    
    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }
}

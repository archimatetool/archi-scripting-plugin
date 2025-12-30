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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.editor.propertysections.AbstractArchiPropertySection;
import com.archimatetool.editor.ui.FontFactory;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.ScriptFiles;
import com.archimatetool.script.preferences.IPreferenceConstants;


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
    private Text textPreview;
    
    private boolean showPreview;
    
    private IPreferenceStore prefsStore = ArchiScriptPlugin.getInstance().getPreferenceStore();
    
    private IPropertyChangeListener prefsListener = event -> {
        switch(event.getProperty()) {
            case IPreferenceConstants.PREFS_SHOW_PREVIEW,
                 IPreferenceConstants.PREFS_PREVIEW_FONT -> {
                     setPreviewControls();
            }
        }
    };
    
    public MainSection() {
    }

    @Override
    protected void createControls(Composite parent) {
        createLabel(parent, Messages.MainSection_0, STANDARD_LABEL_WIDTH, SWT.CENTER);
        textFile = createSingleTextControl(parent, SWT.READ_ONLY);
        
        labelPreview = createLabel(parent, Messages.MainSection_1, STANDARD_LABEL_WIDTH, SWT.NONE);
        textPreview = getWidgetFactory().createText(parent, null, SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
        GridDataFactory.create(GridData.FILL_BOTH).hint(100, 100).applyTo(textPreview); // hint(100, 100) stops excess size if the control contains a lot of text
        
        setPreviewControls();
        
        prefsStore.addPropertyChangeListener(prefsListener);
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
            
            if(showPreview) {
                updatePreview(file);
            }
        }
    }
    
    private void updatePreview(File file) {
        // File
        if(file.isFile()) {
            labelPreview.setVisible(true);
            textPreview.setVisible(true);
            try {
                String content = Files.readString(file.toPath());
                textPreview.setText(content);
                textPreview.setTopIndex(0);
            }
            catch(IOException ex) {
                textPreview.setText(Messages.MainSection_2);
            }
        }
        // Directory
        else {
            labelPreview.setVisible(false);
            textPreview.setVisible(false);
            textPreview.setText(""); //$NON-NLS-1$
        }
    }
    
    private void setPreviewControls() {
        showPreview = prefsStore.getBoolean(IPreferenceConstants.PREFS_SHOW_PREVIEW);
        
        labelPreview.setVisible(showPreview);
        textPreview.setVisible(showPreview);
        
        String fontName = prefsStore.getString(IPreferenceConstants.PREFS_PREVIEW_FONT);
        textPreview.setFont(StringUtils.isSet(fontName) ? FontFactory.get(fontName) : JFaceResources.getTextFont());
    }
    
    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }
    
    @Override
    public void dispose() {
        prefsStore.removePropertyChangeListener(prefsListener);
    }
}

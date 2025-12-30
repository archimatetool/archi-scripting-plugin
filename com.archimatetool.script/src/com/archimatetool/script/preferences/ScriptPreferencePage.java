/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.preferences;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.UIUtils;
import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.JSProvider;


/**
 * Script Preferences Page
 * 
 * @author Phillip Beauvoir
 */
public class ScriptPreferencePage
extends PreferencePage
implements IWorkbenchPreferencePage, IPreferenceConstants {
    
    public static final String ID = "com.archimatetool.script.preferences.ScriptPreferencePage";  //$NON-NLS-1$
    
    public static String HELP_ID = ArchiScriptPlugin.PLUGIN_ID + ".scriptPrefsHelp"; //$NON-NLS-1$
    
    private Text fScriptsFolderTextField;
    private Text fEditorPathTextField;
    
    private Combo fDoubleClickBehaviourCombo;
    
    private FontData fDefaultFontData = JFaceResources.getTextFont().getFontData()[0];

    private Label fConsoleFontLabel;
    private FontData fConsoleFontData = fDefaultFontData;
    
    private String[] DOUBLE_CLICK_BEHAVIOURS = {
            Messages.ScriptPreferencePage_4,
            Messages.ScriptPreferencePage_5,
            Messages.ScriptPreferencePage_6
    };
    
    private Button fShowPreviewButton;
    private Label fPreviewFontLabel;
    private FontData fPreviewFontData = fDefaultFontData;
    
    private Combo fJSCombo;
    private Button fCommonJSButton;
    
    private boolean debuggerOptionsEnabled = !PlatformUtils.isLinux(); // Not working on Linux
    private Button fDebugButton;
    private Text fDebugPortTextField;
    private Text fDebugBrowserTextField;
    
    private String[] JS_VERSIONS = {
            Messages.ScriptPreferencePage_10,
            Messages.ScriptPreferencePage_11,
            Messages.ScriptPreferencePage_14
    };
    
	public ScriptPreferencePage() {
		setPreferenceStore(ArchiScriptPlugin.getInstance().getPreferenceStore());
	}
	
    @Override
    protected Control createContents(Composite parent) {
        // Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_ID);
        
        Composite client = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(client);
        
        // Settings Group
        Group settingsGroup = new Group(client, SWT.NULL);
        settingsGroup.setText(Messages.ScriptPreferencePage_0);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(settingsGroup);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).hint(500, SWT.DEFAULT).applyTo(settingsGroup);
        
        // Scripts folder location
        Label label = new Label(settingsGroup, SWT.NULL);
        label.setText(Messages.ScriptPreferencePage_8 + ":"); //$NON-NLS-1$
        
        fScriptsFolderTextField = UIUtils.createSingleTextControl(settingsGroup, SWT.BORDER, false);
        fScriptsFolderTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Button folderButton = new Button(settingsGroup, SWT.PUSH);
        folderButton.setText(Messages.ScriptPreferencePage_2);
        folderButton.addSelectionListener(widgetSelectedAdapter(event -> {
            String folderPath = chooseFolderPath();
            if(folderPath != null) {
                fScriptsFolderTextField.setText(folderPath);
            }
        }));

        // Editor path
        label = new Label(settingsGroup, SWT.NULL);
        label.setText(Messages.ScriptPreferencePage_1);
        
        fEditorPathTextField = UIUtils.createSingleTextControl(settingsGroup, SWT.BORDER, false);
        fEditorPathTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Button fileButton = new Button(settingsGroup, SWT.PUSH);
        fileButton.setText(Messages.ScriptPreferencePage_2);
        fileButton.addSelectionListener(widgetSelectedAdapter(event -> {
            String path = chooseEditor();
            if(path != null) {
                fEditorPathTextField.setText(path);
            }
        }));
        
        // Double-click behaviour
        label = new Label(settingsGroup, SWT.NULL);
        label.setText(Messages.ScriptPreferencePage_7);
        fDoubleClickBehaviourCombo = new Combo(settingsGroup, SWT.READ_ONLY);
        fDoubleClickBehaviourCombo.setItems(DOUBLE_CLICK_BEHAVIOURS);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).span(2, 1).applyTo(fDoubleClickBehaviourCombo);
        
        // Display Group
        Group displayGroup = new Group(client, SWT.NULL);
        displayGroup.setText(Messages.ScriptPreferencePage_23);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(displayGroup);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).applyTo(displayGroup);        
        
        // Console font
        label = new Label(displayGroup, SWT.NULL);
        label.setText(Messages.ScriptPreferencePage_13);
        
        Button consoleFontButton = new Button(displayGroup, SWT.PUSH);
        consoleFontButton.setText(Messages.ScriptPreferencePage_2);
        consoleFontButton.addSelectionListener(widgetSelectedAdapter(event -> {
            FontData fd = openFontDialog(fConsoleFontData);
            if(fd != null) {
                fConsoleFontData = fd;
                updateFontLabel(fConsoleFontLabel, fConsoleFontData);
            }
        }));
        
        fConsoleFontLabel = new Label(displayGroup, SWT.NULL);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).applyTo(fConsoleFontLabel);        
        
        // Preview Font
        label = new Label(displayGroup, SWT.NULL);
        label.setText(Messages.ScriptPreferencePage_24);
        
        Button previewFontButton = new Button(displayGroup, SWT.PUSH);
        previewFontButton.setText(Messages.ScriptPreferencePage_2);
        previewFontButton.addSelectionListener(widgetSelectedAdapter(event -> {
            FontData fd = openFontDialog(fPreviewFontData);
            if(fd != null) {
                fPreviewFontData = fd;
                updateFontLabel(fPreviewFontLabel, fPreviewFontData);
            }
        }));
        
        fPreviewFontLabel = new Label(displayGroup, SWT.NULL);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).applyTo(fPreviewFontLabel);

        // Show Preview
        fShowPreviewButton = new Button(displayGroup, SWT.CHECK);
        fShowPreviewButton.setText(Messages.ScriptPreferencePage_25);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).span(3, 1).applyTo(fShowPreviewButton);
        
        // JavaScript Group
        Group jsGroup = new Group(client, SWT.NULL);
        jsGroup.setText(Messages.ScriptPreferencePage_27);
        jsGroup.setLayout(new GridLayout(2, false));
        GridDataFactory.create(GridData.FILL_HORIZONTAL).span(2, 1).applyTo(jsGroup);
        
        // JS Versions - only if Nashorn is installed
        if(JSProvider.isNashornInstalled()) {
            label = new Label(jsGroup, SWT.NULL);
            label.setText(Messages.ScriptPreferencePage_12);
            fJSCombo = new Combo(jsGroup, SWT.READ_ONLY);
            fJSCombo.setItems(JS_VERSIONS);
            GridDataFactory.create(GridData.FILL_HORIZONTAL).applyTo(fJSCombo);
        }
        
        // Enable CommonJS Support
        fCommonJSButton = new Button(jsGroup, SWT.CHECK);
        fCommonJSButton.setText(Messages.ScriptPreferencePage_15);
        fCommonJSButton.setToolTipText(Messages.ScriptPreferencePage_16);
        GridDataFactory.create(GridData.FILL_HORIZONTAL).span(2, 1).applyTo(fCommonJSButton);
        
        // Chrome Debugger
        if(debuggerOptionsEnabled) {
            Group debugGroup = new Group(client, SWT.NULL);
            debugGroup.setText(Messages.ScriptPreferencePage_17);
            debugGroup.setLayout(new GridLayout(3, false));
            GridDataFactory.create(GridData.FILL_HORIZONTAL).span(3, 1).applyTo(debugGroup);
            
            // Enable Debug
            fDebugButton = new Button(debugGroup, SWT.CHECK);
            fDebugButton.setText(Messages.ScriptPreferencePage_18);
            fDebugButton.setToolTipText(Messages.ScriptPreferencePage_19);
            GridDataFactory.create(GridData.FILL_HORIZONTAL).span(3, 1).applyTo(fDebugButton);
            
            // Debug port
            new Label(debugGroup, SWT.NULL).setText(Messages.ScriptPreferencePage_20);
            fDebugPortTextField = UIUtils.createSingleTextControl(debugGroup, SWT.BORDER, false);
            GridDataFactory.create(GridData.FILL_HORIZONTAL).span(2, 1).applyTo(fDebugPortTextField);
            
            fDebugPortTextField.addVerifyListener(event -> {
                String currentText = fDebugPortTextField.getText();
                String port = currentText.substring(0, event.start) + event.text + currentText.substring(event.end);
                try {
                    int portNum = Integer.valueOf(port);
                    if(portNum < 0 || portNum > 65535) {
                        event.doit = false;
                    }
                }
                catch(NumberFormatException ex) {
                    if(!port.equals("")) { //$NON-NLS-1$
                        event.doit = false;
                    }
                }
            });
            
            // Browser path
            label = new Label(debugGroup, SWT.NULL);
            label.setText(Messages.ScriptPreferencePage_21);
            
            fDebugBrowserTextField = UIUtils.createSingleTextControl(debugGroup, SWT.BORDER, false);
            fDebugBrowserTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
            Button browserButton = new Button(debugGroup, SWT.PUSH);
            browserButton.setText(Messages.ScriptPreferencePage_2);
            browserButton.addSelectionListener(widgetSelectedAdapter(event -> {
                FileDialog dialog = new FileDialog(getShell());
                dialog.setText(Messages.ScriptPreferencePage_22);
                File file = new File(fDebugBrowserTextField.getText());
                dialog.setFilterPath(file.getParent());
                String path = dialog.open();
                if(path != null) {
                    fDebugBrowserTextField.setText(path);
                }
            }));
        }
        
        setValues();
        
        return client;
    }

    private String chooseFolderPath() {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setText(Messages.ScriptPreferencePage_8);
        dialog.setMessage(Messages.ScriptPreferencePage_9);
        File file = new File(fScriptsFolderTextField.getText());
        if(file.exists()) {
            dialog.setFilterPath(fScriptsFolderTextField.getText());
        }
        return dialog.open();
    }

    private String chooseEditor() {
        FileDialog dialog = new FileDialog(getShell());
        dialog.setText(Messages.ScriptPreferencePage_3);
        File file = new File(fEditorPathTextField.getText());
        dialog.setFilterPath(file.getParent());
        return dialog.open();
    }

    private void setValues() {
        fScriptsFolderTextField.setText(getPreferenceStore().getString(PREFS_SCRIPTS_FOLDER));
        fEditorPathTextField.setText(getPreferenceStore().getString(PREFS_EDITOR));
        fDoubleClickBehaviourCombo.select(getPreferenceStore().getInt(PREFS_DOUBLE_CLICK_BEHAVIOUR));
        fShowPreviewButton.setSelection(getPreferenceStore().getBoolean(PREFS_SHOW_PREVIEW));
        
        String consoleFontName = getPreferenceStore().getString(PREFS_CONSOLE_FONT);
        if(StringUtils.isSet(consoleFontName)) {
            fConsoleFontData = new FontData(consoleFontName);
        }
        updateFontLabel(fConsoleFontLabel, fConsoleFontData);
        
        String previewFontName = getPreferenceStore().getString(PREFS_PREVIEW_FONT);
        if(StringUtils.isSet(previewFontName)) {
            fPreviewFontData = new FontData(previewFontName);
        }
        updateFontLabel(fPreviewFontLabel, fPreviewFontData);
        
        if(fJSCombo != null) {
            fJSCombo.select(getPreferenceStore().getInt(PREFS_JS_ENGINE));
        }
        
        fCommonJSButton.setSelection(getPreferenceStore().getBoolean(PREFS_COMMONJS_ENABLED));
        
        if(debuggerOptionsEnabled) {
            fDebugButton.setSelection(getPreferenceStore().getBoolean(PREFS_DEBUGGER_ENABLED));
            fDebugPortTextField.setText(getPreferenceStore().getString(PREFS_DEBUGGER_PORT));
            fDebugBrowserTextField.setText(getPreferenceStore().getString(PREFS_DEBUGGER_BROWSER));
        }
    }
    
    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(PREFS_SCRIPTS_FOLDER, fScriptsFolderTextField.getText());
        getPreferenceStore().setValue(PREFS_EDITOR, fEditorPathTextField.getText());
        getPreferenceStore().setValue(PREFS_DOUBLE_CLICK_BEHAVIOUR, fDoubleClickBehaviourCombo.getSelectionIndex());
        getPreferenceStore().setValue(PREFS_SHOW_PREVIEW, fShowPreviewButton.getSelection());
        
        getPreferenceStore().setValue(PREFS_CONSOLE_FONT, fDefaultFontData.equals(fConsoleFontData) ? "" : fConsoleFontData.toString()); //$NON-NLS-1$
        getPreferenceStore().setValue(PREFS_PREVIEW_FONT, fDefaultFontData.equals(fPreviewFontData) ? "" : fPreviewFontData.toString()); //$NON-NLS-1$
        
        if(fJSCombo != null) {
            getPreferenceStore().setValue(PREFS_JS_ENGINE, fJSCombo.getSelectionIndex());
        }
        
        getPreferenceStore().setValue(PREFS_COMMONJS_ENABLED, fCommonJSButton.getSelection());
        
        if(debuggerOptionsEnabled) {
            getPreferenceStore().setValue(PREFS_DEBUGGER_ENABLED, fDebugButton.getSelection());
            getPreferenceStore().setValue(PREFS_DEBUGGER_PORT, fDebugPortTextField.getText());
            getPreferenceStore().setValue(PREFS_DEBUGGER_BROWSER, fDebugBrowserTextField.getText());
        }
        
        return true;
    }
    
    @Override
    protected void performDefaults() {
        fScriptsFolderTextField.setText(getPreferenceStore().getDefaultString(PREFS_SCRIPTS_FOLDER));
        fEditorPathTextField.setText(getPreferenceStore().getDefaultString(PREFS_EDITOR));
        fDoubleClickBehaviourCombo.select(getPreferenceStore().getDefaultInt(PREFS_DOUBLE_CLICK_BEHAVIOUR));
        fShowPreviewButton.setSelection(getPreferenceStore().getDefaultBoolean(PREFS_SHOW_PREVIEW));
        
        fConsoleFontData = fDefaultFontData;
        updateFontLabel(fConsoleFontLabel, fConsoleFontData);
        
        fPreviewFontData = fDefaultFontData;
        updateFontLabel(fPreviewFontLabel, fPreviewFontData);
        
        if(fJSCombo != null) {
            fJSCombo.select(getPreferenceStore().getDefaultInt(PREFS_JS_ENGINE));
        }
        
        fCommonJSButton.setSelection(getPreferenceStore().getDefaultBoolean(PREFS_COMMONJS_ENABLED));
        
        if(debuggerOptionsEnabled) {
            fDebugButton.setSelection(getPreferenceStore().getDefaultBoolean(PREFS_DEBUGGER_ENABLED));
            fDebugPortTextField.setText(getPreferenceStore().getDefaultString(PREFS_DEBUGGER_PORT));
            fDebugBrowserTextField.setText(getPreferenceStore().getDefaultString(PREFS_DEBUGGER_BROWSER));
        }
        
        super.performDefaults();
    }
    
    private void updateFontLabel(Label label, FontData fd) {
        label.setText(fd.getName() + " " + fd.getHeight()); //$NON-NLS-1$
    }
    
    private FontData openFontDialog(FontData initialFontData) {
        FontDialog dialog = new FontDialog(getShell());
        dialog.setEffectsVisible(false);
        dialog.setFontList(new FontData[] { initialFontData });
        return dialog.open();
    }
    
    @Override
    public void init(IWorkbench workbench) {
    }
}
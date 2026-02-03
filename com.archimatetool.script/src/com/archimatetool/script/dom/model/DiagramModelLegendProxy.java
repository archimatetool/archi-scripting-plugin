/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.ILegendOptions;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.SetCommand;

/**
 * Diagram Model Legend wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelLegendProxy extends DiagramModelNoteProxy {
    
    DiagramModelLegendProxy(IDiagramModelNote note) {
        super(note);
    }
    
    @Override
    public DiagramModelLegendProxy setText(String text) {
        // Can't set this
        return this;
    }
    
    @Override
    public DiagramModelLegendProxy setBorderType(int type) {
        // Can't set this
        return this;
    }
    
    @Override
    public DiagramModelObjectProxy setTextAlignment(int alignment) {
        // Can't set this
        return this;
    }
    
    @Override
    public DiagramModelObjectProxy setTextPosition(int position) {
        // Can't set this
        return this;
    }
    
    @Override
    public String getType() {
        // Over-ride this
        return DIAGRAM_MODEL_LEGEND;
    }
    
    public DiagramModelLegendProxy setOptions(Map<?, ?> map) {
        String optionsString = ILegendOptions.create()
                                 .displayElements(ModelUtil.getBooleanValueFromMap(map, LEGEND_DISPLAY_ELEMENTS, true))
                                 .displayRelations(ModelUtil.getBooleanValueFromMap(map, LEGEND_DISPLAY_RELATIONS, true))
                                 .displaySpecializationElements(ModelUtil.getBooleanValueFromMap(map, LEGEND_DISPLAY_SPECIALIZATION_ELEMENTS, true))
                                 .displaySpecializationRelations(ModelUtil.getBooleanValueFromMap(map, LEGEND_DISPLAY_SPECIALIZATION_RELATIONS, true))
                                 .rowsPerColumn(ModelUtil.getIntValueFromMap(map, LEGEND_ROWS_PER_COLUMN, ILegendOptions.ROWS_PER_COLUMN_DEFAULT))
                                 .widthOffset(ModelUtil.getIntValueFromMap(map, LEGEND_WIDTH_OFFSET, 0))
                                 .colorScheme(ModelUtil.getIntValueFromMap(map, LEGEND_COLOR_SCHEME, ILegendOptions.COLORS_DEFAULT))
                                 .sortMethod(ModelUtil.getIntValueFromMap(map, LEGEND_SORT_METHOD, ILegendOptions.SORT_DEFAULT))
                                 .toFeatureString();

        CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelNote.FEATURE_LEGEND, optionsString, null));
        
        return this;
    }
    
    public Map<?, ?> getOptions() {
        ILegendOptions options = getEObject().getLegendOptions();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(LEGEND_DISPLAY_ELEMENTS, options.displayElements());
        map.put(LEGEND_DISPLAY_RELATIONS, options.displayRelations());
        map.put(LEGEND_DISPLAY_SPECIALIZATION_ELEMENTS, options.displaySpecializationElements());
        map.put(LEGEND_DISPLAY_SPECIALIZATION_RELATIONS, options.displaySpecializationRelations());
        map.put(LEGEND_ROWS_PER_COLUMN, options.getRowsPerColumn());
        map.put(LEGEND_WIDTH_OFFSET, options.getWidthOffset());
        map.put(LEGEND_COLOR_SCHEME, options.getColorScheme());
        map.put(LEGEND_SORT_METHOD, options.getSortMethod());
        
        return map;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case LEGEND_OPTIONS:
                return getOptions();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case LEGEND_OPTIONS:
                if(value instanceof Map val) {
                    return setOptions(val);
                }
                break;
        }
        
        return super.attr(attribute, value);
    }
}

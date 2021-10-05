/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for model constants
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
interface IModelConstants {

    String ID = "id";
    String NAME = "name";
    String DOCUMENTATION = "documentation";
    String PURPOSE = "purpose";
    String TYPE = "type";
    
    String CONCEPT = "concept";
    String ELEMENT = "element";
    String RELATION = "relation";
    String RELATIONSHIP = "relationship";
    String VIEW = "view";
    String VIEWPOINT = "viewpoint";
    
    String SOURCE = "source";
    String TARGET = "target";
    
    String RELATIVE_BENDPOINTS = "relativeBendpoints";
    String START_X = "startX";
    String START_Y = "startY";
    String END_X = "endX";
    String END_Y = "endY";
    
    String BOUNDS = "bounds";
    
    String FILL_COLOR = "fillColor"; 
    
    String FONT_COLOR = "fontColor"; 
    String FONT_SIZE = "fontSize"; 
    String FONT_STYLE = "fontStyle"; 
    String FONT_NAME = "fontName"; 
    
    String LINE_COLOR = "lineColor"; 
    String LINE_WIDTH = "lineWidth"; 
    
    String OPACITY = "opacity";
    String OUTLINE_OPACITY = "outlineOpacity";
    
    String LABEL_VISIBLE = "labelVisible";
    
    String FIGURE_TYPE = "figureType";
    
    String TEXT = "text";
    
    String TEXT_ALIGNMENT = "textAlignment";
    String TEXT_POSITION = "textPosition";
    
    String BORDER_TYPE = "borderType";
    
    String GRADIENT = "gradient";
    
    String STYLE = "style";
    
    String ACCESS_TYPE = "access-type"; 
    List<String> ACCESS_TYPES_LIST = Arrays.asList(new String[] {
            "write",
            "read",
            "access",
            "readwrite"
    });
    
    String INFLUENCE_STRENGTH = "influence-strength";
    
    String ASSOCIATION_DIRECTED = "association-directed";
    
    String JUNCTION_TYPE = "junction-type";
    List<String> JUNCTION_TYPES_LIST = Arrays.asList(new String[] {
            "and",
            "or"
    });

    // View types
    String VIEW_ARCHIMATE = "archimate";
    String VIEW_SKETCH = "sketch";
    String VIEW_CANVAS = "canvas";
    
    // Object types
    String DIAGRAM_MODEL_GROUP = "diagram-model-group";
    String DIAGRAM_MODEL_NOTE = "diagram-model-note";
    
    // Label Expressions
    String LABEL_EXPRESSION = "label-expression";
    String LABEL_VALUE = "label-value";
}

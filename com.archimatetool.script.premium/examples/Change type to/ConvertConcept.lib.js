function convert(filename) {
    var relaxed = window.confirm('By default, selected concepts are converted, and relationships that would no longer be valid are converted to Association. Click OK for this or Cancel if you want a "strict" mode where relationships are not changed.');

    convertToType = getTypeFromFilename(filename);

    $(selection).each(function (o) {
        var concept = getConcept(o);

        if(concept) {
            $(concept).outRels().each(function (r) {
                if (!$.model.isAllowedRelationship(r.type, convertToType, r.target.type)) {
                    checkAndConvertRelationship(r, relaxed);
                }
            });
    
            $(concept).inRels().each(function (r) {
                if (!$.model.isAllowedRelationship(r.type, r.source.type, convertToType)) {
                    checkAndConvertRelationship(r, relaxed);
                }
            });
    
            concept.type = convertToType;
        }

    });
}

function checkAndConvertRelationship(r, relaxed) {
    if(relaxed) {
        r.documentation = 'This relationship has been converted from "' + r.type.replace(/-relationship$/, '') + '" to "association"\n' + r.documentation;
        r.type = "association-relationship";
    }
    else {
        window.alert('Relationship "' + r.name + '" from "' + r.source.name + '" to "' + r.target.name + '" will not be valid after conversion and "strict" mode is on. Conversion aborted.');
        exit();
    }
}

function getConcept(o) {
    return o.concept ? o.concept : null;
}

function getTypeFromFilename(fileName) {
    return fileName.replace(/^.*[\/\\]/, '').replace(/\.ajs$/, '').replace(/(%20|\s)/g, '-').toLowerCase();
}
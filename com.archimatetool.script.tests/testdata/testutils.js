// =========================================================================
// Assertions
// =========================================================================

function assertNull(obj) {
    if(obj != null) {
        throw new Error("expected <null> but was " + obj);
    }
}

function assertNotNull(obj) {
    if(obj == null) {
        throw new Error("expected not <null>");
    }
}

function assertEquals(expected, actual) {
    if(expected !== actual) {
        throw new Error("expected: <" + expected + "> but was <" + actual + ">");
    }
}

function assertNotEquals(expected, actual) {
    if(expected == actual) {
        throw new Error("expected: not equal but was <" + actual + ">");
    }
}

function assertTrue(condition) {
    if(!condition) {
        throw new Error("expected <true> but was <false>");
    }
}

function assertFalse(condition) {
    if(condition) {
        throw new Error("expected <false> but was <true>");
    }
}

// =========================================================================
// Utils
// =========================================================================

function loadTestModel(fileName, setAsCurrent) {
    var model = $.model.load(__DIR__ + "/" + fileName);
    if(setAsCurrent) {
        model.setAsCurrent();
    }
    return model;
}
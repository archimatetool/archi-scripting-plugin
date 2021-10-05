function jArchi (obj) {
	if (obj == selection) // obj is an object
		return obj;
	else // obj is an object, a string or will be casted to a (potentially empty) string
		return model.find(obj || "");
}

jArchi.model = jArchiModel;

jArchi.fs = jArchiFS;

jArchi.process = {
	engine: Java.type("java.lang.System").getProperty("script.engine"),
	argv: Java.type("org.eclipse.core.runtime.Platform").getApplicationArgs(),
	platform: Java.type("org.eclipse.core.runtime.Platform").getOS(),
	release: {
		archiName: Java.type("org.eclipse.core.runtime.Platform").getBundle("com.archimatetool.editor").getHeaders().get("Bundle-Name"),
		archiVersion: Java.type("org.eclipse.core.runtime.Platform").getBundle("com.archimatetool.editor").getHeaders().get("Bundle-Version"),
		jArchiName: Java.type("org.eclipse.core.runtime.Platform").getBundle("com.archimatetool.script").getHeaders().get("Bundle-Name"),
		jArchiVersion: Java.type("org.eclipse.core.runtime.Platform").getBundle("com.archimatetool.script").getHeaders().get("Bundle-Version")
	}
};
Object.freeze(jArchi.process.release);
Object.freeze(jArchi.process);

jArchi.child_process = {
	exec: function() {
		// Split arguments into an array of args
		var args = Array.prototype.slice.call(arguments);
		
		var platform = Java.type("org.eclipse.core.runtime.Platform").getOS();
		if(platform == "macosx") {
			args = ["open", "-a"].concat(args[0]); // for some reason, Mac will only accept one argument
		}
	
		var runtime = Java.type("java.lang.Runtime").getRuntime();
		runtime.exec(args);
	}
}

$ = jArchi;

// window dialog functions
var window = {
	alert: function(message) {
		var MessageDialog = Java.type("org.eclipse.jface.dialogs.MessageDialog");
		MessageDialog.openInformation(shell, "Archi", message);
	},

	confirm: function(message) {
		var MessageDialog = Java.type("org.eclipse.jface.dialogs.MessageDialog");
		return MessageDialog.openConfirm(shell, "Archi", message);
	},

	prompt: function(message, defaultText) {
		var InputDialog = Java.type("org.eclipse.jface.dialogs.InputDialog");
		var dialog = new InputDialog(shell, "Archi", message, defaultText, null);
		dialog.open();
		return dialog.getValue();
	},

	promptOpenFile: function(options) {
		var FileDialog = Java.type("org.eclipse.swt.widgets.FileDialog");
		var dialog = new FileDialog(shell, 1 << 12);
		
		var options = options || {};
		dialog.text = options.title || "Archi";
		dialog.filterExtensions = options.filterExtensions || [];
		dialog.fileName = options.fileName || null;

		return dialog.open();
	},

	promptOpenDirectory: function(options) {
		var DirectoryDialog = Java.type("org.eclipse.swt.widgets.DirectoryDialog");
		var dialog = new DirectoryDialog(shell);

		var options = options || {};
		dialog.text = options.title || "Archi";
		dialog.filterPath = options.filterPath || null;

		return dialog.open();
	},

	promptSaveFile: function(options) {
		var FileDialog = Java.type("org.eclipse.swt.widgets.FileDialog");
		var dialog = new FileDialog(shell, 1 << 13);

		var options = options || {};
		dialog.text = options.title || "Archi";
		dialog.filterExtensions = options.filterExtensions || [];
		dialog.fileName = options.fileName || null;

		return dialog.open();
	},

};

// Define exit to throw an Exception
function exit() {
	throw "__EXIT__";
}

// Legacy functions marked as deprecated
function exec() {
	console.log("WARNING: exec() is deprecated and will be removed in the future. Use $.child_process.exec() instead.");
	$.child_process.exec.apply(this, arguments);
}

function getArgs() {
	console.log("WARNING: getArgs() is deprecated and will be removed in the future. Use $.process.argv instead.");
	return $.process.argv;
}


// Constants

TEXT_ALIGNMENT = {
    LEFT : 1,
    CENTER : 2,
    RIGHT : 4
};
Object.freeze(TEXT_ALIGNMENT);

TEXT_POSITION = {
    TOP : 0,
    CENTER : 1,
    BOTTOM : 2
};
Object.freeze(TEXT_POSITION);

CONNECION_TEXT_POSITION = {
    SOURCE : 0,
    MIDDLE : 1,
    TARGET : 2
};
Object.freeze(TEXT_POSITION);

CONNECTION_STYLE = {
	LINE_SOLID : 0,
	ARROW_FILL_TARGET : 1,      // 1 << 0
    LINE_DASHED : 2,            // 1 << 1
    LINE_DOTTED : 4,            // 1 << 2
    
    ARROW_NONE : 0,
    ARROW_FILL_SOURCE : 8,      // 1 << 3
    ARROW_HOLLOW_TARGET : 16,   // 1 << 4
    ARROW_HOLLOW_SOURCE : 32,   // 1 << 5
    ARROW_LINE_TARGET : 64,     // 1 << 6
    ARROW_LINE_SOURCE : 128     // 1 << 7
};
Object.freeze(CONNECTION_STYLE);

BORDER = {
    TABBED : 0,
    DOGEAR : 0,
    RECTANGLE : 1,
    NONE : 2
};
Object.freeze(BORDER);

GRADIENT = {
    NONE : -1,
    TOP : 0,
    LEFT : 1,
    RIGHT : 2,
    BOTTOM : 3
};
Object.freeze(GRADIENT);

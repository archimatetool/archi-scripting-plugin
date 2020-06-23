function jArchi (obj) {
	if (obj == selection) // obj is an object
		return obj;
	else // obj is an object, a string or will be casted to a (potentially empty) string
		return model.find(obj || "");
}

jArchi.model = jArchiModel;
jArchi.fs = jArchiFS;

$ = jArchi;

// Application args
function getArgs() {
	return Java.type("org.eclipse.core.runtime.Platform").getApplicationArgs();
}

// Run a system command
function exec() {
	// Split arguments into an array of args
	var args = Array.prototype.slice.call(arguments);
	
	var platform = Java.type("org.eclipse.core.runtime.Platform").getOS();
	if(platform == "macosx") {
		args = ["open", "-a"].concat(args[0]); // for some reason, Mac will only accept one argument
	}

	var runtime = Java.type("java.lang.Runtime").getRuntime();
	runtime.exec(args);
}

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

BORDER = {
    TABBED : 0,
    DOGEAR : 0,
    RECTANGLE : 1,
    NONE : 2
};
Object.freeze(BORDER);


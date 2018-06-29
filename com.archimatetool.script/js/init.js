function jArchi (obj) {
	if (obj == selection) // obj is an object
		return obj;
	else // obj is an object, a string or will be casted to a (potentially empty) string
		return model.find(obj || "");
}

jArchi.model = jArchiModel;
jArchi.fs = jArchiFS;

$ = jArchi;

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

	promptOpenFile: function() {
		var FileDialog = Java.type("org.eclipse.swt.widgets.FileDialog");
		var dialog = new FileDialog(shell, 1 << 12);
		return dialog.open();
	},

	promptOpenDirectory: function() {
		var DirectoryDialog = Java.type("org.eclipse.swt.widgets.DirectoryDialog");
		var dialog = new DirectoryDialog(shell);
		return dialog.open();
	},

	promptSaveFile: function() {
		var FileDialog = Java.type("org.eclipse.swt.widgets.FileDialog");
		var dialog = new FileDialog(shell, 1 << 13);
		return dialog.open();
	},

};
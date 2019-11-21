
def jArchi(obj) {
	if (obj == selection)
		return obj;
	else 
		return model.find(obj != null ? obj : "");        
}

def exit() {
	throw new Exception("__EXIT__");
}

def alert(String message) {
   org.eclipse.jface.dialogs.MessageDialog.openInformation(shell, "Archi", message);
}

def confirm(String message) {
   org.eclipse.jface.dialogs.MessageDialog.openConfirm(shell, "Archi", message);
}

def prompt(String message, String defaultText = "Prompt") {
   def dialog = new org.eclipse.jface.dialogs.InputDialog(shell, "Archi", message, defaultText, null);
   dialog.open();
   return dialog.getValue();
}

def promptOpenFile(options = [ title: "Archi", filterExtensions: [], filename: null ]) {
   def dialog = new org.eclipse.swt.widgets.FileDialog(shell, 1 << 12);
   dialog.text = options.title;
   dialog.filterExtensions = options.filterExtensions;
   dialog.fileName = options.fileName;
   return dialog.open();
}

def promptOpenDirectory(options = [ title: "Archi", filterPath: null]) {
   def dialog = new org.eclipse.swt.widgets.DirectoryDialog(shell);
   dialog.text = options.title;
   dialog.filterPath = options.filterPath;
   return dialog.open();
}

def promptSaveFile(options = [ title: "Archi", filterExtensions: [], filename: null ]) {
   def dialog = new org.eclipse.swt.widgets.FileDialog(shell, 1 << 13);
   dialog.text = options.title;
   dialog.filterExtensions = options.filterExtensions;
   dialog.fileName = options.fileName;
   return dialog.open();
}


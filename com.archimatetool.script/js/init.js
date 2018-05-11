function jArchi (obj) {
	if (obj === Object(obj)) // obj is an object
		return obj;
	else // obj is a string or will be casted to a (potentially empty) string
		return model.find(obj || "");
}

jArchi.fs = jArchiFS;
jArchi.__FILE__ = __JARCHI_FILE__;
jArchi.__DIR__ = __JARCHI_DIR__;

$ = jArchi;

function include(path) {
	var File = Java.type("java.io.File");
	var pathFile = new File(jArchi.__DIR__, path);
	if (pathFile.isFile())
		load(pathFile);
	else
		load(path);
}
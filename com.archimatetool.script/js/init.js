function jArchi (obj) {
	if (obj == model || obj == selection) // obj is an object
		return obj;
	else // obj is an object, a string or will be casted to a (potentially empty) string
		return model.find(obj || "");
}

jArchi.fs = jArchiFS;

$ = jArchi;
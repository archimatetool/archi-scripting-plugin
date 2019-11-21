package com.archimatetool.script;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public interface ScriptStarter {

	void start(ScriptEngine engine, File file) throws ScriptException, IOException;
}

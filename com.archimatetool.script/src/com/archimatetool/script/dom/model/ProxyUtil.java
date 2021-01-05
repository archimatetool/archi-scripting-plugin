/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

/**
 * ProxyUtil for creating Proxy Java objects
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ProxyUtil {
    
    /**
     * Map implementation for GraalVM
     * 
     * This is so we can call map.x and map["x"] in JS
     * 
     * We return a Map rather than just a ProxyObject so we can treat it like a map internally (unit tests)
     * and to be in line with Nashorn which uses a Map.
     * 
     * See https://github.com/archimatetool/archi-scripting-plugin/issues/87
     * 
     * See also:
     * 
     * https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.polyglot/src/org/graalvm/polyglot/proxy/ProxyObject.java
     * https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.polyglot/src/org/graalvm/polyglot/proxy/ProxyExecutable.java
     * https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.polyglot/src/org/graalvm/polyglot/proxy/ProxyArray.java
     * 
     * For using ProxyExecutable:
     * 
     * https://github.com/oracle/graaljs/issues/371
     *
     */
    private static class GraalMap extends HashMap<String, Object> implements ProxyObject {
        
        /**
         * Wrap a GraalVM ProxyObject
         */
        private final ProxyObject proxyObject = ProxyObject.fromMap(this);
        
        /**
         * List of method names we support in our ProxyExecutable implementation
         */
        private final List<String> supportedMethods = Arrays.asList("get", "size");
        
        /**
         * This implementation of ProxyExecutable will execute certain HashMap methods
         */
        private ProxyExecutable getProxyExecutable(String key) {
            return new ProxyExecutable() {
                @Override
                public Object execute(Value... args) {
                    switch(key) {
                        case "get":
                            return get(args[0].asString());
                        case "size":
                            return size();
                        //case "put":
                        //    return put(args[0].asString(), args[1]);
                    }
                    
                    return null;
                }
            };
        }
        
        /**
         * @return true if we support a given HashMap method
         */
        private boolean isSupportedMethod(String method) {
            return supportedMethods.contains(method);
        }

        @Override
        public Object getMember(String key) {
            return isSupportedMethod(key) ? getProxyExecutable(key) : proxyObject.getMember(key);
        }

        @Override
        public Object getMemberKeys() {
            return proxyObject.getMemberKeys();
        }

        @Override
        public boolean hasMember(String key) {
            return proxyObject.hasMember(key) || isSupportedMethod(key);
        }

        @Override
        public void putMember(String key, Value value) {
            proxyObject.putMember(key, value);
        }

        @Override
        public boolean removeMember(String key) {
            return proxyObject.removeMember(key);
        }
    }
    
    public static Map<String, Object> createMap() {
        return isGraalVM() ? new GraalMap() : new HashMap<>();
    }

    public static boolean isGraalVM() {
        return "com.oracle.truffle.js.scriptengine.GraalJSScriptEngine".equals(System.getProperty("script.engine"));
    }
}

/*
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

/**
 * Created on 10-Sep-07
 * 
 */
public class JVMExitSecurityException extends SecurityException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private int exitStatus;

    public JVMExitSecurityException(int status) {
        super("exit JVM-" + status);
        exitStatus = status;
    }

    public int getExitStatus() {
        return this.exitStatus;
    }
}

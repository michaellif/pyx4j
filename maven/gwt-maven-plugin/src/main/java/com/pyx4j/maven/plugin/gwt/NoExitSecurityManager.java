/*
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

import java.security.Permission;

/**
 * Created on 10-Sep-07
 */
public class NoExitSecurityManager extends SecurityManager {

    static final NoExitSecurityManager INSTANCE = new NoExitSecurityManager();

    private NoExitSecurityManager() {
        // nop
    }

    /**
     * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
     */
    @Override
    public void checkPermission(Permission permission) {
    }

    /**
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
        throw new JVMExitSecurityException(status);
    }
}

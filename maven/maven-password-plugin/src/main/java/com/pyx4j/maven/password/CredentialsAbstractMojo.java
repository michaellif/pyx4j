/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-02-02
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.password;

import java.lang.reflect.Method;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;

/**
 * 
 */
public abstract class CredentialsAbstractMojo extends AbstractMojo {

    static final String SECURITY_DISPATCHER_CLASS_NAME = "org.sonatype.plexus.components.sec.dispatcher.SecDispatcher";

    /**
     * The Maven settings reference.
     * 
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;

    /**
     * Plexus container, needed to manually lookup components.
     * 
     * To be able to use Password Encryption
     * http://maven.apache.org/guides/mini/guide-encryption.html
     */
    protected PlexusContainer container;

    /**
     * @parameter default-value="email";
     */
    protected String usernameName;

    /**
     * @parameter default-value="password";
     */
    protected String passwordName;

    protected String decryptPassword(String password) {
        if (password != null) {
            try {
                final Class<?> securityDispatcherClass = container.getClass().getClassLoader().loadClass(SECURITY_DISPATCHER_CLASS_NAME);
                final Object securityDispatcher = container.lookup(SECURITY_DISPATCHER_CLASS_NAME, "maven");
                final Method decrypt = securityDispatcherClass.getMethod("decrypt", String.class);
                return (String) decrypt.invoke(securityDispatcher, password);
            } catch (Exception e) {
                getLog().warn("security features are disabled. Cannot find plexus security dispatcher", e);
            }
        }
        getLog().warn("password could not be decrypted");
        return password;
    }
}

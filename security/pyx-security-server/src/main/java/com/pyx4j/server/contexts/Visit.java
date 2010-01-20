/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 11-Sep-06
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.contexts;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.UserVisit;

/**
 * The way to access the session is Context.getVisit()
 */
public class Visit implements Serializable {

    private static final long serialVersionUID = -8328593691009613691L;

    private UserVisit userVisit;

    private Acl acl;

    private final Hashtable<String, Serializable> attributes;

    private transient final Hashtable<String, Object> transientAttributes;

    private long requestIDCount = 0;

    public Visit() {
        this.userVisit = null;
        this.attributes = new Hashtable<String, Serializable>();
        this.transientAttributes = new Hashtable<String, Object>();
    }

    /**
     * Returns true if the user is logged in.
     */

    public boolean isUserLoggedIn() {
        return (this.userVisit != null);
    }

    public UserVisit getUserVisit() {
        return userVisit;
    }

    public Acl getAcl() {
        return acl;
    }

    protected void beginSession(UserVisit userVisit, Acl acl) {
        this.userVisit = userVisit;
        this.acl = acl;
    }

    protected void endSession() {
        this.userVisit = null;
        this.acl = null;
    }

    public Serializable getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this.attributes.keys();
    }

    public Serializable removeAttribute(String name) {
        return this.attributes.remove(name);
    }

    public void setAttribute(String name, Serializable value) {
        this.attributes.put(name, value);
    }

    public Enumeration<String> getTransientAttributeNames() {
        return this.transientAttributes.keys();
    }

    public Object getTransientAttribute(String name) {
        return this.transientAttributes.get(name);
    }

    public Object removeTransientAttribute(String name) {
        return this.transientAttributes.remove(name);
    }

    public void setTransientAttribute(String name, Object value) {
        this.transientAttributes.put(name, value);
    }

    public synchronized long getNewRequestID() {
        requestIDCount++;
        return requestIDCount;
    }

    @Override
    public String toString() {
        if (this.userVisit == null) {
            return "anonymous";
        } else {
            return this.userVisit.getName();
        }
    }

}

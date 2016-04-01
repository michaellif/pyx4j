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
 */
package com.pyx4j.server.contexts;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.pyx4j.commons.Consts;
import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.UserVisit;

/**
 * The way to access the session is ServerContext.getVisit()
 */
public class Visit implements Serializable {

    private static final long serialVersionUID = 1390138592827996655L;

    private UserVisit userVisit;

    private Acl acl;

    private transient boolean aclChanged;

    protected long aclTimeStamp;

    protected long aclRevalidationTimeStamp;

    private static final long aclRevalidationDelayMillis = 1 * Consts.MIN2MSEC;

    private final Hashtable<String, Serializable> attributes;

    private transient Hashtable<String, Object> transientAttributes;

    private long requestIDCount = 0;

    private boolean loginViaAccessToken;

    private transient boolean changed;

    private final String sessionToken;

    private transient ReadWriteLock sessionGuardLock;

    public Visit(String sessionToken) {
        this.userVisit = null;
        this.sessionToken = sessionToken;
        this.attributes = new Hashtable<String, Serializable>();
        this.transientAttributes = new Hashtable<String, Object>();
        this.sessionGuardLock = new ReentrantReadWriteLock();
    }

    private Object readResolve() {
        this.changed = false;
        this.transientAttributes = new Hashtable<String, Object>();
        this.sessionGuardLock = new ReentrantReadWriteLock();
        return this;
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
        this.changed = true;
        this.aclTimeStamp = System.currentTimeMillis();
        this.aclRevalidationTimeStamp = aclTimeStamp;
    }

    protected void endSession() {
        this.userVisit = null;
        this.acl = null;
    }

    public boolean isChanged() {
        return changed || ((this.userVisit != null) && (this.userVisit.isChanged()));
    }

    /**
     * Gae Session Hack
     */
    void unChanged() {
        changed = false;
        if (this.userVisit != null) {
            this.userVisit.unChanged();
        }
    }

    public boolean isAclRevalidationRequired(String clientAclTimeStamp) {
        return (aclRevalidationTimeStamp + aclRevalidationDelayMillis) < System.currentTimeMillis()
                || ((clientAclTimeStamp != null) && (aclTimeStamp != Long.parseLong(clientAclTimeStamp)));
    }

    public void setAclRevalidationRequired() {
        aclRevalidationTimeStamp = 0;
    }

    public long getAclTimeStamp() {
        return aclTimeStamp;
    }

    void aclRevalidated() {
        this.changed = true;
        this.aclRevalidationTimeStamp = System.currentTimeMillis();
    }

    public boolean isAclChanged() {
        return aclChanged;
    }

    public void setAclChanged(boolean aclChanged) {
        this.aclChanged = aclChanged;
        this.aclRevalidationTimeStamp = System.currentTimeMillis();
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public boolean isLoginViaAccessToken() {
        return loginViaAccessToken;
    }

    public void setLoginViaAccessToken() {
        this.loginViaAccessToken = true;
    }

    public void resetLoginViaAccessToken() {
        this.loginViaAccessToken = false;
    }

    public Serializable getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this.attributes.keys();
    }

    public Serializable removeAttribute(String name) {
        this.changed = true;
        return this.attributes.remove(name);
    }

    public void setAttribute(String name, Serializable value) {
        this.attributes.put(name, value);
        this.changed = true;
    }

    public Map<String, Object> getTransientAttributes() {
        return this.transientAttributes;
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

    /**
     * This only guards RPC Requests for now.
     *
     * @return
     */
    public ReadWriteLock getSessionGuardLock() {
        return sessionGuardLock;
    }

    @Override
    public String toString() {
        if (this.userVisit == null) {
            return "anonymous";
        } else {
            return this.userVisit.toString();
        }
    }

}

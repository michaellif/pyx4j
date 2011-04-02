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
 * Created on 2011-03-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server.mock;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class MockHttpSession implements HttpSession {

    private final long creationTime = System.currentTimeMillis();

    private static int count = 0;

    private final int id = (count++);

    protected Hashtable<String, Object> attributes = new Hashtable<String, Object>();

    public MockHttpSession() {
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return "test-" + id;
    }

    @Override
    public long getLastAccessedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getMaxInactiveInterval() {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate() {
        attributes.clear();
        if (TestLifecycle.threadLocalContext.get().session == this) {
            TestLifecycle.threadLocalContext.get().session = null;
        }
    }

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

}

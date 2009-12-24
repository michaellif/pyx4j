/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.util.HashMap;
import java.util.Map;

import com.google.apphosting.api.ApiProxy;

/**
 * AppEngine test environment as defined in
 * http://code.google.com/appengine/docs/java/howto/unittesting.html
 * 
 * @author icoloma
 */
public class TestEnvironment implements ApiProxy.Environment {

    private String appId = "test";

    private String requestNamespace = "";

    private String versionId = "1.0";

    public String getEmail() {
        throw new UnsupportedOperationException();
    }

    public boolean isLoggedIn() {
        throw new UnsupportedOperationException();
    }

    public boolean isAdmin() {
        throw new UnsupportedOperationException();
    }

    public String getAuthDomain() {
        throw new UnsupportedOperationException();
    }

    public String getRequestNamespace() {
        return requestNamespace;
    }

    public Map<String, Object> getAttributes() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("com.google.appengine.server_url_key", "http://localhost:8080");
        return map;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setRequestNamespace(String requestNamespace) {
        this.requestNamespace = requestNamespace;
    }
}
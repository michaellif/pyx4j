/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
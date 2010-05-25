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
 * Created on Jan 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.rpc;

import java.io.Serializable;
import java.util.Set;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;

@SuppressWarnings("serial")
public class AuthenticationResponse implements Serializable {

    private UserVisit userVisit;

    private Set<Behavior> behaviors;

    private int maxInactiveInterval;

    private String sessionCookieName;

    private String logoutURL;

    public AuthenticationResponse() {

    }

    public Set<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(Set<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    /**
     * @return an integer specifying the number of seconds this session remains open
     *         between client requests
     */
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /**
     * See http://tomcat.apache.org/tomcat-6.0-doc/config/systemprops.html on how to
     * change COOKIE NAME.
     */
    public String getSessionCookieName() {
        return sessionCookieName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public UserVisit getUserVisit() {
        return userVisit;
    }

    public void setUserVisit(UserVisit userVisit) {
        this.userVisit = userVisit;
    }

    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

}

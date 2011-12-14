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
 * Created on Dec 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ClientSystemInfo implements Serializable {

    private String buildLabel;

    private boolean script;

    private String userAgent;

    private long startTime;

    private String timeZoneInfo;

    private long serverTimeDelta;

    public ClientSystemInfo() {

    }

    public String getBuildLabel() {
        return this.buildLabel;
    }

    public void setBuildLabel(String buildLabel) {
        this.buildLabel = buildLabel;
    }

    public boolean isHostedMode() {
        return !isScript();
    }

    public boolean isScript() {
        return this.script;
    }

    public void setScript(boolean script) {
        this.script = script;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getServerTimeDelta() {
        return this.serverTimeDelta;
    }

    public void setServerTimeDelta(long serverTimeDelta) {
        this.serverTimeDelta = serverTimeDelta;
    }

    public String getTimeZoneInfo() {
        return timeZoneInfo;
    }

    public void setTimeZoneInfo(String timeZoneInfo) {
        this.timeZoneInfo = timeZoneInfo;
    }
}

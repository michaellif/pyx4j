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
 */
package com.pyx4j.config.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.shared.ApplicationMode;

public abstract class ClientApplicationVersion {

    private static ClientApplicationVersion instance = GWT.create(ClientApplicationVersion.class);

    public static ClientApplicationVersion instance() {
        return instance;
    }

    public abstract String getBuildLabel();

    public abstract Date getBuildDate();

    public abstract String getScmRevision();

    public abstract String getPyxScmRevision();

    public String getBuildInformation() {
        String version = "";
        if (ApplicationMode.isDevelopment()) {
            version = "Dev Mode ";
        }
        version += getBuildLabel() + " " + TimeUtils.simpleFormat(getBuildDate(), "yyyy-MM-dd HH:mm") + " svn:" + getScmRevision() + " pyx.svn:"
                + getPyxScmRevision();
        return version;
    }
}

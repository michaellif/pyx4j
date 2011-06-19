/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client;

import com.google.gwt.core.client.GWT;

public class ClentNavigUtils {

    /**
     * Used for inter-modules redirections.
     * Consider http://localhost:8888/vista/ and http://localhost:8888/vista/index.html
     */
    public static String getDeploymentBaseURL() {
        String url = GWT.getModuleBaseURL();
        String module = GWT.getModuleName();
        if (url.endsWith(module + "/")) {
            url = url.substring(0, url.length() - (module.length() + 1));
        }
        return url;
    }
}

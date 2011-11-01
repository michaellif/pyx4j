/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

public class PMSiteClientPreferences {

    public static String getClientPref(final String prefName) {
        Map<String, String> prefMap = getClientPrefMap();
        if (prefMap == null || prefMap.size() == 0) {
            return null;
        }
        return prefMap.get(prefName);
    }

    public static void setClientPref(final String prefName, final String prefValue) {
        StringBuffer prefStr = new StringBuffer();

        Map<String, String> prefMap = getClientPrefMap();
        if (prefMap == null || prefMap.size() == 0) {
            prefStr.append(prefName + ":" + prefValue);
        } else {
            // add new value
            prefMap.put(prefName, prefValue);
            // build cookie string
            for (String name : prefMap.keySet()) {
                String value = prefMap.get(name);
                if (prefStr.length() > 0) {
                    prefStr.append(";");
                }
                prefStr.append(name + ":" + value);
            }
        }
        ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie("pmsitePref", prefStr.toString()));
    }

    private static Map<String, String> getClientPrefMap() {
        Cookie pmsitePrefCookie = null;
        List<Cookie> cookies = ((WebRequest) RequestCycle.get().getRequest()).getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("pmsitePref".equals(cookie.getName())) {
                pmsitePrefCookie = cookie;
                break;
            }
        }
        Map<String, String> prefMap = null;
        if (pmsitePrefCookie != null) {
            prefMap = new java.util.Hashtable<String, String>();
            String nvpStr = pmsitePrefCookie.getValue();
            String[] nvpArr = nvpStr.split(";");
            for (String nvp : nvpArr) {
                String[] nv = nvp.split(":");
                try {
                    prefMap.put(nv[0], nv[1]);
                } catch (ArrayIndexOutOfBoundsException ignore) {
                    // do nothing
                }
            }
        }
        return prefMap;
    }
}

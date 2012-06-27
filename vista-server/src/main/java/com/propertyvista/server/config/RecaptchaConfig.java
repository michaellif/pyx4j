/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.util.HashMap;
import java.util.Map;

import com.propertyvista.server.common.security.VistaAntiBot;

public class RecaptchaConfig {

    private final String recaptchaPublicKey;

    private final String recaptchaPrivateKey;

    public RecaptchaConfig(String recaptchaPublicKey, String recaptchaPrivateKey) {
        this.recaptchaPrivateKey = recaptchaPrivateKey;
        this.recaptchaPublicKey = recaptchaPublicKey;
    }

    private static Map<String, RecaptchaConfig> domainsMap = new HashMap<String, RecaptchaConfig>();

    static {
        domainsMap.put("prospectportalsite.com", new RecaptchaConfig("6LeWXM8SAAAAAH661toeFqn51__WNXL6o0eoDqGa", "6LeWXM8SAAAAAAJ694pb4dsHmOduEG2ONfWe7WuE"));
        domainsMap.put("propertyvista.com", new RecaptchaConfig("6LeXXM8SAAAAAIR5XVBundDyFO072XcQ0IYsSD0u", "6LeXXM8SAAAAAGN6S3-bVjixUB6gAiAdgwWPtRXu"));
        domainsMap.put("residentportalsite.com", new RecaptchaConfig("6Ld0Xc8SAAAAAMfjEb9vPZZtYb-DQvAtxy8LSeyw", "6Ld0Xc8SAAAAAO3dWBtLKuhQkkMV8b-iasUk3dXs"));
        domainsMap.put("birchwoodsoftwaregroup.com",
                new RecaptchaConfig("6LdzXc8SAAAAAOKyhb7lGTGWPATHH8uYjeXg5jUc", "6LdzXc8SAAAAADehmgyofUx0zjuBgghbssZCIP_X"));
    }

    public static String getReCaptchaPublicKey() {
        return domainsMap.get(VistaAntiBot.getRequestServerNameBase()).recaptchaPublicKey;
    }

    public static String getReCaptchaPrivateKey() {
        return domainsMap.get(VistaAntiBot.getRequestServerNameBase()).recaptchaPrivateKey;
    }

}

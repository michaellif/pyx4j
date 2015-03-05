/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2015
 * @author vlads
 */
package com.propertyvista.crm.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.WalkMe;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.shared.config.VistaFeatures;

public class CrmWalkMe {

    public static void init() {
        AppSite.getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {

            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                setupVariables();
                WalkMe.load(null);
            }

        });
    }

    private static void setupVariables() {
        JsArrayString behaviors = JavaScriptObject.createArray().cast();
        if (VistaFeatures.instance().yardiIntegration()) {
            behaviors.push("Yardi");
            if (SecurityController.check(VistaCrmBehavior.YardiLoads)) {
                behaviors.push("YardiLoads");
            }
        }
        if (SecurityController.check(VistaCrmBehavior.CreditCheckBasic)) {
            behaviors.push("CreditCheckBasic");
        }
        WalkMe.setupWalkMeVariables(behaviors);
    }
}

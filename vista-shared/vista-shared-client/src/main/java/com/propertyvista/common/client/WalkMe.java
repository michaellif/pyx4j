/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 19, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.AjaxJSLoader;

public class WalkMe {

    private static final Logger log = LoggerFactory.getLogger(WalkMe.class);

    private static String jsApi;

    public static void enable(String walkMeJsAPIUrl) {
        jsApi = walkMeJsAPIUrl;
    }

    public static void load() {
        if (ApplicationMode.offlineDevelopment || CommonsStringUtils.isEmpty(jsApi)) {
            return;
        }

        log.debug("WalkMe Loading...");

        createOnWalkMeReady();

        AjaxJSLoader.load(jsApi, new AjaxJSLoader.IsJSLoaded() {

            @Override
            public boolean isLoaded() {
                return isWalkMeJSLoaded();
            };

        }, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                log.debug("WalkMe Load error", caught);
            }

            @Override
            public void onSuccess(Void result) {
                log.debug("WalkMe Loaded");
            }
        });
    }

    private native static void createOnWalkMeReady() /*-{ $wnd.walkme_ready = function() { $wnd.vista_walkme_loaded = true;}; }-*/;

    private native static boolean isWalkMeJSLoaded() /*-{ return typeof $wnd.vista_walkme_loaded != "undefined"; }-*/;

}

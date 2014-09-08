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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.AjaxJSLoader;

/**
 * http://support.walkme.com/customer/portal/articles/1561564-walkme-api
 *
 */
public class WalkMe {

    private static final Logger log = LoggerFactory.getLogger(WalkMe.class);

    private static String jsApi;

    private static boolean initialize = true;

    public static void enable(String walkMeJsAPIUrl) {
        jsApi = walkMeJsAPIUrl;
    }

    public static void load(final AsyncCallback<Void> callback) {
        if (ApplicationMode.offlineDevelopment || CommonsStringUtils.isEmpty(jsApi)) {
            return;
        }

        final boolean logInitialize = initialize;
        if (initialize) {
            log.debug("WalkMe Loading...");
            createOnWalkMeReady();
            initialize = false;
        }

        AjaxJSLoader.load(jsApi, new AjaxJSLoader.IsJSLoaded() {

            @Override
            public boolean isLoaded() {
                return isWalkMeJSLoaded();
            };

        }, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                log.debug("WalkMe Load error", caught);
                if (callback != null) {
                    callback.onFailure(caught);
                }
            }

            @Override
            public void onSuccess(Void result) {
                if (logInitialize) {
                    log.debug("WalkMe Loaded");
                }
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        });
    }

    private native static void createOnWalkMeReady() /*-{ $wnd.walkme_ready = function() { $wnd.vista_walkme_loaded = true;}; }-*/;

    private native static boolean isWalkMeJSLoaded() /*-{ return typeof $wnd.vista_walkme_loaded != "undefined"; }-*/;

    public native static void setupWalkMeVariables(JsArrayString behaviors) /*-{ $wnd.vistaWalkMeBehaviors = behaviors; }-*/;

    public static void obtainWalkthrus(final AsyncCallback<Map<Integer, String>> callback) {
        load(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(getWalkthrus());
            }
        });
    }

    public static class WalkThru extends JavaScriptObject {

        protected WalkThru() {
        }

        public final native int getId() /*-{  return this.Id; }-*/;

        public final native String getName() /*-{  return this.Name; }-*/;
    }

    private native static JsArray<WalkThru> native_getWalkthrus() /*-{  return $wnd.WalkMeAPI.getWalkthrus(true); }-*/;

    private static Map<Integer, String> getWalkthrus() {
        Map<Integer, String> walkthrus = new HashMap<>();
        JsArray<WalkThru> nwt = native_getWalkthrus();
        for (int i = 0; i < nwt.length(); i++) {
            WalkThru wt = nwt.get(i);
            walkthrus.put(wt.getId(), wt.getName());
            log.debug("got WalkThru {} '{}'", wt.getId(), wt.getName());
        }
        return walkthrus;
    }

    public static void startWalkthruById(int walkthruId) {
        log.debug("startWalkthruById '{}'", walkthruId);
        native_startWalkthruById(walkthruId);
    }

    private native static void native_startWalkthruById(int walkthruId) /*-{  $wnd.WalkMeAPI.startWalkthruById(walkthruId); }-*/;

}

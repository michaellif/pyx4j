/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.AjaxJSLoader;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.services.FeedbackService;

public class GetSatisfaction {

    private final static Logger log = LoggerFactory.getLogger(GetSatisfaction.class);

    private static final I18n i18n = I18n.get(GetSatisfaction.class);

    // base DNS Should be the same as on server.

    private static final String jsApi
    // = "s3.amazonaws.com/getsatisfaction.com/javascripts/feedback-v2.js";
    = "getsatisfaction.com/javascripts/feedback-v2.js";

    static boolean authenticated = false;

    public static void open() {
        ensureGetSatisfactionAPI(new DefaultAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {

                ensureGetSatisfactionLogin(new DefaultAsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        openGetSatisfactionWidget();
                    }
                });
            }
        });
    }

    private static void ensureGetSatisfactionAPI(final AsyncCallback<Void> callback) {
        AjaxJSLoader.load(jsApi, new AjaxJSLoader.IsJSLoaded() {

            @Override
            public boolean isLoaded() {
                return isGetSatisfactionJSLoaded();
            };

        }, callback);
    }

    private native static boolean isGetSatisfactionJSLoaded() /*-{
		return typeof $wnd.GSFN != "undefined";
    }-*/;

    public static void ensureGetSatisfactionLogin(final AsyncCallback<Void> callback) {
        if (authenticated) {
            callback.onSuccess(null);
        } else {
            FeedbackService srv = GWT.create(FeedbackService.class);
            srv.obtainSetsatisfactionLoginUrl(new DefaultAsyncCallback<String>() {

                @Override
                public void onSuccess(String url) {
                    log.debug("login via {}", url);
                    loginToGetSatisfaction(url, callback);
                }
            });
        }
    }

    private native static boolean isGetSatisfactionLoginLoaded() /*-{
		return typeof $wnd.GSFN_fastpass != "undefined";
    }-*/;;

    private static void loginToGetSatisfaction(String url, final AsyncCallback<Void> callback) {
        int idx = url.indexOf("://");
        AjaxJSLoader.load(url.substring(idx + 3), new AjaxJSLoader.IsJSLoaded() {

            @Override
            public boolean isLoaded() {
                return isGetSatisfactionLoginLoaded();
            };

        }, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UserRuntimeException(i18n.tr("Feedback Service unavailable"));
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(null);
                authenticated = true;
            }

        });
    }

    private native static void openGetSatisfactionWidget() /*-{
        var feedback_widget_options = {};
        feedback_widget_options.display = "overlay";
        feedback_widget_options.company = "property_vista";
        feedback_widget_options.placement = "hidden";
        feedback_widget_options.color = "#222";
        feedback_widget_options.style = "question";
        feedback_widget_options.container = "feedback_widget_container";
        //feedback_widget_options.product = "property_vista_crm";
        feedback_widget_options.limit = "3";
        feedback_widget_options.container.style = ("top", "0");

        $wnd.GSFN.feedback_widget.prototype.local_base_url = "http://support.propertyvista.com";
        $wnd.GSFN.feedback_widget.prototype.local_ssl_base_url = "http://support.propertyvista.com";

        var feedback_widget = new $wnd.GSFN.feedback_widget(
                feedback_widget_options);

        feedback_widget.show();
    }-*/;

}

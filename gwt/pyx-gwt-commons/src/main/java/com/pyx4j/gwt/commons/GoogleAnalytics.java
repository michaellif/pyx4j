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
 * Created on Jul 15, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

/**
 * Injects Google Analytics Tracker code
 * 
 * @see http://code.google.com/apis/analytics/docs/tracking/gaTrackingOverview.html
 * 
 */
public class GoogleAnalytics {

    private static Logger log = LoggerFactory.getLogger(GoogleAnalytics.class);

    private static String googleAnalyticsTracker;

    private static String domainName = null;

    private static boolean isLoaded;

    public static void setGoogleAnalyticsTracker(String googleAnalyticsTracker) {
        setGoogleAnalyticsTracker(googleAnalyticsTracker, null);
    }

    public static void setGoogleAnalyticsTracker(String googleAnalyticsTracker, String domainName) {
        String h = Window.Location.getHostName();
        if (!"localhost".equals(h) && !"127.0.0.1".equals(h)) {
            GoogleAnalytics.googleAnalyticsTracker = googleAnalyticsTracker;
            GoogleAnalytics.domainName = domainName;
        }
    }

    /**
     * Cross-Domain Tracking
     * 
     * "none" or ".example-petstore.com"
     * 
     * @see http://code.google.com/apis/analytics/docs/tracking/gaTrackingSite.html
     */
    public static void setDomainName(String domainName) {
        GoogleAnalytics.domainName = domainName;
    }

    public static void track(final String actionName) {
        if (googleAnalyticsTracker == null) {
            return;
        }
        if (isLoaded) {
            trackDeferred(actionName);
        } else {
            DeferredCommand.addCommand(new Command() {
                @Override
                public void execute() {

                    AjaxJSLoader.load("(ssl|www).google-analytics.com/ga.js", new AjaxJSLoader.IsJSLoaded() {

                        @Override
                        public native boolean isLoaded()
                        /*-{ return typeof $wnd._gat != "undefined"; }-*/;

                    }, new Runnable() {

                        @Override
                        public void run() {
                            isLoaded = true;
                            trackDeferred(actionName);
                        }
                    });

                }
            });
        }
    }

    private static void trackDeferred(final String actionName) {
        DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                try {
                    if (domainName != null) {
                        setDomainName(googleAnalyticsTracker, domainName);
                        domainName = null;
                    }

                    log.debug("googleAnalyticsTrack {}", actionName);
                    trackPageView(googleAnalyticsTracker, actionName);
                } catch (Throwable e) {
                    log.error("GoogleAnalytics error", e);
                }
            }
        });
    }

    private static native void trackPageView(String tracker, String pageUrl)
    /*-{ $wnd._gat._getTracker(tracker)._trackPageview(pageUrl); }-*/;

    private static native void setDomainName(String tracker, String domainName)
    /*-{ $wnd._gat._getTracker(tracker)._setDomainName(domainName); }-*/;

}

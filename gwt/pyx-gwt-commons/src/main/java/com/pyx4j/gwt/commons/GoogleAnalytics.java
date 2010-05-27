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

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * Injects Google Analytics Tracker code
 * 
 * @see http://code.google.com/apis/analytics/docs/tracking/gaTrackingOverview.html
 * 
 */
public class GoogleAnalytics {

    private static Logger log = LoggerFactory.getLogger(GoogleAnalytics.class);

    private static boolean alreadyInjected = false;

    /** there are no callback in ga.js, so we use timer */
    private static boolean loaded = false;

    private static Timer loadTimer;

    private static int timeoutCountdown = 10;

    private static Vector<String> queuedActions;

    private static String googleAnalyticsTracker;

    private static String domainName = null;

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
        if (!loaded) {
            // Allow for "Standard Setup" in html page
            if (isInstalled()) {
                loaded = true;
                configure();
            } else {
                if (queuedActions == null) {
                    queuedActions = new Vector<String>();
                }
                queuedActions.add(actionName);

                if (!alreadyInjected) {
                    alreadyInjected = true;
                    load();
                }
                return;
            }
        }

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                try {
                    log.debug("googleAnalyticsTrack {}", actionName);
                    trackPageView(googleAnalyticsTracker, actionName);
                } catch (Throwable e) {
                    log.error("GoogleAnalytics error", e);
                }
            }
        });
    }

    private static void load() {
        injectJS();
        loadTimer = new Timer() {
            @Override
            public void run() {
                if (isInstalled()) {
                    loaded = true;
                    loadTimer.cancel();
                    loadTimer = null;
                    configure();
                    fireQueuedActions();
                } else {
                    timeoutCountdown--;
                    if (timeoutCountdown == 0) {
                        loadTimer.cancel();
                        loadTimer = null;
                        // Disable, GoogleAnalytics, probably working OffLine 
                        GoogleAnalytics.googleAnalyticsTracker = null;
                        log.error("GoogleAnalytics load timeout");
                    }
                }
            }
        };
        loadTimer.scheduleRepeating(7 * 1000);
    }

    private static void fireQueuedActions() {
        if (queuedActions != null) {
            for (String actionName : queuedActions) {
                try {
                    log.debug("googleAnalyticsTrack {}", actionName);
                    trackPageView(googleAnalyticsTracker, actionName);
                } catch (Throwable e) {
                    log.error("GoogleAnalytics error", e);
                }
            }
            queuedActions.clear();
            queuedActions = null;
        }
    }

    private static void configure() {
        if (domainName != null) {
            setDomainName(googleAnalyticsTracker, domainName);
        }
    }

    private static void injectJS() {
        String protocolPrefix = Window.Location.getProtocol().equals("https:") ? "https://ssl" : "http://www";
        Document doc = Document.get();
        ScriptElement script = doc.createScriptElement();
        script.setSrc(protocolPrefix + ".google-analytics.com/ga.js");
        script.setType("text/javascript");
        doc.getBody().appendChild(script);
    }

    private static final native boolean isInstalled()
    /*-{ return typeof $wnd._gat != "undefined"; }-*/;

    private static native void trackPageView(String tracker, String pageUrl)
    /*-{ $wnd._gat._getTracker(tracker)._trackPageview(pageUrl); }-*/;

    private static native void setDomainName(String tracker, String domainName)
    /*-{ $wnd._gat._getTracker(tracker)._setDomainName(domainName); }-*/;

}

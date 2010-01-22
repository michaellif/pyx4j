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

public class GoogleAnalytics {

    private static Logger log = LoggerFactory.getLogger(GoogleAnalytics.class);

    private static String googleAnalyticsTracker;

    public static void setGoogleAnalyticsTracker(String googleAnalyticsTracker) {
        GoogleAnalytics.googleAnalyticsTracker = googleAnalyticsTracker;
    }

    public static void track(final String actionName) {
        if (googleAnalyticsTracker == null) {
            return;
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

    private static native void trackPageView(String tracker, String pageUrl)
    /*-{ $wnd._gat._getTracker(tracker)._trackPageview(pageUrl); }-*/;
}

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
 * Created on Feb 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.essentials.client.BaseSiteDispatcher;
import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class ExamplesSiteDispatcher extends BaseSiteDispatcher {

    @Override
    public void onModuleLoad() {
        super.onModuleLoad();

        //ApplicationCommon.init();

        if (Window.Location.getHost().endsWith("pyx4j-demo.appspot.com")) {
            AjaxLoader.init("ABQIAAAAZuLUizjWCGkAYOfiIpZpgxSsRVEFJ6vLwIBuBBr9l_DYCz2brRSV7aQHBsZMSBwjF72gUZrsiWfavw");
        } else {
            // pyx4j.com
            AjaxLoader.init("ABQIAAAAZuLUizjWCGkAYOfiIpZpgxT2nw7IAgYZCN3UZ-Glm95U7gTjpRTVD1pxXeXBpUR-ZQ5Z0YCQkesTkg");
        }

        GoogleAnalytics.setGoogleAnalyticsTracker("UA-12949578-1");

        MessageDialog.info("TODO", "TODO");
    }

    @Override
    protected void obtainSite(String siteName, AsyncCallback<SitePanel> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getAppId() {
        return "examples";
    }

}

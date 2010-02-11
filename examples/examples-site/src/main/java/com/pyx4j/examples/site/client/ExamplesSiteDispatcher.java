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

import com.pyx4j.essentials.client.AbstractSiteDispatcher;
import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class ExamplesSiteDispatcher extends AbstractSiteDispatcher {

    @Override
    public void onModuleLoad() {
        super.onModuleLoad();

        //ApplicationCommon.init();

        GoogleAnalytics.setGoogleAnalyticsTracker("UA-12949578-1");

        MessageDialog.info("TODO", "TODO");
    }

    @Override
    public Iterable<SitePanel> getAllSitePanels() {
        // TODO Auto-generated method stub
        return null;
    }
}

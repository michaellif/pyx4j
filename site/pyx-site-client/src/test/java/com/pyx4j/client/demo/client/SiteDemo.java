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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.SitePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SiteDemo extends AbstractSiteDispatcher {

    final Logger log = LoggerFactory.getLogger(SiteDemo.class);

    @Override
    protected void obtainSite(String siteName, AsyncCallback<SitePanel> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getAppId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onSiteLoad() {

    }
}

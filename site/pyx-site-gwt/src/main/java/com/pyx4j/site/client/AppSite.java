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
 * @version $Id: SiteDemo.java 7954 2011-02-02 14:01:03Z michaellif $
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.site.client.place.AppPlaceHistoryMapper;
import com.pyx4j.widgets.client.util.BrowserType;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public abstract class AppSite implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(AppSite.class);

    private static I18n i18n = I18nFactory.getI18n(AppSite.class);

    private static AppSite instance;

    private final AppPlaceHistoryMapper historyMapper;

    private final PlaceHistoryHandler historyHandler;

    private final AppSiteEventBus eventBus;

    private final PlaceController placeController;

    public AppSite() {
        instance = this;

        Element head = Document.get().getElementsByTagName("html").getItem(0);
        head.setPropertyString("xmlns:pyx", "");

        historyMapper = new AppPlaceHistoryMapper();
        historyHandler = new PlaceHistoryHandler(historyMapper);
        eventBus = new AppSiteEventBus();
        placeController = new PlaceController(eventBus);
    }

    public static AppSite instance() {
        return instance;
    }

    public AppPlaceHistoryMapper getHistoryMapper() {
        return historyMapper;
    }

    public PlaceHistoryHandler getHistoryHandler() {
        return historyHandler;
    }

    public AppSiteEventBus getEventBus() {
        return eventBus;
    }

    public PlaceController getPlaceController() {
        return placeController;
    }

    public abstract void onSiteLoad();

    @Override
    public void onModuleLoad() {
        if (ApplicationMode.isDevelopment()) {
            ClientLogger.setDebugOn(true);
        }
        ClientEntityFactory.ensureIEntityImplementations();
        log.debug("{}", BrowserType.getCompiledType());
        onSiteLoad();
    }

    public void hideLoadingIndicator() {
        // Remove the loading icon
        RootPanel loading = RootPanel.get("loading");
        if (loading != null) {
            com.google.gwt.user.client.Element elem = loading.getElement();
            UIObject.setVisible(elem, false);
            DOM.setInnerHTML(elem, "");
            loading.removeFromParent();
            elem.getParentElement().removeChild(elem);
        }
    }
}

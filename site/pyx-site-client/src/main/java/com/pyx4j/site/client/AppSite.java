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
package com.pyx4j.site.client;

import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.UserAgentDetection;
import com.pyx4j.config.client.ClientApplicationVersion;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.gwt.commons.DefaultUnrecoverableErrorHandler;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.client.place.AppPlaceHistoryMapper;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.SiteMap;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public abstract class AppSite implements EntryPoint {

    static {
        ClientEntityFactory.ensureIEntityImplementations();
        ClientApplicationVersion.instance().getBuildLabel();
    }

    private static final Logger log = LoggerFactory.getLogger(AppSite.class);

    private static AppSite instance;

    private final AppPlaceHistoryMapper historyMapper;

    private final AppPlaceHistoryHandler historyHandler;

    private final AppPlaceContorller placeController;

    private final String appId;

    private RootPane<?> rootPane;

    private final ViewFactory viewFactory;

    public final long applicationStartTime = System.currentTimeMillis();

    private final UserAgentDetection userAgentDetection;

    public AppSite(String appId, Class<? extends SiteMap> siteMapClass, ViewFactory viewFactory, AppPlaceDispatcher dispatcher) {
        this.appId = appId;
        this.viewFactory = viewFactory;

        if (dispatcher == null) {
            dispatcher = new AppPlaceDispatcher() {

                @Override
                public AppPlace forwardTo(AppPlace newPlace) {
                    return newPlace;
                }

            };
        }

        String encodedPlace = Window.Location.getParameter(NavigNode.PLACE_ARGUMENT);
        if (encodedPlace != null) {
            // Redirect to proper location with history token
            // Example: app?place=placeName&v1=1&v2=2  -> app#placeName?v1=1&v2=2
            StringBuilder hash = new StringBuilder();
            UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
            hash.append(encodedPlace);

            boolean first = true;
            for (Entry<String, List<String>> me : Window.Location.getParameterMap().entrySet()) {
                String paramName = me.getKey();
                if (paramName.equals("gwt.codesvr")) {
                    continue;
                }
                urlBuilder.removeParameter(paramName);
                if (!paramName.equals(NavigNode.PLACE_ARGUMENT)) {
                    if (first) {
                        hash.append(NavigNode.ARGS_GROUP_SEPARATOR);
                        first = false;
                    } else if (hash.length() > 0) {
                        hash.append(NavigNode.ARGS_SEPARATOR);
                    }
                    hash.append(paramName);
                    hash.append(NavigNode.NAME_VALUE_SEPARATOR);
                    // TODO verify multiple values
                    hash.append(Window.Location.getParameter(paramName));
                }
            }
            urlBuilder.setHash(hash.toString());
            log.debug("redirect {}", urlBuilder);
            Window.Location.assign(urlBuilder.buildString());
        }

        instance = this;
        Element head = Document.get().getElementsByTagName("html").getItem(0);
        head.setPropertyString("xmlns:pyx", "");

        historyMapper = new AppPlaceHistoryMapper(siteMapClass);
        historyHandler = new AppPlaceHistoryHandler(historyMapper);
        placeController = new AppPlaceContorller(ClientEventBus.instance, dispatcher);

        userAgentDetection = new UserAgentDetection(Window.Navigator.getUserAgent());
    }

    public AppSite(String appId, Class<? extends SiteMap> siteMapClass, ViewFactory viewFactory) {
        this(appId, siteMapClass, viewFactory, null);
    }

    public static AppSite instance() {
        return instance;
    }

    public String getAppId() {
        return appId;
    }

    public static ViewFactory getViewFactory() {
        return instance().viewFactory;
    }

    protected void setRootPane(RootPane<?> rootPane) {
        RootLayoutPanel.get().add(rootPane);
        this.rootPane = rootPane;
    }

    public RootPane<?> getRootPane() {
        return rootPane;
    }

    public static AppPlaceHistoryMapper getHistoryMapper() {
        return instance().historyMapper;
    }

    public static AppPlaceHistoryHandler getHistoryHandler() {
        return instance().historyHandler;
    }

    public static AppPlace getWhere() {
        return getPlaceController().getWhere();
    }

    public static EventBus getEventBus() {
        return ClientEventBus.instance;
    }

    public static AppPlaceContorller getPlaceController() {
        return instance().placeController;
    }

    public static String getPlaceId(Place place) {
        return instance().historyMapper.getPlaceId(place);
    }

    public static UserAgentDetection getUserAgentDetection() {
        return instance().userAgentDetection;
    }

    public abstract void onSiteLoad();

    public abstract NotificationAppPlace getNotificationPlace(Notification notification);

    @Override
    public void onModuleLoad() {
        if (ApplicationMode.isDevelopment()) {
            ClientLogger.setDebugOn(true);
            if (Window.Location.getParameter("trace") != null) {
                ClientLogger.setTraceOn(true);
            }
            if (Window.Location.getParameter("usec") != null) {
                ClientSecurityController.setUnsecure();
            }
            AppDevStartTimeMonitor.start();
        } else {
            String debug = Window.Location.getParameter("debug");
            if ((debug != null) && (debug.equals(ClientApplicationVersion.instance().getScmRevision()))) {
                ClientLogger.setDebugOn(true);
            }
        }
        log.debug("{}", BrowserType.getCompiledType());
        if (ApplicationMode.isDevelopment()) {
            log.debug("GWT.getPermutationStrongName       {}", GWT.getPermutationStrongName());
            log.debug("GWT.getModuleName                  {}", GWT.getModuleName());
            log.debug("GWT.getModuleBaseURL               {}", GWT.getModuleBaseURL());
            log.debug("GWT.getHostPageBaseURL             {}", GWT.getHostPageBaseURL());
            log.debug("GWT.getModuleBaseForStaticFiles    {}", GWT.getModuleBaseForStaticFiles());
            log.debug("NavigationUri.getDeploymentBaseURL {}", getDeploymentBaseURL());
            log.debug("NavigationUri.getHostPageURL       {}", getHostPageURL());
        }
        onSiteLoad();
    }

    public void hideLoadingIndicator() {
        // Remove the loading icon
        RootPanel loading = RootPanel.get("loading");
        if (loading != null) {
            com.google.gwt.dom.client.Element elem = loading.getElement();
            UIObject.setVisible(elem, false);
            elem.setInnerHTML("");
            loading.removeFromParent();
            elem.getParentElement().removeChild(elem);
        }
        DefaultUnrecoverableErrorHandler.setApplicationInitialized();
    }

    /**
     * Used for inter-modules redirections.
     * Consider http://localhost:8888/gwt/ and http://localhost:8888/gwt/index.html
     */
    public static String getDeploymentBaseURL() {
        String url = GWT.getModuleBaseURL();
        String module = GWT.getModuleName();
        if (url.endsWith(module + "/")) {
            url = url.substring(0, url.length() - (module.length() + 1));
        }
        return url;
    }

    public static String getHostPageURL() {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol(Window.Location.getProtocol());
        urlBuilder.setHost(Window.Location.getHost());
        String path = Window.Location.getPath();
        if (path != null && path.length() > 0) {
            urlBuilder.setPath(path);
        }
        return urlBuilder.buildString();
    }
}

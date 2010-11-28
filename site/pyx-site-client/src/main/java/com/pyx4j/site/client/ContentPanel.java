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
 * Created on Aug 30, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.commons.AsyncCallbackAggregator;
import com.pyx4j.security.client.ClientContext;

public abstract class ContentPanel extends SimplePanel {

    private static final Logger log = LoggerFactory.getLogger(ContentPanel.class);

    private final SitePanel sitePanel;

    private final List<InlineWidget> inlineWidgets = new ArrayList<InlineWidget>();

    private HandlerRegistration authenticationObtainedHandlerRegistration;

    public SitePanel getSitePanel() {
        return sitePanel;
    }

    public ContentPanel(SitePanel parent) {
        this.sitePanel = parent;
    }

    protected void addInlineWidget(InlineWidget widget) {
        inlineWidgets.add(widget);

    }

    public void populateInlineWidgets(final Map<String, String> args) {

        if (ClientContext.isAuthenticationObtained()) {
            for (InlineWidget inlineWidget : inlineWidgets) {
                inlineWidget.populate(args);
            }
        } else {
            authenticationObtainedHandlerRegistration = ClientContext.addAuthenticationObtainedHandler(new InitializeHandler() {

                @Override
                public void onInitialize(InitializeEvent event) {
                    authenticationObtainedHandlerRegistration.removeHandler();
                    if (!ClientContext.isAuthenticated()) {
                        for (InlineWidget inlineWidget : inlineWidgets) {
                            inlineWidget.populate(args);
                        }
                    }
                }
            });
        }
    }

    public void createInlineWidgets(Iterable<String> widgetIds, AsyncCallback<Void> widgetsAvalable) {
        if (widgetIds == null) {
            widgetsAvalable.onSuccess(null);
            return;
        }
        final AsyncCallbackAggregator callback = new AsyncCallbackAggregator(widgetsAvalable);
        callback.expect();
        for (final String widgetId : widgetIds) {
            //check in local (page) factory
            InlineWidget inlineWidget = null;
            //check in local (page) factory
            if (getSitePanel().getLocalWidgetFactory() != null) {
                inlineWidget = getSitePanel().getLocalWidgetFactory().createWidget(widgetId);
            }
            //check in global factory
            if (inlineWidget == null) {
                inlineWidget = SitePanel.getGlobalWidgetFactory().createWidget(widgetId);
            }

            if (inlineWidget != null) {
                injectInlineWidget(widgetId, inlineWidget);
            } else {
                AsyncInlineWidgetFactory async = getSitePanel().getAsyncInlineWidgetFactory();
                if (async != null) {
                    callback.expect();
                    async.obtainWidget(widgetId, new AsyncCallback<InlineWidget>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(InlineWidget result) {
                            if (result == null) {
                                log.warn("Failed create inline widget {}", widgetId);
                            } else {
                                injectInlineWidget(widgetId, result);
                            }
                            callback.onSuccess(null);
                        }
                    });
                } else {
                    log.warn("Failed create inline widget {}", widgetId);
                }
                continue;
            }
        }
        callback.onSuccess(null);
    }

    protected abstract void injectInlineWidget(String widgetId, InlineWidget inlineWidget);

}

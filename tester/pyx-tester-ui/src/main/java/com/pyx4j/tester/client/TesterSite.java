/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.essentials.client.DefaultErrorHandlerDialog;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.tester.client.theme.TesterPalette;
import com.pyx4j.tester.client.theme.TesterTheme;
import com.pyx4j.tester.client.ui.TesterRootPane;
import com.pyx4j.tester.shared.TesterAuthenticationService;
import com.pyx4j.widgets.client.GlassPanel;

public class TesterSite extends AppSite {

    private static SingletonViewFactory viewFactory = new SingletonViewFactory();

    private Message message;

    public TesterSite() {
        super("test", TesterSiteMap.class, null);
    }

    @Override
    public void onSiteLoad() {
        ApplicationCommon.initRpcGlassPanel();
        if (Window.Location.getParameter("trace") != null) {
            final RPCAppender rpcAppender = new RPCAppender(Level.TRACE);
            rpcAppender.autoFlush(2 * Consts.SEC2MILLISECONDS);
            ClientLogger.addAppender(rpcAppender);
            ClientLogger.setTraceOn(true);
            Window.addWindowClosingHandler(new ClosingHandler() {
                @Override
                public void onWindowClosing(ClosingEvent event) {
                    rpcAppender.flush();
                }
            });
        } else {
            ClientLogger.addAppender(new RPCAppender(Level.WARN));
        }

        RootPanel.get().add(GlassPanel.instance());

        DefaultErrorHandlerDialog.register();

        getHistoryHandler().register(getPlaceController(), getEventBus(), new TesterSiteMap.Landing());

        StyleManager.installTheme(new TesterTheme(), new TesterPalette());

        setRootPane(new TesterRootPane());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        ClientContext.obtainAuthenticationData(GWT.<AuthenticationService> create(TesterAuthenticationService.class), new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    TesterSite.getHistoryHandler().handleCurrentHistory();
                } else {
                    ClientContext.authenticate(GWT.<AuthenticationService> create(TesterAuthenticationService.class), null,
                            new DefaultAsyncCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    TesterSite.getHistoryHandler().handleCurrentHistory();

                                }
                            });
                }
            }
        });

    }

    public static SingletonViewFactory getViewFactory() {
        return viewFactory;
    }

    public void showMessageDialog_v2(String message, String title, String buttonText, Command command) {
        setMessage(new Message(message, title, buttonText, command));
        //TODO getPlaceController().goTo(new AdminSiteMap.GenericMessage());
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}

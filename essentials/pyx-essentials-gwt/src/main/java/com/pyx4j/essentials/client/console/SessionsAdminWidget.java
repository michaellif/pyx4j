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
 * Created on 2010-05-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.console;

import java.util.Map;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.essentials.client.ConfirmActionClickHandler;
import com.pyx4j.essentials.rpc.admin.AdminServices;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.widgets.client.GroupBoxPanel;

public class SessionsAdminWidget extends VerticalPanel implements InlineWidget {

    private final HTML expiredCount;

    private final HTML allCount;

    public SessionsAdminWidget() {
        this.setWidth("100%");

        GroupBoxPanel statisticGroup = new GroupBoxPanel(false);
        statisticGroup.setCaption("Sessions Statistics");
        this.add(statisticGroup);

        statisticGroup.add(allCount = new HTML());
        statisticGroup.add(expiredCount = new HTML());

        GroupBoxPanel cleanupGroup = new GroupBoxPanel(false);
        cleanupGroup.setCaption("Cleanup Sessions");
        this.add(cleanupGroup);

        Anchor removeExpired = new Anchor("Remove Expired Sessions");
        cleanupGroup.add(removeExpired);
        removeExpired.addClickHandler(new ConfirmActionClickHandler("Remove Expired Sessions",
                "Expired Sessions data would be removed\nDo you want to continue?", new Runnable() {
                    @Override
                    public void run() {
                        DeferredActionProcessDialog dd = DeferredActionProcessDialog.start("Remove Expired Sessions", AdminServices.PurgeExpiredSessions.class);
                        dd.addCloseHandler(new CloseHandler<PopupPanel>() {
                            @Override
                            public void onClose(CloseEvent<PopupPanel> event) {
                                refreshStats();
                            }
                        });
                    }
                }));

        Anchor removeAll = new Anchor("Remove All Sessions");
        cleanupGroup.add(removeAll);
        removeAll.addClickHandler(new ConfirmActionClickHandler("Remove All Sessions", "All Sessions data would be removed\nDo you want to continue?",
                new Runnable() {
                    @Override
                    public void run() {
                        DeferredActionProcessDialog dd = DeferredActionProcessDialog.start("Remove All Sessions", AdminServices.PurgeAllSessions.class);
                        dd.addCloseHandler(new CloseHandler<PopupPanel>() {
                            @Override
                            public void onClose(CloseEvent<PopupPanel> event) {
                                refreshStats();
                            }
                        });
                    }
                }));
    }

    private void refreshStats() {
        AsyncCallback<String> callback1 = new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(String value) {
                allCount.setHTML("Total sessions: " + value);
            }

        };

        RPCManager.execute(AdminServices.CountSessions.class, Boolean.TRUE, callback1);

        AsyncCallback<String> callback2 = new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(String value) {
                expiredCount.setHTML("Expired sessions: " + value);
            }

        };

        RPCManager.execute(AdminServices.CountSessions.class, Boolean.FALSE, callback2);
    }

    @Override
    public void populate(Map<String, String> args) {
        refreshStats();
    }

}

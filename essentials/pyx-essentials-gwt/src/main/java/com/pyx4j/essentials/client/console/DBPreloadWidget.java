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
 * Created on Mar 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.console;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.entity.rpc.DatastoreAdminServices;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.widgets.client.dialog.MessageDialog;

class DBPreloadWidget extends SimplePanel implements InlineWidget {

    DBPreloadWidget() {

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        {
            Anchor cleanDB = new Anchor("Remove All Data");
            contentPanel.add(cleanDB);
            cleanDB.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    MessageDialog.confirm("Remove All Data", "All data would be removed\nDo you want to continue?", new Runnable() {
                        @Override
                        public void run() {

                            final AsyncCallback<String> rpcCallback = new AsyncCallback<String>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    MessageDialog.error("RemoveInitialData Service failed", caught);
                                }

                                @Override
                                public void onSuccess(String result) {
                                    MessageDialog.info("DB Reset completed", result);
                                }
                            };

                            RPCManager.execute(DatastoreAdminServices.RemoveAllData.class, null, rpcCallback);
                        }
                    });
                }
            });
        }

        {
            Anchor resetDB = new Anchor("Reset Initial Data");
            contentPanel.add(resetDB);
            resetDB.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    MessageDialog.confirm("Reset Initial Data", "All data would be removed\nDo you want to continue?", new Runnable() {
                        @Override
                        public void run() {

                            final AsyncCallback<String> rpcCallback = new AsyncCallback<String>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    MessageDialog.error("ResetInitialData Service failed", caught);
                                }

                                @Override
                                public void onSuccess(String result) {
                                    MessageDialog.info("DB Reset completed", result);
                                }
                            };

                            RPCManager.execute(DatastoreAdminServices.ResetInitialData.class, null, rpcCallback);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void populate(Map<String, String> args) {
    }

    @Override
    public boolean onBeforeLeaving() {
        return true;
    }

}

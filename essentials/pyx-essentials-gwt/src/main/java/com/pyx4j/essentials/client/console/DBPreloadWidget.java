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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
import com.pyx4j.essentials.rpc.admin.DatastoreAdminServices;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.widgets.client.GroupBoxPanel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

class DBPreloadWidget extends SimplePanel implements InlineWidget {

    VerticalPanel preloadersPanel;

    DBPreloadWidget() {
        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);
        contentPanel.setWidth("100%");

        GroupBoxPanel generalGroup = new GroupBoxPanel(false);
        generalGroup.setCaption("General");
        VerticalPanel general = new VerticalPanel();
        generalGroup.setContainer(general);
        contentPanel.add(generalGroup);

        {
            Anchor cleanDB = new Anchor("Remove All Data");
            general.add(cleanDB);
            cleanDB.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    MessageDialog.confirm("Remove All Data", "All data would be removed\nDo you want to continue?", new Runnable() {
                        @Override
                        public void run() {

                            final boolean useDeferred = true;
                            if (useDeferred) {
                                DeferredActionProcessDialog.start("Remove All Data", DatastoreAdminServices.RemoveAllDataDeferred.class);
                            } else {
                                final AsyncCallback<String> rpcCallback = new BlockingAsyncCallback<String>() {

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
                        }
                    });
                }
            });
        }

        {
            Anchor resetDB = new Anchor("Reset Initial Data");
            general.add(resetDB);
            resetDB.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    MessageDialog.confirm("Reset Initial Data", "All data would be removed\nDo you want to continue?", new Runnable() {
                        @Override
                        public void run() {

                            final AsyncCallback<String> rpcCallback = new BlockingAsyncCallback<String>() {

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

        GroupBoxPanel preloadersGroup = new GroupBoxPanel(true);
        preloadersGroup.setCaption("Preloaders");
        preloadersPanel = new VerticalPanel();
        preloadersPanel.setWidth("100%");
        preloadersGroup.setContainer(preloadersPanel);
        contentPanel.add(preloadersGroup);
    }

    @Override
    public void populate(Map<String, String> args) {
        // Remove all.
        Iterator<Widget> it = preloadersPanel.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        final AsyncCallback<Vector<DataPreloaderInfo>> rpcCallback = new AsyncCallback<Vector<DataPreloaderInfo>>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageDialog.error("ResetInitialData Service failed", caught);
            }

            @Override
            public void onSuccess(Vector<DataPreloaderInfo> result) {
                for (DataPreloaderInfo info : result) {
                    addDataPreloaderInfo(info);
                }
            }
        };
        RPCManager.execute(DatastoreAdminServices.GetPreloaders.class, null, rpcCallback);
    }

    private void addDataPreloaderInfo(final DataPreloaderInfo info) {
        GroupBoxPanel group = new GroupBoxPanel(false);
        group.setCaption(info.getDataPreloaderClassName());
        preloadersPanel.add(group);
        VerticalPanel panel = new VerticalPanel();
        group.setContainer(panel);

        FlexTable table = new FlexTable();
        int row = 0;
        for (final Map.Entry<String, Serializable> me : info.getParameters().entrySet()) {
            Label label = new Label(me.getKey());
            table.setWidget(row, 0, label);
            label.getElement().getStyle().setPaddingRight(10, Unit.PX);

            TextBox text = new TextBox();
            table.setWidget(row, 1, text);
            text.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    info.getParameters().put(me.getKey(), event.getValue());
                }
            });

            row++;
        }

        if (table.getRowCount() > 0) {
            panel.add(table);
        }

        HorizontalPanel execPanel = new HorizontalPanel();
        panel.add(execPanel);

        Anchor executeDelete = new Anchor("Execute Delete");
        execPanel.add(executeDelete);
        executeDelete.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm("Remove Data", "Some data would be removed\nDo you want to continue?", new Runnable() {
                    @Override
                    public void run() {

                        final AsyncCallback<String> rpcCallback = new BlockingAsyncCallback<String>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                MessageDialog.error("Execute Service failed", caught);
                            }

                            @Override
                            public void onSuccess(String result) {
                                MessageDialog.info("Execute completed", result);
                            }
                        };

                        Vector<DataPreloaderInfo> preloaders = new Vector<DataPreloaderInfo>();
                        preloaders.add(info);
                        RPCManager.execute(DatastoreAdminServices.ExectutePreloadersDelete.class, preloaders, rpcCallback);

                    }
                });

            }
        });

        execPanel.add(new HTML(CommonsStringUtils.NO_BREAK_SPACE_HTML));

        Anchor executeCreate = new Anchor("Execute Create");
        execPanel.add(executeCreate);
        executeCreate.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final AsyncCallback<String> rpcCallback = new BlockingAsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        MessageDialog.error("Execute Service failed", caught);
                    }

                    @Override
                    public void onSuccess(String result) {
                        MessageDialog.info("Execute completed", result);
                    }
                };

                final Vector<DataPreloaderInfo> preloaders = new Vector<DataPreloaderInfo>();
                preloaders.add(info);

                final AsyncCallback<VoidSerializable> rpcPrepareCallback = new BlockingAsyncCallback<VoidSerializable>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        MessageDialog.error("Execute Service failed", caught);

                    }

                    @Override
                    public void onSuccess(VoidSerializable result) {
                        RPCManager.execute(DatastoreAdminServices.ExectutePreloadersCreate.class, preloaders, rpcCallback);

                    }

                };
                RPCManager.execute(DatastoreAdminServices.ExectutePreloadersPrepare.class, preloaders, rpcPrepareCallback);
            }
        });

    }
}

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
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.pyx4j.entity.rpc.DatastoreAdminServices;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.themes.dark.DarkTheme;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkOption;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.style.StyleManger;

public class MainMenu extends MenuBar {

    private static Logger log = LoggerFactory.getLogger(MainMenu.class);

    private static LightTheme lightTheme = new LightTheme();

    private static DarkTheme darkTheme = new DarkTheme();

    public MainMenu(final AdminApplication app) {

        {
            MenuBar fileMenuBar = new MenuBar(true);
            addItem(new MenuItem("File", fileMenuBar));

            fileMenuBar.addItem(new MenuItem("Save", app.getSaveCommand()));
        }

        {
            MenuBar styleMenuBar = new MenuBar(true);
            addItem(new MenuItem("Style", styleMenuBar));

            styleMenuBar.addItem(new MenuItem("Light", new Command() {

                @Override
                public void execute() {
                    StyleManger.installTheme(lightTheme);
                }
            }));
            styleMenuBar.addItem(new MenuItem("Dark", new Command() {
                @Override
                public void execute() {
                    StyleManger.installTheme(darkTheme);
                }
            }));
        }

        {
            MenuBar datastoreMenuBar = new MenuBar(true);
            addItem(new MenuItem("Datastore", datastoreMenuBar));

            datastoreMenuBar.addItem(new MenuItem("Reset Initial Data", new Command() {
                @Override
                public void execute() {
                    final AsyncCallback<String> rpcCallback = new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            MessageDialog.error("CreateInitialData Service failed", caught);
                        }

                        @Override
                        public void onSuccess(String result) {
                            new Dialog("DB Reset completed", result, Type.Info, new Custom1Option() {
                                public boolean onClickCustom1() {
                                    logout();
                                    return true;
                                }

                                public String custom1Text() {
                                    return "Logout";
                                }
                            }).show();
                        }
                    };
                    RPCManager.execute(DatastoreAdminServices.ResetInitialData.class, null, rpcCallback);
                }

                public void logout() {
                    ClientContext.logout(new AsyncCallback<AuthenticationResponse>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            log.error("Logout failure", caught);
                        }

                        @Override
                        public void onSuccess(AuthenticationResponse result) {
                            Window.Location.replace("/");
                        }
                    });
                }

            }));
        }

        {
            MenuBar helpMenuBar = new MenuBar(true);
            addItem(new MenuItem("Help", helpMenuBar));

            helpMenuBar.addItem(new MenuItem("About", new Command() {
                @Override
                public void execute() {
                    Dialog aboutDialog = new Dialog("About", "TODO About", Type.Info, new OkOption() {

                        @Override
                        public boolean onClickOk() {
                            return true;
                        }
                    });
                    aboutDialog.show();
                }
            }));
        }

    }

}
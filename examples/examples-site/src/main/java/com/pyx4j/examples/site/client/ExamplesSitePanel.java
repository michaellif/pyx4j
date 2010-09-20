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

import com.google.gwt.user.client.Command;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.CommandLink;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.SkinFactory;
import com.pyx4j.site.client.themes.crm.CrmTheme;
import com.pyx4j.site.shared.domain.Link;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.style.Theme;

public abstract class ExamplesSitePanel extends SitePanel {

    private final CommandLink logOutLink;

    private final CommandLink logInLink;

    protected ExamplesSitePanel(Site site) {
        super(site, null);

        setSkinFactory(new SkinFactory() {
            @Override
            public Theme createSkin(String skinName) {
                return new CrmTheme();
            }
        });

        {
            boolean hasSeparator = false;
            for (Link link : site.footerLinks()) {
                //TODO
                //addFooterLink(iterator.next(), hasSeparator);
                hasSeparator = true;
            }
        }

        logInLink = new CommandLink("Sign In", new Command() {
            private Dialog dialog;

            @Override
            public void execute() {
                final LogInPanel logInPanel = new LogInPanel() {
                    @Override
                    public void onLogInComplete() {
                        dialog.hide();
                        if (ClientContext.isAuthenticated()) {
                            AbstractSiteDispatcher.show(ExamplesSiteMap.Crm.Customers.class);
                        }
                    }

                };
                logInPanel.setSize("400px", "200px");
                dialog = new Dialog("Sign In", logInPanel);
                dialog.setBody(logInPanel);
                dialog.show();
            }
        });

        logOutLink = new CommandLink("Sign Out", new Command() {
            @Override
            public void execute() {
                AbstractSiteDispatcher.instance().pageLeavingOnLogout(new Runnable() {
                    @Override
                    public void run() {
                        ClientContext.logout(null);
                    }
                });
            }
        });

    }

    protected CommandLink getLogInLink() {
        return logInLink;
    }

    protected CommandLink getLogOutLink() {
        return logOutLink;
    }

}

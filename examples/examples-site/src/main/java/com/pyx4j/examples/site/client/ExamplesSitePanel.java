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
import com.google.gwt.user.client.History;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.CommandLink;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.shared.domain.Link;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.widgets.client.dialog.Dialog;

public abstract class ExamplesSitePanel extends SitePanel {

    private final CommandLink logOutLink;

    private final CommandLink logInLink;

    protected ExamplesSitePanel(Site site) {
        super(site);

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
                        History.newItem("crm&customers");
                    }

                };
                dialog = new Dialog("Sign In", logInPanel);
                dialog.setBody(logInPanel);
            }
        });

        logOutLink = new CommandLink("Sign Out", new Command() {
            @Override
            public void execute() {
                ClientContext.logout(null);
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

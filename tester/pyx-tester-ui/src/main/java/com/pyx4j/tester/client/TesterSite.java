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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.essentials.client.DefaultErrorHandlerDialog;
import com.pyx4j.essentials.client.SessionInactiveDialog;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.tester.client.ui.TesterPanel;
import com.pyx4j.widgets.client.GlassPanel;

public class TesterSite extends AppSite {

    private Message message;

    public TesterSite() {
        super(TesterSiteMap.class);
    }

    @Override
    public void onSiteLoad() {

        ApplicationCommon.initRpcGlassPanel();

        RootPanel.get().add(GlassPanel.instance());

        DefaultErrorHandlerDialog.register();

        getHistoryHandler().register(getPlaceController(), getEventBus(), new TesterSiteMap.Landing());

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(new TesterPanel());

        hideLoadingIndicator();

        SessionInactiveDialog.register();

        TesterSite.getHistoryHandler().handleCurrentHistory();
    }

    public void showMessageDialog(String message, String title, String buttonText, Command command) {
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

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
 * Created on 2010-09-15
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.console;

import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.crud.AbstractEntityEditorPanel;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.rpc.admin.AdminServices;
import com.pyx4j.essentials.rpc.admin.NetworkSimulation;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.widgets.client.GroupBoxPanel;

class SimulationWidget extends VerticalPanel implements InlineWidget {

    private static I18n i18n = I18nFactory.getI18n(SimulationWidget.class);

    private final AbstractEntityEditorPanel<NetworkSimulation> editorPanel;

    SimulationWidget() {
        this.setWidth("100%");

        editorPanel = new AbstractEntityEditorPanel<NetworkSimulation>(NetworkSimulation.class) {
            @Override
            protected IObject<?>[][] getFormMembers() {

                return new IObject[][] {

                { meta().enabled(), },

                { meta().delay(), },

                };
            }

            @Override
            protected Class<? extends EntityServices.Save> getSaveService() {
                return AdminServices.NetworkSimulationSet.class;
            }
        };

        GroupBoxPanel networkGroup = new GroupBoxPanel(false);
        networkGroup.setCaption("Network Simulation");
        this.add(networkGroup);
        networkGroup.add(editorPanel);

        editorPanel.setWidget(editorPanel.createFormWidget(LabelAlignment.LEFT));

        Button saveButton = new Button(i18n.tr("Save"));
        saveButton.getElement().getStyle().setProperty("margin", "20px");

        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                editorPanel.doSave();
            }
        });

        networkGroup.add(saveButton);
    }

    @Override
    public void populate(Map<String, String> args) {
        AsyncCallback cb = new AsyncCallback<NetworkSimulation>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(NetworkSimulation result) {
                editorPanel.populateForm(result);
            }
        };
        RPCManager.execute(AdminServices.NetworkSimulationRetrieve.class, null, cb);
    }

}

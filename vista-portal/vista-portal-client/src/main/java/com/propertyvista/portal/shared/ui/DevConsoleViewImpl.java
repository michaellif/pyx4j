/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author vlads
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.devconsole.AbstractDevConsole;

import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.rpc.DevConsoleDataTO;

public class DevConsoleViewImpl extends AbstractDevConsole implements DevConsoleView {

    protected final CForm<DevConsoleDataTO> form;

    private class DevConsoleForm extends CForm<DevConsoleDataTO> {

        DevConsoleForm() {
            super(DevConsoleDataTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel content = new FormPanel(this);
            content.append(Location.Left, proto().crmUrl()).decorate().componentWidth(450).labelWidth(80);
            content.append(Location.Left, proto().residentUrl()).decorate().componentWidth(450).labelWidth(80);
            content.append(Location.Left, proto().prospectUrl()).decorate().componentWidth(450).labelWidth(80);
            content.append(Location.Left, proto().siteUrl()).decorate().componentWidth(450).labelWidth(80);

            ((CField<?, ?>) get(proto().crmUrl())).setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(getValue().crmUrl().getValue(), "_self", null);
                }
            });

            ((CField<?, ?>) get(proto().residentUrl())).setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(getValue().residentUrl().getValue(), "_self", null);
                }
            });

            ((CField<?, ?>) get(proto().prospectUrl())).setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(getValue().prospectUrl().getValue(), "_self", null);
                }
            });

            ((CField<?, ?>) get(proto().siteUrl())).setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(getValue().siteUrl().getValue(), "_self", null);
                }
            });

            return content;
        }
    }

    public DevConsoleViewImpl() {
        add(new SetMocksButton());
        form = new DevConsoleForm();
        form.init();
        form.setViewable(true);
        add(form);
    }

    @Override
    protected void setMockValues() {
        setMockValues(((PortalRootPane) AppSite.instance().getRootPane()).asWidget());
    }

    @Override
    public void setData(DevConsoleDataTO data) {
        form.populate(data);
        form.get(form.proto().residentUrl()).setVisible(VistaSite.instance().getApplication() == VistaApplication.prospect);
        form.get(form.proto().prospectUrl()).setVisible(VistaSite.instance().getApplication() == VistaApplication.resident);
    }
}

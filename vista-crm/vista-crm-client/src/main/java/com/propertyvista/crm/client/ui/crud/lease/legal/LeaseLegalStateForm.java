/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.activity.crud.lease.legal.LeaseLegalStateController;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.dto.LeaseLegalStateDTO;

public class LeaseLegalStateForm extends CForm<LeaseLegalStateDTO> {

    private static final I18n i18n = I18n.get(LeaseLegalStateForm.class);

    private final LeaseLegalStateController controller;

    public LeaseLegalStateForm(LeaseLegalStateController controller) {
        super(LeaseLegalStateDTO.class);
        setViewable(true);
        this.controller = controller;
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.h1(i18n.tr("Current Status"));
        formPanel.append(Location.Full, proto().current(), new LegalStatusForm(false));
        formPanel.append(Location.Full, createCommandBar());
        formPanel.h1(i18n.tr("History"));
        formPanel.append(Location.Full, proto().historical(), new LegalStatusHistoryFolder() {
            @Override
            protected void onRemoved(LegalStatus item) {
                LeaseLegalStateForm.this.deleteStatus(item);
            }
        });
        return formPanel;
    }

    private IsWidget createCommandBar() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("100%");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        panel.add(new Button(i18n.tr("Update..."), new Command() {
            @Override
            public void execute() {
                LeaseLegalStateForm.this.controller.updateStatus();
            }
        }));
        panel.add(new Button(i18n.tr("Clear"), new Command() {
            @Override
            public void execute() {
                LeaseLegalStateForm.this.controller.clearStatus();
            }
        }));
        return panel;
    }

    private void deleteStatus(LegalStatus status) {
        LeaseLegalStateForm.this.controller.deleteStatus(EntityFactory.createIdentityStub(LegalStatus.class, status.getPrimaryKey()));
    }
}

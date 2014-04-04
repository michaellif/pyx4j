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
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.activity.crud.lease.legal.LeaseLegalStateController;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.dto.LeaseLegalStateDTO;

public class LeaseLegalStateForm extends CEntityForm<LeaseLegalStateDTO> {

    private static final I18n i18n = I18n.get(LeaseLegalStateForm.class);

    private Button update;

    private final LeaseLegalStateController controller;

    private Button clear;

    private LegalStatusHistoryFolder historyFolder;

    public LeaseLegalStateForm(LeaseLegalStateController controller) {
        super(LeaseLegalStateDTO.class);
        setViewable(true);
        this.controller = controller;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 2, i18n.tr("Current Status"));
        panel.setWidget(++row, 0, 2, inject(proto().current(), new LegalStatusForm(false)));
        panel.setWidget(++row, 0, 2, createCommandBar());
        panel.setH1(++row, 0, 2, i18n.tr("History"));
        panel.setWidget(++row, 0, 2, inject(proto().historical(), historyFolder = new LegalStatusHistoryFolder() {
            @Override
            protected void onRemoved(LegalStatus item) {
                LeaseLegalStateForm.this.deleteStatus(item);
            }
        }).asWidget());
        return panel;
    }

    private IsWidget createCommandBar() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("100%");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        panel.add(update = new Button(i18n.tr("Update..."), new Command() {
            @Override
            public void execute() {
                LeaseLegalStateForm.this.controller.updateStatus();
            }
        }));
        panel.add(clear = new Button(i18n.tr("Clear"), new Command() {
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

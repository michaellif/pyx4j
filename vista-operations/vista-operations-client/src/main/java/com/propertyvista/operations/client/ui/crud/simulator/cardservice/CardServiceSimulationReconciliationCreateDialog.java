/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author vlads
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.common.client.ui.MiscUtils;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCreateTO;

public class CardServiceSimulationReconciliationCreateDialog extends OkCancelDialog implements OkOptionText {

    private final CardServiceSimulationReconciliationListerView.Presenter presenter;

    private final CForm<CardServiceSimulationReconciliationCreateTO> form;

    public CardServiceSimulationReconciliationCreateDialog(CardServiceSimulationReconciliationListerView.Presenter presenter) {
        super("Create Simulated Reconciliation Report");
        this.presenter = presenter;

        form = new CForm<CardServiceSimulationReconciliationCreateTO>(CardServiceSimulationReconciliationCreateTO.class) {

            @Override
            protected IsWidget createContent() {
                FormPanel content = new FormPanel(this);
                content.append(Location.Left, proto().company()).decorate();
                content.append(Location.Left, proto().fromDate()).decorate();
                content.append(Location.Right, proto().toDate()).decorate();

                get(proto().fromDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                        if (getValue().toDate().getValue().before(event.getValue())) {
                            get(proto().toDate()).populate(event.getValue());
                        }
                    }
                });

                return content;
            }
        };

        form.init();

        CardServiceSimulationReconciliationCreateTO sendTO = EntityFactory.create(CardServiceSimulationReconciliationCreateTO.class);
        sendTO.fromDate().setValue(new LogicalDate(ClientContext.getServerDate()));
        sendTO.toDate().setValue(new LogicalDate(ClientContext.getServerDate()));
        form.populate(sendTO);

        setBody(createBody());
    }

    private IsWidget createBody() {
        VerticalPanel body = new VerticalPanel();

        body.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        body.add(new HTML("Select Report interval"));
        body.add(form);

        MiscUtils.setPanelSpacing(body, 4);
        body.setWidth("100%");
        return body;
    }

    @Override
    public String optionTextOk() {
        return "Create";
    }

    @Override
    public boolean onClickOk() {
        presenter.createReconciliations(form.getValue());
        return true;
    }

}

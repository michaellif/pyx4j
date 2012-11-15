/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureStatusForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureLogo;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureManagementViewImpl extends Composite implements TenantSureManagementView {

    private static final I18n i18n = I18n.get(TenantSureManagementViewImpl.class);

    private Presenter presenter;

    private final TenantSureStatusForm statusForm;

    public TenantSureManagementViewImpl() {

        FlowPanel viewPanel = new FlowPanel();

        FlowPanel tenantSureGreetingPanel = new FlowPanel();
        tenantSureGreetingPanel.getElement().getStyle().setProperty("width", "100%");
        tenantSureGreetingPanel.getElement().getStyle().setProperty("height", "8em");
        tenantSureGreetingPanel.getElement().getStyle().setProperty("display", "table-cell");
        tenantSureGreetingPanel.getElement().getStyle().setProperty("verticalAlign", "middle");

        TenantSureLogo tenantSureLogo = new TenantSureLogo();
        tenantSureLogo.getElement().getStyle().setFloat(Float.LEFT);
        tenantSureLogo.getElement().getStyle().setProperty("width", "10em");
        tenantSureLogo.getElement().getStyle().setProperty("height", "8em");
        tenantSureGreetingPanel.add(tenantSureLogo);

        Label greeting = new Label(//@formatter:off
                  "TenantSure is a Licensed Broker. Below please find your TenantSure insurance details. "
                  + "If you have any claims, you can reach TenanSure's claim department at 1-888-1234-444"
          );//@formatter:on

        greeting.getElement().getStyle().setProperty("width", "100%");
        greeting.getElement().getStyle().setProperty("height", "8em");
        tenantSureGreetingPanel.add(greeting);

        viewPanel.add(tenantSureGreetingPanel);

        FlowPanel statusPanel = new FlowPanel();

        statusForm = new TenantSureStatusForm();
        statusForm.initContent();
        statusPanel.add(statusForm);

        viewPanel.add(statusPanel);

        FlowPanel controlPanel = new FlowPanel();
        controlPanel.getElement().getStyle().setMarginTop(50, Unit.PX);
        Button updateCC = new Button("Update Credit Card Details", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.updateCreditCardDetails();
            }
        });
        setControlButtonLayout(updateCC);
        Button cancelTenantSure = new Button("Cancel TenantSure", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("TenantSure cancellation"), i18n.tr("Are you sure you want to cancel TenantSure?"), new Command() {
                    @Override
                    public void execute() {
                        presenter.cancelTenantSure();
                    }
                });

            }
        });
        setControlButtonLayout(cancelTenantSure);

        controlPanel.add(updateCC);
        controlPanel.add(cancelTenantSure);

        viewPanel.add(controlPanel);

        initWidget(viewPanel);
    }

    private static void setControlButtonLayout(Button button) {
        button.getElement().getStyle().setProperty("display", "block");
        button.getElement().getStyle().setProperty("textAlign", "center");
        button.getElement().getStyle().setProperty("width", "15em");
        button.getElement().getStyle().setProperty("marginLeft", "auto");
        button.getElement().getStyle().setProperty("marginRight", "auto");

        button.getElement().getStyle().setProperty("marginTop", "10px");
        button.getElement().getStyle().setProperty("marginBottom", "10px");

    }

    @Override
    public void populate(TenantSureTenantInsuranceStatusDetailedDTO detailedStatus) {
        statusForm.populate(detailedStatus);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}

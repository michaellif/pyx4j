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
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureLogo;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureStatusForm;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureManagementViewImpl extends Composite implements TenantSureManagementView {

    private static final I18n i18n = I18n.get(TenantSureManagementViewImpl.class);

    private Presenter presenter;

    private TenantSureStatusForm statusForm;

    private Button cancelTenantSureButton;

    private Button updateCCButton;

    private Button updateCCAndPay;

    public TenantSureManagementViewImpl() {

        FlowPanel viewPanel = new FlowPanel();

        viewPanel.add(makeGreetingPanel());
        viewPanel.add(makeStatusDetailsPanel());
        viewPanel.add(makeActionsPanel());

        initWidget(viewPanel);
    }

    @Override
    public void populate(TenantSureTenantInsuranceStatusDetailedDTO detailedStatus) {
        statusForm.populate(detailedStatus);
        boolean isCancelled = !detailedStatus.expiryDate().isNull();
        updateCCButton.setEnabled(!isCancelled);
        cancelTenantSureButton.setEnabled(!isCancelled);

        setControlButtonLayout(updateCCButton, !detailedStatus.isPaymentFailed().isBooleanTrue()); // because setVisible for some reason screws up other style settings        
        setControlButtonLayout(updateCCAndPay, detailedStatus.isPaymentFailed().isBooleanTrue()); // because setVisible for some reason screws up other style settings
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void reportUpdateCreditCardUpdate(String errorMessage) {
        MessageDialog.error(i18n.tr("Error"), errorMessage);
    }

    @Override
    public void reportCancelFailure(String errorMessage) {
        MessageDialog.error(i18n.tr("Error"), errorMessage);
    }

    private Widget makeGreetingPanel() {
        FlowPanel tenantSureGreetingPanel = new FlowPanel();
        final double HEIGHT = 8;
        tenantSureGreetingPanel.getElement().getStyle().setHeight(HEIGHT, Unit.EM);
        tenantSureGreetingPanel.getElement().getStyle().setPadding(20, Unit.PX);

        TenantSureLogo tenantSureLogo = new TenantSureLogo();
        tenantSureLogo.getElement().getStyle().setFloat(Float.LEFT);
        tenantSureLogo.getElement().getStyle().setWidth(10, Unit.EM);
        tenantSureLogo.getElement().getStyle().setHeight(HEIGHT, Unit.EM);
        tenantSureGreetingPanel.add(tenantSureLogo);

        Label greeting = new Label(//@formatter:off
                i18n.tr("TenantSure is a Licensed Broker. Below please find your TenantSure insurance details. If you have any claims, you can reach TenantSure''s claim department at {0}.",
                 TenantSureConstants.TENANTSURE_PHONE_NUMBER)
          );//@formatter:on

        greeting.getElement().getStyle().setWidth(100, Unit.PCT);
        greeting.getElement().getStyle().setHeight(HEIGHT, Unit.EM);
        greeting.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        greeting.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        greeting.getElement().getStyle().setProperty("display", "table-cell");
        tenantSureGreetingPanel.add(greeting);
        return tenantSureGreetingPanel;
    }

    private Widget makeStatusDetailsPanel() {
        FlowPanel statusPanel = new FlowPanel();
        statusPanel.getElement().getStyle().setPadding(10, Unit.PX);
        statusForm = new TenantSureStatusForm();
        statusForm.initContent();
        statusPanel.add(statusForm);
        return statusPanel;
    }

    private Widget makeActionsPanel() {
        FlowPanel actionsPanel = new FlowPanel();
        actionsPanel.getElement().getStyle().setMarginTop(50, Unit.PX);
        actionsPanel.getElement().getStyle().setMarginBottom(50, Unit.PX);

        updateCCButton = new Button(i18n.tr("Update Credit Card Details"), new Command() {
            @Override
            public void execute() {
                presenter.updateCreditCardDetails();
            }
        });
        setControlButtonLayout(updateCCButton);

        updateCCAndPay = new Button(i18n.tr("Update Credid Card and Pay"), new Command() {

            @Override
            public void execute() {
                presenter.updateCreditCardDetails();
            }
        });
        setControlButtonLayout(updateCCAndPay);

        cancelTenantSureButton = new Button(i18n.tr("Cancel TenantSure"), new Command() {
            @Override
            public void execute() {
                TenantSureManagementViewImpl.this.onCancelTenantSure();
            }
        });
        setControlButtonLayout(cancelTenantSureButton);

        Button viewCertificateButton = new Button(i18n.tr("View Insurance Certificate"));
        setControlButtonLayout(viewCertificateButton);

        Button viewFaq = new Button(i18n.tr("FAQ"), new Command() {
            @Override
            public void execute() {
                presenter.viewFaq();
            }
        });
        setControlButtonLayout(viewFaq);

        Button aboutTenantSure = new Button(i18n.tr("About TenantSure / Contact Us"), new Command() {
            @Override
            public void execute() {
                presenter.viewAboutTenantSure();
            }
        });
        setControlButtonLayout(aboutTenantSure);

        Button makeAClaim = new Button(i18n.tr("Make a Claim"), new Command() {
            @Override
            public void execute() {
                TenantSureManagementViewImpl.this.onMakeAClaim();
            }
        });
        setControlButtonLayout(makeAClaim);

//        actionsPanel.add(viewCertificateButton);
        actionsPanel.add(makeAClaim);
        actionsPanel.add(updateCCButton);
        actionsPanel.add(updateCCAndPay);
        actionsPanel.add(cancelTenantSureButton);

        actionsPanel.add(new HTML("&nbsp;")); // add separator 

        // informations
        actionsPanel.add(viewFaq);
        actionsPanel.add(aboutTenantSure);

        return actionsPanel;
    }

    private void onCancelTenantSure() {
        MessageDialog.confirm(i18n.tr("TenantSure cancellation"), i18n.tr("Are you sure you want to cancel TenantSure?"), new Command() {
            @Override
            public void execute() {
                presenter.cancelTenantSure();
            }
        });
    }

    private void onMakeAClaim() {
        MessageDialog.info(i18n.tr("To make a claim please call {0} at {1}", TenantSureConstants.TENANTSURE_LEGAL_NAME,
                TenantSureConstants.TENANTSURE_PHONE_NUMBER));
    }

    private static void setControlButtonLayout(Button button, boolean isVisible) {
        button.getElement().getStyle().setProperty("display", isVisible ? "block" : "none");
        button.getElement().getStyle().setProperty("textAlign", "center");
        button.getElement().getStyle().setProperty("width", "15em");
        button.getElement().getStyle().setProperty("marginLeft", "auto");
        button.getElement().getStyle().setProperty("marginRight", "auto");

        button.getElement().getStyle().setProperty("marginTop", "10px");
        button.getElement().getStyle().setProperty("marginBottom", "10px");
    }

    private static void setControlButtonLayout(Button button) {
        setControlButtonLayout(button, true);
    }

}

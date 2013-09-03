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
package com.propertyvista.portal.client.ui.residents.tenantinsurance.views;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.OtherProviderTenantInsuranceStatusViewer;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureLogo;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.NoInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.OtherProviderInsuranceStatusDTO;

public class ProvideTenantInsuranceViewImpl extends Composite implements ProvideTenantInsuranceView {

    public enum Styles implements IStyleName {
        ProvideTIRequirements, ProvideTIInsuranceStatus, ProvideTIBGetTenantSure, ProvideTIUpdateExisitingInsurance, ProvideTITenantSureLogo;
    }

    private static class TenantSureInvitationPanel extends Composite {

        public TenantSureInvitationPanel(final Command acceptInvitation) {
            HorizontalPanel getTenantSurePanel = new HorizontalPanel();
            getTenantSurePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            getTenantSurePanel.getElement().getStyle().setProperty("marginLeft", "auto");
            getTenantSurePanel.getElement().getStyle().setProperty("marginRight", "auto");

            TenantSureLogo tenantSureLogo = new TenantSureLogo();
            tenantSureLogo.addStyleName(Styles.ProvideTITenantSureLogo.name());
            tenantSureLogo.addHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    acceptInvitation.execute();
                }
            }, ClickEvent.getType());
            tenantSureLogo.getElement().getStyle().setCursor(Cursor.POINTER);
            getTenantSurePanel.add(tenantSureLogo);

            Button getTenantSureButton = new Button(i18n.tr("Get TenantSure"), acceptInvitation);
            getTenantSureButton.addStyleName(Styles.ProvideTIBGetTenantSure.name());
            getTenantSurePanel.add(getTenantSureButton);

            initWidget(getTenantSurePanel);
        }

    }

    private static final I18n i18n = I18n.get(ProvideTenantInsuranceViewImpl.class);

    private Presenter presenter;

    private final Label tenantInsuranceRequirementsMessage;

    private final TenantSureInvitationPanel tenantSureInvitationPanel;

    private final OtherProviderTenantInsuranceStatusViewer insuranceStatusViewer;

    private final Anchor provideInsuranceByOtherProvider;

    public ProvideTenantInsuranceViewImpl() {
        FlowPanel viewPanel = new FlowPanel();

        tenantInsuranceRequirementsMessage = new Label();
        tenantInsuranceRequirementsMessage.setStyleName(Styles.ProvideTIRequirements.name());
        viewPanel.add(tenantInsuranceRequirementsMessage);

        insuranceStatusViewer = new OtherProviderTenantInsuranceStatusViewer();
        insuranceStatusViewer.asWidget().setStyleName(Styles.ProvideTIInsuranceStatus.name());
        viewPanel.add(insuranceStatusViewer);

        viewPanel.add(tenantSureInvitationPanel = new TenantSureInvitationPanel(new Command() {
            @Override
            public void execute() {
                presenter.onPurchaseTenantSure();
            }
        }));

        provideInsuranceByOtherProvider = new Anchor("", new Command() {
            @Override
            public void execute() {
                presenter.onUpdateInsuranceByOtherProvider();
            }
        });
        provideInsuranceByOtherProvider.addStyleName(Styles.ProvideTIUpdateExisitingInsurance.name());
        viewPanel.add(provideInsuranceByOtherProvider);

        initWidget(viewPanel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTenantSureInvitationEnabled(boolean tenantSureInvitationEnabled) {
        tenantSureInvitationPanel.setVisible(tenantSureInvitationEnabled);
    }

    @Override
    public void populate(InsuranceStatusDTO insuranceStatus) {
        tenantInsuranceRequirementsMessage.setVisible(false);
        tenantInsuranceRequirementsMessage.setHTML("");

        insuranceStatusViewer.setVisible(false);

        if (insuranceStatus != null) {
            if (insuranceStatus.isInstanceOf(NoInsuranceStatusDTO.class)) {
                NoInsuranceStatusDTO insuranceStatusNoInsurance = insuranceStatus.duplicate(NoInsuranceStatusDTO.class);
                tenantInsuranceRequirementsMessage.setVisible(true);
                tenantInsuranceRequirementsMessage.setHTML(insuranceStatusNoInsurance.tenantInsuranceInvitation().getValue());
                provideInsuranceByOtherProvider.setText(i18n.tr("I (we) already have Tenant Insurance"));
            } else if (insuranceStatus.isInstanceOf(OtherProviderInsuranceStatusDTO.class)) {
                insuranceStatusViewer.setVisible(true);
                insuranceStatusViewer.populate(insuranceStatus.duplicate(OtherProviderInsuranceStatusDTO.class));
                provideInsuranceByOtherProvider.setText(i18n.tr("Update Insurance"));
            } else if (insuranceStatus.isInstanceOf(TenantSureTenantInsuranceStatusDTO.class)) {
                assert false : "this place shouldn't be used when tenantsure is active";
            } else {
                assert false : "unknown insurance status: " + insuranceStatus.getInstanceValueClass().getName();
            }
        }
    }

}

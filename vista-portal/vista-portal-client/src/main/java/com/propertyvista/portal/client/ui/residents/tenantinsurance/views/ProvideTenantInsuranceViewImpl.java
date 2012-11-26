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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureLogo;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;

public class ProvideTenantInsuranceViewImpl extends Composite implements ProvideTenantInsuranceView {

    public enum Styles implements IStyleName {
        ProvideTIRequirements, ProvideTIBGetTenantSure, ProvideTIUpdateExisitingInsurance, ProvideTITenantSureLogo;
    }

    private static final I18n i18n = I18n.get(ProvideTenantInsuranceViewImpl.class);

    private Presenter presenter;

    private final Label tenantInsuranceRequirementsMessage;

    private final TenantSureLogo tenantSureLogo;

    public ProvideTenantInsuranceViewImpl() {
        FlowPanel viewPanel = new FlowPanel();

        tenantInsuranceRequirementsMessage = new Label();
        tenantInsuranceRequirementsMessage.setStyleName(Styles.ProvideTIRequirements.name());
        viewPanel.add(tenantInsuranceRequirementsMessage);

        HorizontalPanel getTenantSurePanel = new HorizontalPanel();
        getTenantSurePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        getTenantSurePanel.getElement().getStyle().setProperty("marginLeft", "auto");
        getTenantSurePanel.getElement().getStyle().setProperty("marginRight", "auto");

        tenantSureLogo = new TenantSureLogo();
        tenantSureLogo.addStyleName(Styles.ProvideTITenantSureLogo.name());
        getTenantSurePanel.add(tenantSureLogo);

        Button getTenantSureButton = new Button(i18n.tr("Get TenantSure"));
        getTenantSureButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onPurchaseTenantSure();
            }
        });
        getTenantSureButton.addStyleName(Styles.ProvideTIBGetTenantSure.name());
        getTenantSurePanel.add(getTenantSureButton);
        viewPanel.add(getTenantSurePanel);

        Anchor provideInsuranceByOtherProvider = new Anchor(i18n.tr("I (we) already have Tenant Insurance"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
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
    public void populate(NoInsuranceTenantInsuranceStatusDTO noInsuranceStatus) {
        boolean noInsurance = noInsuranceStatus != null && !noInsuranceStatus.isNull();

        tenantInsuranceRequirementsMessage.setVisible(noInsurance);
        tenantInsuranceRequirementsMessage.setVisible(noInsurance);
        tenantInsuranceRequirementsMessage.setHTML(noInsuranceStatus.tenantInsuranceInvitation().getValue());
    }

}

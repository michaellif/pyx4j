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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;

public class ProvideTenantInsuranceViewImpl extends Composite implements ProvideTenantInsuranceView {

    private static final I18n i18n = I18n.get(ProvideTenantInsuranceViewImpl.class);

    private Presenter presenter;

    private final Label tenantInsuranceRequirementsMessage;

    private final Label noTenantInsuranceWarningMessage;

    public ProvideTenantInsuranceViewImpl() {
        FlowPanel viewPanel = new FlowPanel();

        noTenantInsuranceWarningMessage = new Label();
        noTenantInsuranceWarningMessage.setText(i18n.tr("According to our records you do not have Valid Tenant Insurance!"));
        viewPanel.add(noTenantInsuranceWarningMessage);

        tenantInsuranceRequirementsMessage = new Label();
        viewPanel.add(tenantInsuranceRequirementsMessage);

        Button getTenantSureButton = new Button(i18n.tr("Get TenantSure Insurance"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onPurchaseTenantSure();
            }
        });
        viewPanel.add(getTenantSureButton);

        Anchor provideInsuranceByOtherProvider = new Anchor(i18n.tr("I (we) already have Tenant Insurance"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.onUpdateInsuranceByOtherProvider();
            }
        });
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

        noTenantInsuranceWarningMessage.setVisible(noInsurance);
        tenantInsuranceRequirementsMessage.setVisible(noInsurance);

        tenantInsuranceRequirementsMessage
                .setText(noInsurance ? i18n
                        .tr("As per you Lease Agreement, you must obtain and provide the Landlord with Proof of Tenant Insurance with a minimum Personal Liability of ${0}. We have teamed up with TenantSure, a Licensed Broker, to assist you in obtaining your Tenant Insurance.",
                                noInsuranceStatus.minimumRequiredLiability())
                        : "");
    }

}

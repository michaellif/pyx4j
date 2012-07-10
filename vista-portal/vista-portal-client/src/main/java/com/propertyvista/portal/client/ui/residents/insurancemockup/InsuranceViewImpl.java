/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.insurancemockup;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.moveinwizardmockup.InsuranceDTO;
import com.propertyvista.domain.moveinwizardmockup.TenantInsuranceDTO;
import com.propertyvista.domain.moveinwizardmockup.TenantInsuranceDTO.InsuranceStatus;
import com.propertyvista.portal.client.ui.residents.insurancemockup.components.InsuranceMessagePanel;
import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.InsuranceAlreadyAvailabileForm;
import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.TenantSureInsuranceForm;
import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.UnknownInsuranceForm;
import com.propertyvista.portal.client.ui.residents.insurancemockup.resources.InsuranceMockupResources;

public class InsuranceViewImpl implements InsuranceView {

    private final CEntityForm<InsuranceDTO> unknownInsuranceForm;

    private final InsuranceAlreadyAvailabileForm independantInsuranceForm;

    private final FormFlexPanel independantInsurancePanel;

    private final TenantSureInsuranceForm tenantSureInsuranceForm;

    private final FormFlexPanel viewPanel;

    private Presenter presenter;

    public InsuranceViewImpl() {

        unknownInsuranceForm = new UnknownInsuranceForm();
        unknownInsuranceForm.initContent();
        unknownInsuranceForm.setVisible(false);

        independantInsuranceForm = new InsuranceAlreadyAvailabileForm();
        independantInsuranceForm.initContent();
        independantInsuranceForm.setVisible(false);
        independantInsuranceForm.setViewable(true);
        independantInsurancePanel = new FormFlexPanel();

        independantInsurancePanel.setWidget(0, 0,
                new InsuranceMessagePanel(new HTML(InsuranceMockupResources.INSTANCE.independantInsuranceMessage().getText())));
        independantInsurancePanel.setWidget(1, 0, independantInsuranceForm);

        tenantSureInsuranceForm = new TenantSureInsuranceForm();
        tenantSureInsuranceForm.asWidget().setVisible(false);

        viewPanel = new FormFlexPanel();
        viewPanel.setWidget(0, 0, unknownInsuranceForm);
        viewPanel.setWidget(1, 0, independantInsurancePanel);
        viewPanel.setWidget(2, 0, tenantSureInsuranceForm);

    }

    @Override
    public Widget asWidget() {
        return viewPanel;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(final TenantInsuranceDTO tenantInsuranceDTO) {
        unknownInsuranceForm.setVisible(tenantInsuranceDTO.status().getValue() == InsuranceStatus.unknown);

        independantInsuranceForm.setVisible(tenantInsuranceDTO.status().getValue() == InsuranceStatus.independant);
        independantInsurancePanel.setVisible(tenantInsuranceDTO.status().getValue() == InsuranceStatus.independant);

        tenantSureInsuranceForm.asWidget().setVisible(tenantInsuranceDTO.status().getValue() == InsuranceStatus.tenantSure);

        switch (tenantInsuranceDTO.status().getValue()) {
        case unknown:
            unknownInsuranceForm.populate(tenantInsuranceDTO.newInsuranceRequest());
            break;
        case independant:
            independantInsuranceForm.populate(tenantInsuranceDTO.independant());
        case tenantSure:

        }

    }

}

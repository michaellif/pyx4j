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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.InsuranceForm;
import com.propertyvista.portal.rpc.portal.dto.insurancemockup.TenantInsuranceDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;

public class InsuranceViewImpl implements InsuranceView {

    private final CEntityForm<InsuranceDTO> noInsuranceForm;

    private final FormFlexPanel viewPanel;

    private Presenter presenter;

    public InsuranceViewImpl() {

        noInsuranceForm = new InsuranceForm();
        noInsuranceForm.initContent();
        viewPanel = new FormFlexPanel();
        viewPanel.setWidget(0, 0, noInsuranceForm);

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
    public void populate(TenantInsuranceDTO tenantInsuranceDTO) {
        switch (tenantInsuranceDTO.status().getValue()) {
        case unknown:
            noInsuranceForm.populate(tenantInsuranceDTO.newRequestInsurance().duplicate(InsuranceDTO.class));
            noInsuranceForm.asWidget().setVisible(true);
            break;
        }
    }

}

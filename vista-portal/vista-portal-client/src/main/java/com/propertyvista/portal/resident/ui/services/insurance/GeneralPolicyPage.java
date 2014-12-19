/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class GeneralPolicyPage extends CPortalEntityForm<GeneralInsurancePolicyDTO> {

    private final static I18n i18n = I18n.get(GeneralPolicyPage.class);

    public GeneralPolicyPage(GeneralPolicyPageView view) {
        super(GeneralInsurancePolicyDTO.class, view, i18n.tr("Insurance Certificate"), ThemeColor.contrast3);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().certificate().insuranceProvider()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().certificate().insuranceCertificateNumber()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().certificate().liabilityCoverage()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().certificate().inceptionDate()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().certificate().expiryDate()).decorate().componentWidth(150);

        formPanel.h1("Scanned Insurance Certificate Documents");
        formPanel.append(Location.Left, proto().certificate().certificateDocs(), new CertificateScanFolder());

        return formPanel;

    }

    @Override
    protected FormDecorator<GeneralInsurancePolicyDTO> createDecorator() {
        FormDecorator<GeneralInsurancePolicyDTO> decorator = super.createDecorator();

        Button btnEdit = new Button(i18n.tr("Remove"), new Command() {
            @Override
            public void execute() {
                onRemove();
            }
        });
        decorator.addHeaderToolbarWidget(btnEdit);

        return decorator;
    }

    protected void onRemove() {

    }

}
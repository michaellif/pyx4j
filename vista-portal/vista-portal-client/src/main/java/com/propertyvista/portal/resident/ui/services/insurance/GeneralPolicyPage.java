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
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class GeneralPolicyPage extends CPortalEntityForm<GeneralInsurancePolicyDTO> {

    private final static I18n i18n = I18n.get(GeneralPolicyPage.class);

    public GeneralPolicyPage(GeneralPolicyPageView view) {
        super(GeneralInsurancePolicyDTO.class, view, i18n.tr("Insurance Certificate"), ThemeColor.contrast3);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setWidget(++row, 0, 1, inject(proto().certificate().insuranceProvider(), new FieldDecoratorBuilder(150).build()));
        mainPanel.setWidget(++row, 0, 1, inject(proto().certificate().insuranceCertificateNumber(), new FieldDecoratorBuilder(150).build()));
        mainPanel.setWidget(++row, 0, 1, inject(proto().certificate().liabilityCoverage(), new FieldDecoratorBuilder(150).build()));
        mainPanel.setWidget(++row, 0, 1, inject(proto().certificate().inceptionDate(), new FieldDecoratorBuilder(150).build()));
        mainPanel.setWidget(++row, 0, 1, inject(proto().certificate().expiryDate(), new FieldDecoratorBuilder(150).build()));

        mainPanel.setH1(++row, 0, 1, "Scanned Insurance Certificate Documents");
        mainPanel.setWidget(++row, 0, 1, inject(proto().certificate().certificateDocs(), new CertificateScanFolder()));

        return mainPanel;

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
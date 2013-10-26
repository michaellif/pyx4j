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
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsurancePolicyUploadService;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;

public class GeneralPolicyPage extends CPortalEntityForm<GeneralInsurancePolicyDTO> {

    private final static I18n i18n = I18n.get(GeneralPolicyPage.class);

    public GeneralPolicyPage(GeneralPolicyPageView view) {
        super(GeneralInsurancePolicyDTO.class, view, i18n.tr("Insurance Certificate"), ThemeColor.contrast3);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceProvider()), 150).build());
        mainPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceCertificateNumber()), 150).build());
        mainPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().certificate().liabilityCoverage()), 150).build());
        mainPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().certificate().inceptionDate()), 150).build());
        mainPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().certificate().expiryDate()), 150).build());
        mainPanel.setWidget(
                ++row,
                0,
                1,
                new FormWidgetDecoratorBuilder(inject(proto().certificate().certificateScan(),
                        new CFile<InsuranceCertificateScan>(GWT.<GeneralInsurancePolicyUploadService> create(GeneralInsurancePolicyUploadService.class),
                                new FileURLBuilder<InsuranceCertificateScan>() {

                                    @Override
                                    public String getUrl(InsuranceCertificateScan file) {
                                        return MediaUtils.createInsuranceCertificateScanUrl(file);
                                    }
                                })), 200).build());

        return mainPanel;

    }

    @Override
    protected FormDecorator<GeneralInsurancePolicyDTO, CEntityForm<GeneralInsurancePolicyDTO>> createDecorator() {
        FormDecorator<GeneralInsurancePolicyDTO, CEntityForm<GeneralInsurancePolicyDTO>> decorator = super.createDecorator();

        Button btnEdit = new Button(i18n.tr("Remove"), new Command() {
            @Override
            public void execute() {
                onRemove();
            }
        });
        decorator.addHeaderToolbarButton(btnEdit);

        return decorator;
    }

    protected void onRemove() {

    }

}
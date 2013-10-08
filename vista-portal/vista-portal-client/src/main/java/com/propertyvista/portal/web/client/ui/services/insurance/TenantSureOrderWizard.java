/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class TenantSureOrderWizard extends CPortalEntityWizard<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderWizard.class);

    private final WizardStep personalInfoStep;

    private final WizardStep insuranceCoverageStep;

    private final WizardStep paymentMethodStep;

    private final WizardStep confirmationStep;

    public TenantSureOrderWizard(TenantSureOrderWizardView view, String endButtonCaption) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("TenantSure Insurance"), endButtonCaption, ThemeColor.contrast3);

        personalInfoStep = addStep(createPersonalInfoStep());

        insuranceCoverageStep = addStep(createInsuranceCoverageStep());

        paymentMethodStep = addStep(createPaymentMethodStep());

        confirmationStep = addStep(createConfirmationStep());

    }

    private BasicFlexFormPanel createPersonalInfoStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal Disclaimer Terms"));
        panel.setWidget(++row, 0, new HTML("TODO - Personal disclaimer goes here"));

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal & Contact Information"));

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantSureCoverageRequest().tenantName())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantSureCoverageRequest().tenantPhone())).build());

        return panel;
    }

    private BasicFlexFormPanel createInsuranceCoverageStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        return panel;
    }

    private BasicFlexFormPanel createPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        return panel;
    }

    private BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        return panel;
    }
}

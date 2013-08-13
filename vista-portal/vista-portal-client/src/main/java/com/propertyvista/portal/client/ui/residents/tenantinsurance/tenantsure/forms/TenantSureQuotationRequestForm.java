/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.common.client.ui.components.tenantinsurance.YesNoComboBox;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;

public class TenantSureQuotationRequestForm extends CEntityDecoratableForm<TenantSureCoverageDTO> {

    private static final I18n i18n = I18n.get(TenantSureQuotationRequestForm.class);

    public TenantSureQuotationRequestForm() {
        super(TenantSureCoverageDTO.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();
        int row = -1;

        contentPanel.setH2(++row, 0, 1, i18n.tr("Personal Info and Contact Information"));
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantName())).build());
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantPhone())).build());

        contentPanel.setH2(++row, 0, 1, i18n.tr("Coverage"));
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().personalLiabilityCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().contentsCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().deductible(), new MoneyComboBox())).build());

        contentPanel.setH2(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().smoker(), new YesNoComboBox()), 5).labelWidth("25em").build());
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().numberOfPreviousClaims()), 5).labelWidth("25em").build());

        contentPanel.setH2(++row, 0, 1, i18n.tr("Payment"));
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentSchedule())).build());

        return contentPanel;
    }

    /** resets the form and sets pre-defined options for filling the from */
    public void setCoverageParams(TenantSureQuotationRequestParamsDTO params) {
        TenantSureCoverageDTO coverageRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        coverageRequest.tenantName().setValue(params.tenantName().getValue());
        coverageRequest.tenantPhone().setValue(params.tenantPhone().getValue());
        coverageRequest.paymentSchedule().setValue(params.defaultPaymentSchedule().getValue());

        setValue(coverageRequest, false);

        ((CComboBox<BigDecimal>) (get(proto().personalLiabilityCoverage()))).setOptions(params.generalLiabilityCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().contentsCoverage()))).setOptions(params.contentsCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().deductible()))).setOptions(params.deductibleOptions());
    }
}

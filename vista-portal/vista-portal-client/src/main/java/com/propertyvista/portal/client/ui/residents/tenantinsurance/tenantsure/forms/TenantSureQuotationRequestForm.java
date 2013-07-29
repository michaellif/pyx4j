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
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
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
        TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        contentPanel.setH2(++row, 0, 2, i18n.tr("Personal Info and Contact Information"));
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().tenantName())).build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().tenantPhone())).build());

        contentPanel.setH2(++row, 0, 2, i18n.tr("Coverage"));
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().personalLiabilityCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().contentsCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().deductible(), new MoneyComboBox())).build());

        contentPanel.setH2(++row, 0, 2, i18n.tr("Coverage Qualification Questions"));
        contentPanel.setWidget(++row, 0, 2,
                new FormDecoratorBuilder(inject(proto().smoker(), new YesNoComboBox())).labelWidth("25em").labelAlignment(Alignment.left).componentWidth("5em")
                        .contentWidth("5em").build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().numberOfPreviousClaims())).labelWidth("25em").componentWidth("5em")
                .contentWidth("5em").labelAlignment(Alignment.left).build());

        return contentPanel;
    }

    /** resets the form and sets pre-defined options for filling the from */
    public void setCoverageParams(TenantSureQuotationRequestParamsDTO params) {
        TenantSureCoverageDTO coverageRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        setValue(coverageRequest, false);

        (get(proto().tenantName())).setValue(params.tenantName().getValue());
        (get(proto().tenantPhone())).setValue(params.tenantPhone().getValue());

        ((CComboBox<BigDecimal>) (get(proto().personalLiabilityCoverage()))).setOptions(params.generalLiabilityCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().contentsCoverage()))).setOptions(params.contentsCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().deductible()))).setOptions(params.deductibleOptions());
    }
}

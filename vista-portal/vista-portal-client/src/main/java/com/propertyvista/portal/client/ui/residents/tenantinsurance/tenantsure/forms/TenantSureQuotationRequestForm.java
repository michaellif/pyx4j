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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.LegalTermsFolder;
import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.SignatureFolder;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.components.MoneyComboBox;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.components.YesNoComboBox;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;

public class TenantSureQuotationRequestForm extends CEntityDecoratableForm<TenantSureQuotationRequestDTO> {

    private static final I18n i18n = I18n.get(TenantSureQuotationRequestForm.class);

    public TenantSureQuotationRequestForm() {
        super(TenantSureQuotationRequestDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().policy().personalLiabilityCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().policy().contentsCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().policy().deductible(), new MoneyComboBox())).build());

        contentPanel.setH2(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().policy().smoker(), new YesNoComboBox())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().policy().numberOfPreviousClaims())).build());

        contentPanel.setWidget(++row, 0, inject(proto().personalDisclaimerTerms(), new LegalTermsFolder(true)));
        contentPanel.setWidget(++row, 0, inject(proto().digitalSignatures(), new SignatureFolder(true)));
        return contentPanel;
    }

    /** resets the form and sets pre-defined options for filling the from */
    public void setCoverageParams(TenantSureQuotationRequestParamsDTO params) {
        TenantSureQuotationRequestDTO coverageRequest = EntityFactory.create(TenantSureQuotationRequestDTO.class);
        coverageRequest.personalDisclaimerTerms().addAll(params.personalDisclaimerTerms());
        coverageRequest.digitalSignatures().addAll(params.digitalSignatures());
        setValue(coverageRequest, false);

        ((CComboBox<BigDecimal>) (get(proto().policy().personalLiabilityCoverage()))).setOptions(params.generalLiabilityCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().policy().contentsCoverage()))).setOptions(params.contentsCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().policy().deductible()))).setOptions(params.deductibleOptions());

    }
}

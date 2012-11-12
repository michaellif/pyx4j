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
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureCoverageRequestDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuotationRequestParamsDTO;

public class TenantSureCoverageRequestForm extends CEntityDecoratableForm<TenantSureCoverageRequestDTO> {

    private static final I18n i18n = I18n.get(TenantSureCoverageRequestForm.class);

    public TenantSureCoverageRequestForm() {
        super(TenantSureCoverageRequestDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalLiabilityCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contentsCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deductible(), new MoneyComboBox())).build());

        contentPanel.setH2(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().smoker(), new YesNoComboBox())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfPreviousClaims())).build());

        contentPanel.setWidget(++row, 0, inject(proto().personalDisclaimerTerms(), new LegalTermsFolder(true)));
        contentPanel.setWidget(++row, 0, inject(proto().digitalSignatures(), new SignatureFolder(true)));
        return contentPanel;
    }

    public void setCoverageParams(TenantSureQuotationRequestParamsDTO params) {
        TenantSureCoverageRequestDTO coverageRequest = EntityFactory.create(TenantSureCoverageRequestDTO.class);
        coverageRequest.personalDisclaimerTerms().addAll(params.personalDisclaimerTerms());
        coverageRequest.digitalSignatures().addAll(params.digitalSignatures());
        setValue(coverageRequest, false);

        ((CComboBox<BigDecimal>) (get(proto().personalLiabilityCoverage()))).setOptions(params.generalLiabilityCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().contentsCoverage()))).setOptions(params.contentsCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().deductible()))).setOptions(params.deductibleOptions());

    }
}

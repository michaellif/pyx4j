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
package com.propertyvista.portal.resident.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.common.client.ui.components.tenantinsurance.YesNoComboBox;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureAgreementParamsDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class TenantSureCoverageRequestForm extends CEntityForm<TenantSureCoverageDTO> {

    private static final I18n i18n = I18n.get(TenantSureCoverageRequestForm.class);

    private LogicalDate lastInceptionDate;

    public TenantSureCoverageRequestForm() {
        this(false);
    }

    public TenantSureCoverageRequestForm(boolean readOnly) {
        super(TenantSureCoverageDTO.class);
        setViewable(readOnly);
        inheritViewable(!readOnly);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().inceptionDate())).build());
        contentPanel.setH1(++row, 0, 1, i18n.tr("Coverage"));
        contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().personalLiabilityCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().contentsCoverage(), new MoneyComboBox())).build());
        contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().deductible(), new MoneyComboBox())).build());

        contentPanel.setH1(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
        contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().smoker(), new YesNoComboBox())).build());
        contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().numberOfPreviousClaims())).build());

        if (VistaTODO.VISTA_3207_TENANT_SURE_YEARLY_PAY_SCHEDULE_IMPLEMENTED) {
            contentPanel.setH1(++row, 0, 1, i18n.tr("Payment"));
            contentPanel.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().paymentSchedule())).build());
        }

        return contentPanel;
    }

    /** resets the form and sets pre-defined options for filling the from */
    public void setCoverageParams(TenantSureAgreementParamsDTO params) {
        ((CComboBox<BigDecimal>) (get(proto().personalLiabilityCoverage()))).setOptions(params.generalLiabilityCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().contentsCoverage()))).setOptions(params.contentsCoverageOptions());
        ((CComboBox<BigDecimal>) (get(proto().deductible()))).setOptions(params.deductibleOptions());
        this.lastInceptionDate = params.lastInceptionDate().getValue();
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().inceptionDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public AbstractValidationError isValid() {
                if (TenantSureCoverageRequestForm.this.lastInceptionDate != null) {
                    if (getComponent().getValue() != null && getComponent().getValue().compareTo(lastInceptionDate) > 0) {
                        return new FieldValidationError(getComponent(), i18n.tr("Maximum possible value for inception date is {0,short,date}",
                                lastInceptionDate));
                    }
                    if (getComponent().getValue() != null & getComponent().getValue().compareTo(new LogicalDate()) < 0) {
                        return new FieldValidationError(getComponent(), i18n.tr("This date cannot be in the past", lastInceptionDate));
                    }
                }
                return null;
            }
        });
    }
}

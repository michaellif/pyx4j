/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartMetadata;

public class ArrearsYoyAnalysisGadgetMetadataForm extends CEntityDecoratableForm<ArrearsYOYAnalysisChartMetadata> {

    private final static I18n i18n = I18n.get(ArrearsYoyAnalysisGadgetMetadataForm.class);

    public ArrearsYoyAnalysisGadgetMetadataForm() {
        super(ArrearsYOYAnalysisChartMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel p = new FormFlexPanel();
        int row = -1;
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yearsToCompare())).build());
        get(proto().yearsToCompare()).addValueValidator(new EditableValueValidator<Integer>() {
            @Override
            public ValidationFailure isValid(CComponent<Integer, ?> component, Integer value) {
                if (value != null & value >= 0) {
                    if (value > ArrearsReportService.YOY_ANALYSIS_CHART_MAX_YEARS_AGO) {
                        return new ValidationFailure(i18n.tr("Please enter a value between 0 and {0}", ArrearsReportService.YOY_ANALYSIS_CHART_MAX_YEARS_AGO));
                    }
                    return null;
                } else {
                    return new ValidationFailure(i18n.tr("Non-negative value expected"));
                }
            }
        });
        return p;
    }

}

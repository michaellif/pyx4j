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
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;

public class ArrearsYoyAnalysisGadgetMetadataForm extends CEntityDecoratableForm<ArrearsYOYAnalysisChartGadgetMetadata> {

    private final static I18n i18n = I18n.get(ArrearsYoyAnalysisGadgetMetadataForm.class);

    public ArrearsYoyAnalysisGadgetMetadataForm() {
        super(ArrearsYOYAnalysisChartGadgetMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel p = new TwoColumnFlexFormPanel();
        int row = -1;
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().refreshInterval())).build());
        p.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().yearsToCompare())).build());
        get(proto().yearsToCompare()).addValueValidator(new EditableValueValidator<Integer>() {
            @Override
            public ValidationError isValid(CComponent<Integer> component, Integer value) {
                if (value != null & value >= 0) {
                    if (value > ArrearsReportService.YOY_ANALYSIS_CHART_MAX_YEARS_AGO) {
                        return new ValidationError(component, i18n.tr("Please enter a value between 0 and {0}",
                                ArrearsReportService.YOY_ANALYSIS_CHART_MAX_YEARS_AGO));
                    }
                    return null;
                } else {
                    return new ValidationError(component, i18n.tr("Non-negative value expected"));
                }
            }
        });
        return p;
    }

}

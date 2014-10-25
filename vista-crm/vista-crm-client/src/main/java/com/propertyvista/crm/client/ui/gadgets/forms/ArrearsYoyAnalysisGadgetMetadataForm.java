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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;

public class ArrearsYoyAnalysisGadgetMetadataForm extends CForm<ArrearsYOYAnalysisChartGadgetMetadata> {

    private final static I18n i18n = I18n.get(ArrearsYoyAnalysisGadgetMetadataForm.class);

    public ArrearsYoyAnalysisGadgetMetadataForm() {
        super(ArrearsYOYAnalysisChartGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().refreshInterval()).decorate();
        formPanel.append(Location.Left, proto().yearsToCompare()).decorate();
        get(proto().yearsToCompare()).addComponentValidator(new AbstractComponentValidator<Integer>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null & getCComponent().getValue() >= 0) {
                    if (getCComponent().getValue() > ArrearsReportService.YOY_ANALYSIS_CHART_MAX_YEARS_AGO) {
                        return new BasicValidationError(getCComponent(), i18n.tr("Please enter a value between 0 and {0}",
                                ArrearsReportService.YOY_ANALYSIS_CHART_MAX_YEARS_AGO));
                    }
                    return null;
                } else {
                    return new BasicValidationError(getCComponent(), i18n.tr("Non-negative value expected"));
                }
            }
        });
        return formPanel;
    }
}

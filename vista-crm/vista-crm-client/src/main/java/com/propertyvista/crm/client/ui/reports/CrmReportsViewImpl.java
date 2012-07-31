/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.reports.AbstractReportsView;
import com.pyx4j.site.client.ui.reports.AdvancedReportSettingsForm;
import com.pyx4j.site.client.ui.reports.ReportSettings;

public class CrmReportsViewImpl extends AbstractReportsView implements CrmReportsView {

    @Override
    public CEntityForm<? extends ReportSettings> getReportSettingsForm(ReportSettings reportSettings) {
        if (reportSettings == null) {
            return null;
        } else if (reportSettings.getInstanceValueClass().equals(MockupReportSettings.class)) {
            return new AdvancedReportSettingsForm<MockupReportSettings>(MockupReportSettings.class) {

                @Override
                public Widget createSimpleSettingsPanel() {
                    FormFlexPanel simple = new FormFlexPanel();
                    int row = -1;
                    simple.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().valueX())).build());
                    simple.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().valueY())).build());
                    return simple;
                }

                @Override
                public Widget createAdvancedSettingsPanel() {
                    FormFlexPanel advanced = new FormFlexPanel();
                    int row = -1;
                    advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValueX())).build());
                    advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValueY())).build());
                    advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValueZ())).build());
                    return advanced;
                }

            };
        } else {
            return null;
        }
    }

}

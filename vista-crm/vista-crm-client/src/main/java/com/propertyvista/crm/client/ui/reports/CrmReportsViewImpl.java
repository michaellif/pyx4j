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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.reports.AbstractReportsView;
import com.pyx4j.site.client.ui.reports.AdvancedReportSettingsForm;
import com.pyx4j.site.client.ui.reports.IReportSettingsForm;
import com.pyx4j.site.client.ui.reports.ReportFactory;
import com.pyx4j.site.client.ui.reports.ReportSettings;

public class CrmReportsViewImpl extends AbstractReportsView implements CrmReportsView {

    private static Map<Class<? extends ReportSettings>, ReportFactory> factoryMap;

    static {
        factoryMap = new HashMap<Class<? extends ReportSettings>, ReportFactory>();

        factoryMap.put(MockupReportSettings.class, new ReportFactory() {
            @Override
            public IReportSettingsForm<? extends ReportSettings> getReportSettingsForm(ReportSettings reportSettings) {

                AdvancedReportSettingsForm<MockupReportSettings> form = new AdvancedReportSettingsForm<MockupReportSettings>(MockupReportSettings.class) {
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
                form.initContent();
                return form;
            }

            @Override
            public Widget getReport() {
                return new FormFlexPanel();
            }
        });

    }

    public CrmReportsViewImpl() {
        super(factoryMap);
    }

}

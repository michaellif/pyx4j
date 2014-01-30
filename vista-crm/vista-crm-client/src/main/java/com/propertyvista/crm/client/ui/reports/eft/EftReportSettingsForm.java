/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.eft;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.reports.ReportOrderColumnMetadata;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.organisation.common.PortfolioFolder;
import com.propertyvista.crm.rpc.dto.reports.EftReportRecordDTO;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.shared.config.VistaFeatures;

public class EftReportSettingsForm extends CEntityForm<EftReportMetadata> {

    private static final I18n i18n = I18n.get(EftReportSettingsForm.class);

    public EftReportSettingsForm() {
        super(EftReportMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FlexTable panel = new FlexTable();

        FlowPanel column1 = new FlowPanel();
        final String CHECKBOX_WIDTH = "100px";
        final String INPUT_FIELD_WIDTH = "150px";
        final String CONTENT_WIDTH = "180px";
        column1.add(new FormDecoratorBuilder(inject(proto().leasesOnNoticeOnly())).contentWidth(CONTENT_WIDTH).build());
        column1.add(new FormDecoratorBuilder(inject(proto().forthcomingEft())).contentWidth(CONTENT_WIDTH).build());
        get(proto().forthcomingEft()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().filterByBillingCycle()).setValue(event.getValue() == true, true);
                get(proto().filterByBillingCycle()).setEditable(event.getValue() != true);
                get(proto().paymentStatus()).setVisible(event.getValue() != true);
                get(proto().onlyWithNotice()).setVisible(event.getValue() != true);
            }
        });
        column1.add(new FormDecoratorBuilder(inject(proto().onlyWithNotice())).contentWidth(CONTENT_WIDTH).build());
        column1.add(new FormDecoratorBuilder(inject(proto().paymentStatus())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH).build());
        column1.add(new FormDecoratorBuilder(inject(proto().orderBy(), makeOrderByComboBox())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());

        FlowPanel column2 = new FlowPanel();
        column2.add(new FormDecoratorBuilder(inject(proto().filterByBillingCycle())).componentWidth(CHECKBOX_WIDTH).contentWidth(CONTENT_WIDTH).build());
        get(proto().filterByBillingCycle()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().billingPeriod()).setVisible(event.getValue());
                if (VistaFeatures.instance().yardiIntegration()) {
                    get(proto().billingPeriod()).setValue(BillingPeriod.Monthly);
                }
                get(proto().billingCycleStartDate()).setVisible(event.getValue());
            }
        });

        FlowPanel billingCycleFilterPanel = new FlowPanel();
        billingCycleFilterPanel.getElement().getStyle().setPaddingLeft(3, Unit.EM);
        billingCycleFilterPanel.getElement().getStyle().setPaddingBottom(1, Unit.EM);

        billingCycleFilterPanel.add(new FormDecoratorBuilder(inject(proto().billingPeriod())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());
        get(proto().billingPeriod()).setEnabled(!VistaFeatures.instance().yardiIntegration());

        billingCycleFilterPanel.add(new FormDecoratorBuilder(inject(proto().billingCycleStartDate())).componentWidth(INPUT_FIELD_WIDTH)
                .contentWidth(CONTENT_WIDTH).build());
        column2.add(billingCycleFilterPanel);

        column2.add(new FormDecoratorBuilder(inject(proto().filterByExpectedMoveOut())).componentWidth(CHECKBOX_WIDTH).contentWidth(CONTENT_WIDTH).build());
        get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimum()).setVisible(event.getValue());
                get(proto().maximum()).setVisible(event.getValue());
            }
        });
        FlowPanel expectedMoveOutFilterPanel = new FlowPanel();
        expectedMoveOutFilterPanel.getElement().getStyle().setPaddingLeft(3, Unit.EM);
        expectedMoveOutFilterPanel.add(new FormDecoratorBuilder(inject(proto().minimum())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());
        expectedMoveOutFilterPanel.add(new FormDecoratorBuilder(inject(proto().maximum())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());
        column2.add(expectedMoveOutFilterPanel);

        FlowPanel buildingFilterPanel = new FlowPanel();
        buildingFilterPanel.add(new FormDecoratorBuilder(inject(proto().filterByPortfolio())).build());
        buildingFilterPanel.add(inject(proto().selectedPortfolios(), new PortfolioFolder(true)));
        get(proto().filterByPortfolio()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedPortfolios()).setVisible(event.getValue());
            }
        });

        buildingFilterPanel.add(new FormDecoratorBuilder(inject(proto().filterByBuildings())).build());
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });
        buildingFilterPanel.add(inject(proto().selectedBuildings(), new SelectedBuildingsFolder()));

        panel.setWidget(0, 0, column1);
        panel.setWidget(0, 1, column2);
        panel.setWidget(0, 2, buildingFilterPanel);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().billingPeriod()).setVisible(getValue().filterByBillingCycle().isBooleanTrue());
        get(proto().billingCycleStartDate()).setVisible(getValue().filterByBillingCycle().isBooleanTrue());

        get(proto().selectedPortfolios()).setVisible((getValue().filterByPortfolio().isBooleanTrue()));
        get(proto().selectedBuildings()).setVisible((getValue().filterByBuildings().isBooleanTrue()));

        get(proto().minimum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
        get(proto().maximum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
    }

    private CComboBox<ReportOrderColumnMetadata> makeOrderByComboBox() {
        CComboBox<ReportOrderColumnMetadata> orderByComboBox = new CComboBox<ReportOrderColumnMetadata>();
        orderByComboBox.setFormat(new IFormat<ReportOrderColumnMetadata>() {
            @Override
            public ReportOrderColumnMetadata parse(String string) throws ParseException {
                return null;
            }

            @Override
            public String format(ReportOrderColumnMetadata o) {
                if (o != null && !o.memberPath().isNull()) {
                    String direction = o.isDesc().isBooleanTrue() ? "\u21E7" : "\u21E9";
                    return direction + " "
                            + EntityFactory.getEntityMeta(EftReportRecordDTO.class).getMemberMeta(new Path(o.memberPath().getValue())).getCaption();
                } else {
                    return i18n.tr("Default");
                }
            }
        });

        ArrayList<ReportOrderColumnMetadata> reportColumnOptions = new ArrayList<ReportOrderColumnMetadata>();
        EftReportRecordDTO reportRecordProto = EntityFactory.getEntityPrototype(EftReportRecordDTO.class);
        for (String memberName : reportRecordProto.getEntityMeta().getMemberNames()) {
            if (!memberName.endsWith("_")) {
                ReportOrderColumnMetadata c = EntityFactory.create(ReportOrderColumnMetadata.class);
                c.memberPath().setValue(reportRecordProto.getMember(memberName).getPath().toString());
                reportColumnOptions.add(c);
                c = c.duplicate();
                c.isDesc().setValue(true);
                reportColumnOptions.add(c);
            }
        }
        Collections.sort(reportColumnOptions, new Comparator<ReportOrderColumnMetadata>() {

            @Override
            public int compare(ReportOrderColumnMetadata o1, ReportOrderColumnMetadata o2) {
                EntityMeta meta = EntityFactory.getEntityMeta(EftReportRecordDTO.class);
                int cmp = meta.getMemberMeta(new Path(o1.memberPath().getValue())).getCaption()
                        .compareTo(meta.getMemberMeta(new Path(o2.memberPath().getValue())).getCaption());
                if (cmp == 0) {
                    cmp = -Boolean.valueOf(o1.isDesc().isBooleanTrue()).compareTo(o2.isDesc().isBooleanTrue());
                }
                return cmp;
            }

        });
        orderByComboBox.setOptions(reportColumnOptions);

        return orderByComboBox;
    }
}

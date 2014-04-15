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
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.shared.domain.reports.ReportOrderColumnMetadata;

import com.propertyvista.crm.client.ui.crud.organisation.common.PortfolioFolder;
import com.propertyvista.crm.rpc.dto.reports.EftReportRecordDTO;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.shared.config.VistaFeatures;

public class EftReportSettingsForm extends CEntityForm<EftReportMetadata> {

    private static final I18n i18n = I18n.get(EftReportSettingsForm.class);

    private final IPane parentView;

    public EftReportSettingsForm(IPane parentView) {
        super(EftReportMetadata.class);
        this.parentView = parentView;
    }

    @Override
    protected IsWidget createContent() {
        FlexTable panel = new FlexTable();

        FlowPanel column1 = new FlowPanel();
        final String CHECKBOX_WIDTH = "100px";
        final String INPUT_FIELD_WIDTH = "150px";
        final String CONTENT_WIDTH = "180px";
        column1.add(inject(proto().leasesOnNoticeOnly(), new FormDecoratorBuilder().contentWidth(CONTENT_WIDTH).build()));
        column1.add(inject(proto().forthcomingEft(), new FormDecoratorBuilder().contentWidth(CONTENT_WIDTH).build()));
        get(proto().forthcomingEft()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().filterByBillingCycle()).setValue(event.getValue() == true, true);
                get(proto().filterByBillingCycle()).setEditable(event.getValue() != true);
                get(proto().paymentStatus()).setVisible(event.getValue() != true);
                get(proto().onlyWithNotice()).setVisible(event.getValue() != true);
            }
        });
        column1.add(inject(proto().onlyWithNotice(), new FormDecoratorBuilder().contentWidth(CONTENT_WIDTH).build()));
        column1.add(inject(proto().paymentStatus(), new FormDecoratorBuilder().componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH).build()));
        column1.add(inject(proto().orderBy(), makeOrderByComboBox(), new FormDecoratorBuilder().componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build()));

        FlowPanel column2 = new FlowPanel();
        column2.add(inject(proto().filterByBillingCycle(), new FormDecoratorBuilder().componentWidth(CHECKBOX_WIDTH).contentWidth(CONTENT_WIDTH).build()));
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

        billingCycleFilterPanel.add(inject(proto().billingPeriod(), new FormDecoratorBuilder().componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build()));
        get(proto().billingPeriod()).setEnabled(!VistaFeatures.instance().yardiIntegration());

        billingCycleFilterPanel.add(inject(proto().billingCycleStartDate(),
                new FormDecoratorBuilder().componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH).build()));
        column2.add(billingCycleFilterPanel);

        column2.add(inject(proto().filterByExpectedMoveOut(), new FormDecoratorBuilder().componentWidth(CHECKBOX_WIDTH).contentWidth(CONTENT_WIDTH).build()));
        get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimum()).setVisible(event.getValue());
                get(proto().maximum()).setVisible(event.getValue());
            }
        });
        FlowPanel expectedMoveOutFilterPanel = new FlowPanel();
        expectedMoveOutFilterPanel.getElement().getStyle().setPaddingLeft(3, Unit.EM);
        expectedMoveOutFilterPanel.add(inject(proto().minimum(), new FormDecoratorBuilder().componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build()));
        expectedMoveOutFilterPanel.add(inject(proto().maximum(), new FormDecoratorBuilder().componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build()));
        column2.add(expectedMoveOutFilterPanel);

        FlowPanel buildingFilterPanel = new FlowPanel();
        buildingFilterPanel.add(inject(proto().filterByPortfolio(), new FormDecoratorBuilder().build()));
        buildingFilterPanel.add(inject(proto().selectedPortfolios(), new PortfolioFolder(parentView, isEditable())));
        get(proto().filterByPortfolio()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedPortfolios()).setVisible(event.getValue());
            }
        });

        buildingFilterPanel.add(inject(proto().filterByBuildings(), new FormDecoratorBuilder().build()));
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });
        buildingFilterPanel.add(inject(proto().selectedBuildings(), new SelectedBuildingsFolder(parentView)));

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
        get(proto().billingPeriod()).setVisible(getValue().filterByBillingCycle().getValue(false));
        get(proto().billingCycleStartDate()).setVisible(getValue().filterByBillingCycle().getValue(false));

        get(proto().selectedPortfolios()).setVisible((getValue().filterByPortfolio().getValue(false)));
        get(proto().selectedBuildings()).setVisible((getValue().filterByBuildings().getValue(false)));

        get(proto().minimum()).setVisible(getValue().filterByExpectedMoveOut().getValue(false));
        get(proto().maximum()).setVisible(getValue().filterByExpectedMoveOut().getValue(false));
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
                    String direction = o.isDesc().getValue(false) ? "\u21E7" : "\u21E9";
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
                    cmp = -Boolean.valueOf(o1.isDesc().getValue(false)).compareTo(o2.isDesc().getValue(false));
                }
                return cmp;
            }

        });
        orderByComboBox.setOptions(reportColumnOptions);

        return orderByComboBox;
    }
}

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
 */
package com.propertyvista.crm.client.ui.reports.eft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.shared.domain.reports.ReportOrderColumnMetadata;

import com.propertyvista.crm.client.ui.reports.SelectPortfolioFolder;
import com.propertyvista.crm.client.ui.reports.SelectedBuildingsFolder;
import com.propertyvista.crm.rpc.dto.reports.EftReportRecordDTO;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.shared.config.VistaFeatures;

public class EftReportSettingsForm extends CForm<EftReportMetadata> {

    private static final I18n i18n = I18n.get(EftReportSettingsForm.class);

    public EftReportSettingsForm() {
        super(EftReportMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        final String LABEL_WIDTH = "180px";
        final String INPUT_WIDTH = "120px";

        int row = 0;

        FlexTable column1 = new FlexTable();
        column1.setWidget(row++, 0, inject(proto().leasesOnNoticeOnly(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column1.setWidget(row++, 0, inject(proto().forthcomingEft(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column1.setWidget(row++, 0, inject(proto().onlyWithNotice(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column1.setWidget(row++, 0, inject(proto().paymentStatus(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        if (SecurityController.check(VistaBasicBehavior.PropertyVistaSupport)) {
            column1.setWidget(row++, 0, inject(proto().trace(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        }
        column1.setWidget(row++, 0, inject(proto().orderBy(), makeOrderByComboBox(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));

        get(proto().forthcomingEft()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().filterByBillingCycle()).setValue(event.getValue() == true, true);
                get(proto().filterByBillingCycle()).setEditable(event.getValue() != true);
                get(proto().paymentStatus()).setVisible(event.getValue() != true);
                get(proto().onlyWithNotice()).setVisible(event.getValue() != true);
            }
        });

        row = 0; // ----------------------------------------------------------------------------------------------------------------

        FlexTable column2 = new FlexTable();
        column2.setWidget(row++, 0, inject(proto().filterByBillingCycle(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column2.setWidget(row++, 0, inject(proto().billingPeriod(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column2.setWidget(row++, 0, inject(proto().billingCycleStartDate(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column2.setWidget(row++, 0, inject(proto().filterByExpectedMoveOut(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column2.setWidget(row++, 0, inject(proto().minimum(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));
        column2.setWidget(row++, 0, inject(proto().maximum(), new FieldDecoratorBuilder(LABEL_WIDTH, INPUT_WIDTH).build()));

        get(proto().billingPeriod()).setEnabled(!VistaFeatures.instance().yardiIntegration());
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
        get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimum()).setVisible(event.getValue());
                get(proto().maximum()).setVisible(event.getValue());
            }
        });

        row = 0; // ----------------------------------------------------------------------------------------------------------------

        FlexTable column3 = new FlexTable();
        column3.getElement().getStyle().setMarginLeft(1, Unit.EM);
        column3.setWidget(row++, 0, inject(proto().filterByPortfolio(), new FieldDecoratorBuilder(INPUT_WIDTH, INPUT_WIDTH).build()));
        column3.setWidget(row++, 0, inject(proto().selectedPortfolios(), new SelectPortfolioFolder()));
        column3.setWidget(row++, 0, inject(proto().filterByBuildings(), new FieldDecoratorBuilder(INPUT_WIDTH, INPUT_WIDTH).build()));
        column3.setWidget(row++, 0, inject(proto().selectedBuildings(), new SelectedBuildingsFolder()));

        get(proto().filterByPortfolio()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedPortfolios()).setVisible(event.getValue());
            }
        });
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });

        // combine it all together: ------------------------------------------------------------------------------------------------
        FlexTable panel = new FlexTable();

        panel.setWidget(0, 0, column1);
        panel.setWidget(0, 1, column2);
        panel.setWidget(0, 2, column3);

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

        get(proto().orderBy()).setValue(null);
    }

    private CComboBox<ReportOrderColumnMetadata> makeOrderByComboBox() {
        CComboBox<ReportOrderColumnMetadata> orderByComboBox = new CComboBox<ReportOrderColumnMetadata>();
        orderByComboBox.populate(null);

        ArrayList<ReportOrderColumnMetadata> reportColumnOptions = new ArrayList<ReportOrderColumnMetadata>();
        EftReportRecordDTO reportRecordProto = EntityFactory.getEntityPrototype(EftReportRecordDTO.class);
        for (String memberName : reportRecordProto.getEntityMeta().getMemberNames()) {
            if (!memberName.endsWith("_")) {
                if (reportRecordProto.getMember(memberName).getMeta().isTransient()) {
                    continue;
                }
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
        orderByComboBox.setFormat(new IFormatter<ReportOrderColumnMetadata, String>() {
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

        return orderByComboBox;
    }
}

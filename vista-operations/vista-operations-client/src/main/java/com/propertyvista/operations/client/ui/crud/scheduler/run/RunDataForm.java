/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-22
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.form.IForm;
import com.pyx4j.site.client.ui.crud.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportMessageService;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportSectionService;

public class RunDataForm extends OperationsEntityForm<RunData> {

    private static final I18n i18n = I18n.get(RunDataForm.class);

    private final ExecutionReportSectionLister reportSectionLister;

    public RunDataForm(IForm<RunData> view) {
        super(RunData.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pmc().dnsName()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().started()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 15).build());

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().total()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().processed()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().failed()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().erred()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().averageDuration()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().totalDuration()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().message()), 40).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().errorMessage()), 40).build());

        reportSectionLister = new ExecutionReportSectionLister();
        content.setH4(++row, 0, 2, i18n.tr("Details"));
        content.setWidget(++row, 0, reportSectionLister);

        selectTab(addTab(content));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().errorMessage()).setVisible(!getValue().errorMessage().isNull());

        reportSectionLister.getDataSource().setParentFiltering(getValue().executionReport().getPrimaryKey(), ExecutionReport.class);
        reportSectionLister.restoreState();
    }

    public class ExecutionReportSectionLister extends EntityDataTablePanel<ExecutionReportSection> {

        private final ExecutionReportMessageLister messageLister;

        private final Dialog messageDialog;

        public ExecutionReportSectionLister() {
            super(ExecutionReportSection.class);

            setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().name()).build(),
                new MemberColumnDescriptor.Builder(proto().type()).build(),
                new MemberColumnDescriptor.Builder(proto().counter()).build(),
                new MemberColumnDescriptor.Builder(proto().value()).build(),
                new MemberColumnDescriptor(new Builder(proto().messages())) {
                    @Override
                    public String convert(IEntity entity) {
                        return String.valueOf(entity.isEmpty() ? 0 : ((ISet<?>)entity.getMember(getColumnPath())).size());
                    }
                }
            );//@formatter:on

            setDataSource(new ListerDataSource<ExecutionReportSection>(ExecutionReportSection.class,
                    GWT.<AbstractListService<ExecutionReportSection>> create(ExecutionReportSectionService.class)));

            setAllowZoomIn(true);

            messageDialog = new OkDialog(i18n.tr("Execution Messages")) {
                @Override
                public boolean onClickOk() {
                    return true;
                }
            };
            messageDialog.setBody(messageLister = new ExecutionReportMessageLister());
        }

        @Override
        protected void onItemSelect(ExecutionReportSection item) {
            messageLister.getDataSource().setParentFiltering(item.getPrimaryKey(), ExecutionReportSection.class);
            messageLister.restoreState();
            messageDialog.show();
        }

    }

    public class ExecutionReportMessageLister extends EntityDataTablePanel<ExecutionReportMessage> {

        public ExecutionReportMessageLister() {
            super(ExecutionReportMessage.class);

            setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().eventTime()).build(),
                new MemberColumnDescriptor.Builder(proto().message()).build()
            );//@formatter:on

            setDataSource(new ListerDataSource<ExecutionReportMessage>(ExecutionReportMessage.class,
                    GWT.<AbstractListService<ExecutionReportMessage>> create(ExecutionReportMessageService.class)));
        }
    }
}
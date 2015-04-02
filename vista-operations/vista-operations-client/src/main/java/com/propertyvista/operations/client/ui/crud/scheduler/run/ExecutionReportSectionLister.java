/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 28, 2013
 * @author stanp
 */
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportMessageService;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportSectionService;

public class ExecutionReportSectionLister extends SiteDataTablePanel<ExecutionReportSection> {
    private static final I18n i18n = I18n.get(ExecutionReportSectionLister.class);

    private final ExecutionReportMessageLister messageLister;

    private final Dialog messageDialog;

    public ExecutionReportSectionLister() {
        super(ExecutionReportSection.class, GWT.<AbstractListCrudService<ExecutionReportSection>> create(ExecutionReportSectionService.class));

        setItemZoomInCommand(new ItemZoomInCommand<ExecutionReportSection>() {
            @Override
            public void execute(ExecutionReportSection item) {
                messageLister.getDataSource().setParentEntityId(item.getPrimaryKey(), ExecutionReportSection.class);
                messageLister.populate();
                messageDialog.show();
            }
        });

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().name()).build(), //
                new ColumnDescriptor.Builder(proto().type()).build(), //
                new ColumnDescriptor.Builder(proto().counter()).build(), //
                new ColumnDescriptor.Builder(proto().value()).build(), //
                new ColumnDescriptor.Builder(proto().messages()).formatter(new IFormatter<IEntity, SafeHtml>() {

                    @Override
                    public SafeHtml format(IEntity value) {
                        SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        builder.appendHtmlConstant(String.valueOf(value.isEmpty() ? 0 : ((ICollection<?, ?>) value.getMember(proto().messages().getPath()))
                                .size()));
                        return builder.toSafeHtml();
                    }
                }).build(), //
                new ColumnDescriptor.Builder(proto().messages()).columnTitle(i18n.tr("First Message")).formatter(new IFormatter<IEntity, SafeHtml>() {

                    @Override
                    public SafeHtml format(IEntity value) {
                        SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        String message = value.isEmpty() || ((IList<ExecutionReportMessage>) value.getMember(proto().messages().getPath())).isEmpty() ? ""
                                : ((IList<ExecutionReportMessage>) value.getMember(proto().messages().getPath())).get(0).message().getValue();
                        if (message != null) {
                            builder.appendHtmlConstant(message);
                        }
                        return builder.toSafeHtml();
                    }
                }).build());

        setDataTableModel(new DataTableModel<ExecutionReportSection>());

        messageDialog = new OkDialog(i18n.tr("Execution Messages")) {
            @Override
            public boolean onClickOk() {
                return true;
            }
        };
        messageDialog.setBody(messageLister = new ExecutionReportMessageLister());
    }

    public class ExecutionReportMessageLister extends DataTablePanel<ExecutionReportMessage> {

        public ExecutionReportMessageLister() {
            super(ExecutionReportMessage.class);

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().eventTime()).build(), //
                    new ColumnDescriptor.Builder(proto().message()).width("300px").build());

            setDataTableModel(new DataTableModel<ExecutionReportMessage>());

            setDataSource(new ListerDataSource<ExecutionReportMessage>(ExecutionReportMessage.class,
                    GWT.<AbstractListCrudService<ExecutionReportMessage>> create(ExecutionReportMessageService.class)));
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().eventTime(), false));
        }
    }
}

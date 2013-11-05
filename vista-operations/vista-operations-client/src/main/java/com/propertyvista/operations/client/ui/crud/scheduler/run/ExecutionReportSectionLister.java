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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportMessageService;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportSectionService;

public class ExecutionReportSectionLister extends EntityDataTablePanel<ExecutionReportSection> {
    private static final I18n i18n = I18n.get(ExecutionReportSectionLister.class);

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
                    return String.valueOf(entity.isEmpty() ? 0 : ((ICollection<?,?>)entity.getMember(getColumnPath())).size());
                }
            },
            new MemberColumnDescriptor(new Builder(proto().messages())) {
                {
                    setColumnTitle(i18n.tr("First Message"));                    
                }
                @Override
                public String convert(IEntity entity) {
                    String message = entity.isEmpty() ? "" : ((IList<ExecutionReportMessage>)entity.getMember(getColumnPath())).get(0).message().getValue();
                    if (message != null && message.length() > 200) {
                        message = message.substring(0, 200) + "...";                        
                    }
                    return message != null ? message : "";
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

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().eventTime(), false));
        }
    }
}

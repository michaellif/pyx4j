/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-19
 * @author Amer Sohail
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.customercreditcheck;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReportWidget;
import com.pyx4j.widgets.client.memento.IMementoAware;
import com.pyx4j.widgets.client.memento.IMementoInput;
import com.pyx4j.widgets.client.memento.IMementoOutput;

import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.CustomerCreditCheckReportDataDTO;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class CustomerCreditCheckReportWidget extends HTML implements IReportWidget, IMementoAware {

    private static final I18n i18n = I18n.get(CustomerCreditCheckReportWidget.class);

    private static ColumnDescriptor[] CREDITCHECK_TABLE_COLUMNS;

    static {
        CustomerCreditCheck proto = EntityFactory.getEntityPrototype(CustomerCreditCheck.class);

        CREDITCHECK_TABLE_COLUMNS = new ColumnDescriptor[] {

        new MemberColumnDescriptor.Builder(proto.screening().screene().person().name()).title(i18n.tr("Tenant")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.screening().screene().person().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.screening().screene().person().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.creditCheckDate()).build(),
                new MemberColumnDescriptor.Builder(proto.createdBy().name()).title(i18n.tr("Created By")).build(),
                new MemberColumnDescriptor.Builder(proto.createdBy().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.createdBy().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.amountChecked()).build(), new MemberColumnDescriptor.Builder(proto.riskCode()).build(),
                new MemberColumnDescriptor.Builder(proto.creditCheckResult()).build(), new MemberColumnDescriptor.Builder(proto.amountApproved()).build(),
                new MemberColumnDescriptor.Builder(proto.reason()).build()

        //statuses
        };
    }

    ScrollBarPositionMemento scrollBarPositionMemento;

    @Override
    public void setData(Object data) {
        setHTML("");
        if (data == null) {
            return;
        }

        CustomerCreditCheckReportDataDTO reportData = (CustomerCreditCheckReportDataDTO) data;
        if (reportData.unitStatuses.isEmpty()) {
            setHTML(NoResultsHtml.get());
            return;
        }
        SafeHtmlBuilder bb = new SafeHtmlBuilder();
        bb.appendHtmlConstant("<div style=\"text-align: center; font-size: 22px; line-height: 22px;\">");
        bb.appendEscaped(i18n.tr("Customer Credit Check Report"));
        bb.appendHtmlConstant("</div>");
        bb.appendHtmlConstant("<div>");
        bb.appendHtmlConstant("</div>");

        bb.appendHtmlConstant("<table style=\"white-space: nowrap; border-collapse: separate; border-spacing: 15pt;\">");
        bb.appendHtmlConstant("<tr>");

        for (ColumnDescriptor desc : CREDITCHECK_TABLE_COLUMNS) {
            bb.appendHtmlConstant("<th style=\"text-align: left\">");
            bb.appendEscaped(desc.getColumnTitle());
            bb.appendHtmlConstant("</th>");
        }

        bb.appendHtmlConstant("</tr>");

        for (CustomerCreditCheck status : reportData.unitStatuses) {
            bb.appendHtmlConstant("<tr>");
            for (ColumnDescriptor desc : CREDITCHECK_TABLE_COLUMNS) {
                bb.appendHtmlConstant("<td>");
                bb.append(desc.getFormatter().format(status));
                bb.appendHtmlConstant("</td>");
            }
            bb.appendHtmlConstant("</tr>");
        }
        bb.appendHtmlConstant("</table>");

        setHTML(bb.toSafeHtml());
        addDomHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                scrollBarPositionMemento = new ScrollBarPositionMemento(getElement().getScrollLeft(), getElement().getScrollTop());
            }
        }, ScrollEvent.getType());

    }

    @Override
    public void saveState(IMementoOutput memento) {
        memento.write(getHTML());
        memento.write(scrollBarPositionMemento);
    }

    @Override
    public void restoreState(IMementoInput memento) {
        String html = (String) (memento.read());
        scrollBarPositionMemento = (ScrollBarPositionMemento) memento.read();

        setHTML(html);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (scrollBarPositionMemento != null) {
                    getElement().setScrollLeft(scrollBarPositionMemento.posX);
                    getElement().setScrollTop(scrollBarPositionMemento.posY);
                }
            }
        });
    }

}

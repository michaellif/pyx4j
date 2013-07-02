/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.factories.autopay;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReviewDataGrid extends Composite {

    private static final I18n i18n = I18n.get(AutoPayChangesReviewDataGrid.class);

    private final DataGrid<AutoPayReviewDTO> dataGrid = new DataGrid<AutoPayReviewDTO>(Integer.MAX_VALUE);

    private Column<AutoPayReviewDTO, String> suggestedPaymentColumn;

    private class HeaderBuilder extends AbstractHeaderOrFooterBuilder<AutoPayReviewDTO> {

        //@formatter:off
        private final Header<String> buildingHeader = new TextHeader(i18n.tr("Building"));
        private final Header<String> unitHeader = new TextHeader(i18n.tr("Unit"));
        private final Header<String> leaseHeader = new TextHeader(i18n.tr("Lease ID"));
        private final Header<String> expectedMoveOutHeader = new TextHeader(i18n.tr("Expected Move Out"));        
        private final Header<String> tenantNameHeader = new TextHeader(i18n.tr("Tenant Name"));
        private final Header<String> chargeCodeHeader = new TextHeader(i18n.tr("Charge Code"));
        
        // suspended               
        private final Header<String> suspendedTotalPriceHeader = new TextHeader(i18n.tr("Total Price"));        
        private final Header<String> suspendedPaymentHeader = new TextHeader(i18n.tr("Payment"));        
        private final Header<String> suspendedPercentOfTotalHeader = new TextHeader(i18n.tr("% of Total"));
        
        // suggested              
        private final Header<String> suggestedTotalPriceHeader = new TextHeader(i18n.tr("Total Price"));
        private final Header<String> suggestedChangeHeader = new TextHeader(i18n.tr("Change"));
        private final Header<String> suggestedPaymentHeader = new TextHeader(i18n.tr("Payment"));        
        private final Header<String> suggestedPercentOfTotalHeader = new TextHeader(i18n.tr("% of Total"));
        
        private final Header<String> paymentDueHeader = new TextHeader(i18n.tr("Payment Due"));
        //@formatter:on

        public HeaderBuilder() {
            super(dataGrid, false);
        }

        @Override
        protected boolean buildHeaderOrFooterImpl() {
            TableRowBuilder tr = startRow();

            TableCellBuilder th = tr.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 0, buildingHeader.getKey()), buildingHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 1, unitHeader.getKey()), unitHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 2, leaseHeader.getKey()), leaseHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 3, expectedMoveOutHeader.getKey()), expectedMoveOutHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 4, tenantNameHeader.getKey()), tenantNameHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 5, chargeCodeHeader.getKey()), chargeCodeHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 6, suspendedTotalPriceHeader.getKey()), suspendedTotalPriceHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 7, suspendedPaymentHeader.getKey()), suspendedPaymentHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 8, suspendedPercentOfTotalHeader.getKey()), suspendedPercentOfTotalHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 9, suggestedTotalPriceHeader.getKey()), suggestedTotalPriceHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 10, suggestedChangeHeader.getKey()), suggestedChangeHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 11, suggestedPaymentHeader.getKey()), suggestedPaymentHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 12, suggestedTotalPriceHeader.getKey()), suggestedTotalPriceHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 13, paymentDueHeader.getKey()), paymentDueHeader);
            tr.endTH();

            tr.endTR();

            return true;
        }
    }

    private class TableBuilder extends AbstractCellTableBuilder<AutoPayReviewDTO> {

        public TableBuilder() {
            super(dataGrid);
        }

        @Override
        protected void buildRowImpl(AutoPayReviewDTO reviewCaseValue, int absRowIndex) {

            int numOfCaseRows = caseRows(reviewCaseValue);

            TableRowBuilder tr = startRow();

            TableCellBuilder td = tr.startTD();
            td.rowSpan(numOfCaseRows).text(reviewCaseValue.building().getStringView());
            td.endTD();

            td = tr.startTD();
            td.rowSpan(numOfCaseRows).text(reviewCaseValue.unit().getStringView());
            td.endTD();

            td = tr.startTD();
            td.rowSpan(numOfCaseRows).text(reviewCaseValue.leaseId().getStringView());
            td.endTD();

            td = tr.startTD();
            td.rowSpan(numOfCaseRows).text(reviewCaseValue.lease().expectedMoveOut().getStringView());
            td.endTD();

            boolean isFirstLine = true;
            for (AutoPayReviewPreauthorizedPaymentDTO reviewPap : reviewCaseValue.pap()) {
                int numOfTenantRows = reviewPap.items().size();
                if (!isFirstLine) {
                    tr = startRow();
                }
                td = tr.startTD();
                td.rowSpan(numOfTenantRows).text(reviewPap.tenantName().getStringView());
                td.endTD();

                boolean isFirstCharge = true;
                for (AutoPayReviewChargeDTO charge : reviewPap.items()) {
                    if (!isFirstCharge) {
                        tr = startRow();
                    } else {
                        isFirstCharge = false;
                    }

                    td = tr.startTD();
                    td.text(charge.leaseCharge().getStringView());
                    td.endTD();

                    // suspended
                    td = tr.startTD();
                    td.text(charge.suspended().totalPrice().getStringView());
                    td.endTD();

                    td = tr.startTD();
                    td.text(charge.suspended().payment().getStringView());
                    td.endTD();

                    td = tr.startTD();
                    td.text(charge.suspended().percent().getStringView());
                    td.endTD();

                    // suggested

                    td = tr.startTD();
                    td.text(charge.suggested().totalPrice().getStringView());
                    td.endTD();

                    String percentChange = SafeHtmlUtils.htmlEscape(charge.suggested().billableItem().isNull() ? i18n.tr("Removed") : charge.suggested()
                            .percentChange().isNull() ? i18n.tr("New") : charge.suggested().percentChange().getStringView());
                    td = tr.startTD();
                    td.text(percentChange);
                    td.endTD();

                    SelectionModel<? super AutoPayReviewDTO> selectionModel = dataGrid.getSelectionModel();
                    td = tr.startTD();
                    if (selectionModel.isSelected(reviewCaseValue)) {
                        renderCell(td, createContext(11), suggestedPaymentColumn, reviewCaseValue);
                    } else {
                        td.text(charge.suggested().payment().getStringView());
                    }
                    td.endTD();

                    td = tr.startTD();
                    td.text(charge.suggested().percent().getStringView());
                    td.endTD();

                    if (isFirstLine) {
                        td = tr.startTD();
                        td.rowSpan(numOfCaseRows).text(reviewCaseValue.paymentDue().getStringView());
                        td.endTD();
                    }

                    tr.endTR();

                    if (isFirstLine) {
                        isFirstLine = false;
                    }
                }
            }

//            tr.endTR();
        }
    }

    public AutoPayChangesReviewDataGrid() {
        suggestedPaymentColumn = new Column<AutoPayReviewDTO, String>(new EditTextCell() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
                super.render(context, value, sb);
                System.out.println("<-------------------------------------------");
                System.out.println("Col: " + context.getColumn());
                System.out.println("Indx: " + context.getIndex());
                System.out.println("SubIndex: " + context.getSubIndex());
                System.out.println(">-------------------------------------------");
            }
        }) {
            @Override
            public String getValue(AutoPayReviewDTO object) {
                return object.pap().get(0).items().get(0).suggested().payment().getStringView();
            }
        };

        dataGrid.setHeaderBuilder(new HeaderBuilder());
        dataGrid.setTableBuilder(new TableBuilder());
        dataGrid.setSelectionModel(new SingleSelectionModel<AutoPayReviewDTO>());
        initWidget(dataGrid);
        getElement().setId("AutoPayChangesReviewDataGrid");

        ListDataProvider<AutoPayReviewDTO> listDataProvider = new ListDataProvider<AutoPayReviewDTO>(devFillWithMockup(new LinkedList<AutoPayReviewDTO>()));
        listDataProvider.addDataDisplay(dataGrid);
    }

    /**
     * Generate mockup data for UI debugging
     */
    private List<AutoPayReviewDTO> devFillWithMockup(List<AutoPayReviewDTO> data) {
        if (!ApplicationMode.isDevelopment()) {
            return data;
        }

        for (int i = 0; i < 1000; ++i) {
            AutoPayReviewDTO dto = EntityFactory.create(AutoPayReviewDTO.class);
            dto.building().setValue("building #B");
            dto.unit().setValue("unit #U");
            dto.leaseId().setValue("t0000" + i);
            Lease lease = EntityFactory.create(Lease.class);
            lease.setPrimaryKey(new Key(1));
            dto.lease().set(lease);
            dto.paymentDue().setValue(new LogicalDate());
            for (int j = 0; j < 3; ++j) {
                AutoPayReviewPreauthorizedPaymentDTO papDto = dto.pap().$();
                papDto.tenantName().setValue("Ivan Vasilyevich" + j);

                for (int charge = 0; charge < 3; ++charge) {
                    AutoPayReviewChargeDTO papItemDto = papDto.items().$();
                    papItemDto.leaseCharge().setValue("ccode " + charge);
                    papItemDto.suspended().totalPrice().setValue(new BigDecimal("1000"));
                    papItemDto.suspended().percentChange().setValue(new BigDecimal("0.1"));
                    papItemDto.suspended().payment().setValue(new BigDecimal("120" + charge));
                    papItemDto.suspended().percent().setValue(new BigDecimal("0.25"));
                    papItemDto.suggested().totalPrice().setValue(new BigDecimal("120" + charge));
                    papItemDto.suggested().percentChange().setValue(new BigDecimal("0.2"));
                    papItemDto.suggested().payment().setValue(new BigDecimal("1234"));
                    papItemDto.suggested().percent().setValue(new BigDecimal("0.11"));
                    papDto.items().add(papItemDto);
                }
                dto.pap().add(papDto);
            }
            data.add(dto);
        }
        return data;
    }

    private int caseRows(AutoPayReviewDTO reviewCase) {
        int rows = 0;
        for (AutoPayReviewPreauthorizedPaymentDTO pap : reviewCase.pap()) {
            rows += pap.items().size();
        }
        return rows;
    }

}

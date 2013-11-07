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
package com.propertyvista.crm.client.ui.tools.autopayreview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;

public class AutoPayReviewDataGrid extends Composite {

    private static final I18n i18n = I18n.get(AutoPayReviewDataGrid.class);

    private final DataGrid<PapChargeReviewDTO> dataGrid = new DataGrid<PapChargeReviewDTO>(100);

    private final ListDataProvider<PapChargeReviewDTO> listDataProvider;

    private Column<PapChargeReviewDTO, String> suggestedPaymentColumn;

    private class HeaderBuilder extends AbstractHeaderOrFooterBuilder<PapChargeReviewDTO> {

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
            renderHeader(th, new Context(0, 12, suggestedPercentOfTotalHeader.getKey()), suggestedPercentOfTotalHeader);
            tr.endTH();

            th.startTH();
            th.colSpan(1).rowSpan(2);
            renderHeader(th, new Context(0, 13, paymentDueHeader.getKey()), paymentDueHeader);
            tr.endTH();

            tr.endTR();

            return true;
        }
    }

    private class TableBuilder extends AbstractCellTableBuilder<PapChargeReviewDTO> {

        public TableBuilder() {
            super(dataGrid);
        }

        @Override
        protected void buildRowImpl(PapChargeReviewDTO papCharge, int absRowIndex) {

            TableRowBuilder tr = startRow();
            if (papCharge._isPivot().isBooleanTrue()) {
                TableCellBuilder td = tr.startTD();
                td.colSpan(9);
                td.style().fontWeight(FontWeight.BOLD).paddingTop(10, Unit.PX);
                td.text(i18n.tr("{0} {1} {2}", papCharge._parentPap().building().getValue(), papCharge._parentPap().unit().getValue(), papCharge._parentPap()
                        .lease().getValue()));
                td.endTD();

                tr.endTR();
            }

            tr = startRow();

            TableCellBuilder td = tr.startTD();
            td.text(papCharge.chargeName().getStringView() + " " + papCharge.getPrimaryKey());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.suspendedPrice().getStringView());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.suspendedPapAmount().getStringView());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.suspendedPapPercent().getStringView());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.newPrice().getStringView());
            td.endTD();

            td = tr.startTD();
            // suggested change
            td.endTD();

            td = tr.startTD();
            renderCell(td, createContext(11), suggestedPaymentColumn, papCharge);
            // suggested payment
            td.endTD();

            td = tr.startTD();
            // suggested perncet of total
            td.endTD();

            td = tr.startTD();
            // payment due
            td.endTD();

            tr.endTR();

        }
    }

    public AutoPayReviewDataGrid() {
        suggestedPaymentColumn = new Column<PapChargeReviewDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(PapChargeReviewDTO object) {
                return object.newPapAmount().getStringView();
            }
        };
        suggestedPaymentColumn.setFieldUpdater(new FieldUpdater<PapChargeReviewDTO, String>() {
            @Override
            public void update(int index, PapChargeReviewDTO object, String value) {
                try {
                    object.newPapAmount().setValue(new BigDecimal(value));
                } catch (Throwable e) {

                }
                listDataProvider.refresh();
            }
        });

        dataGrid.setHeight("100%");
        dataGrid.getResources();
        dataGrid.setHeaderBuilder(new HeaderBuilder());
        dataGrid.setTableBuilder(new TableBuilder());

        initWidget(dataGrid);
        getElement().setId("AutoPayChangesReviewDataGrid");

        listDataProvider = new ListDataProvider<PapChargeReviewDTO>(new ArrayList<PapChargeReviewDTO>());
        listDataProvider.addDataDisplay(dataGrid);
    }

    public void populate(List<PapChargeReviewDTO> charges) {
        listDataProvider.setList(charges);
        listDataProvider.refresh();
    }

}

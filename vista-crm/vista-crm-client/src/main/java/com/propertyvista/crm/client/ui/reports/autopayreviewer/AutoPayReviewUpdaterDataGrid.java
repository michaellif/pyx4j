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
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeDTO;

public class AutoPayReviewUpdaterDataGrid extends Composite {

    private static final I18n i18n = I18n.get(AutoPayReviewUpdaterDataGrid.class);

    private final DataGrid<PapChargeDTO> dataGrid = new DataGrid<PapChargeDTO>(20);

    private final ListDataProvider<PapChargeDTO> listDataProvider;

    private Column<PapChargeDTO, String> suggestedPaymentColumn;

    private class HeaderBuilder extends AbstractHeaderOrFooterBuilder<PapChargeDTO> {

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

    private class TableBuilder extends AbstractCellTableBuilder<PapChargeDTO> {

        public TableBuilder() {
            super(dataGrid);
        }

        @Override
        protected void buildRowImpl(PapChargeDTO papCharge, int absRowIndex) {

            TableRowBuilder tr = startRow();

            TableCellBuilder td = tr.startTD();
            // building
            td.endTD();

            td = tr.startTD();
            // unit
            td.endTD();

            td = tr.startTD();
            // lease
            td.endTD();

            td = tr.startTD();
            // expected move out
            td.endTD();

            td = tr.startTD();
            // tenant name
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.chargeName().getStringView() + " " + papCharge.getPrimaryKey());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.suspendedPrice().getStringView());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.suspendedPreAuthorizedPaymentAmount().getStringView());
            td.endTD();

            td = tr.startTD();
            td.text(papCharge.suspendedPreAuthorizedPaymentPercent().getStringView());
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

    public AutoPayReviewUpdaterDataGrid() {
        suggestedPaymentColumn = new Column<PapChargeDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(PapChargeDTO object) {
                return object.suggestedNewPreAuthorizedPaymentAmount().getStringView();
            }
        };
        suggestedPaymentColumn.setFieldUpdater(new FieldUpdater<PapChargeDTO, String>() {
            @Override
            public void update(int index, PapChargeDTO object, String value) {
                try {
                    object.suggestedNewPreAuthorizedPaymentAmount().setValue(new BigDecimal(value));
                } catch (Throwable e) {

                }
                listDataProvider.refresh();
            }
        });
        dataGrid.setHeaderBuilder(new HeaderBuilder());
        dataGrid.setTableBuilder(new TableBuilder());

        initWidget(dataGrid);
        getElement().setId("AutoPayChangesReviewDataGrid");

        listDataProvider = new ListDataProvider<PapChargeDTO>(new ArrayList<PapChargeDTO>());
        listDataProvider.addDataDisplay(dataGrid);
    }

    public void populate(List<PapChargeDTO> charges) {
        listDataProvider.setList(charges);
        listDataProvider.refresh();
    }

}

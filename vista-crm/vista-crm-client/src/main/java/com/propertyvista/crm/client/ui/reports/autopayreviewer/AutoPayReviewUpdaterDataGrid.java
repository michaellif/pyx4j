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

import static com.google.gwt.dom.client.BrowserEvents.BLUR;
import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;
import static com.google.gwt.dom.client.BrowserEvents.KEYUP;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
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

public class AutoPayReviewUpdaterDataGrid extends Composite {

    private static final I18n i18n = I18n.get(AutoPayReviewUpdaterDataGrid.class);

    private final DataGrid<AutoPayReviewDTO> dataGrid = new DataGrid<AutoPayReviewDTO>(Integer.MAX_VALUE);

    private Column<AutoPayReviewDTO, AutoPayReviewDTO> suggestedPaymentColumn;

    /**
     * The view data object used by this cell. We need to store both the text and
     * the state because this cell is rendered differently in edit mode. If we did
     * not store the edit state, refreshing the cell with view data would always
     * put us in to edit state, rendering a text box instead of the new text
     * string.
     */
    static class ViewData {

        private boolean isEditing;

        /**
         * If true, this is not the first edit.
         */
        private boolean isEditingAgain;

        /**
         * Keep track of the original value at the start of the edit, which might be
         * the edited value from the previous edit and NOT the actual value.
         */
        private String original;

        private String text;

        /**
         * Construct a new ViewData in editing mode.
         * 
         * @param text
         *            the text to edit
         */
        public ViewData(String text) {
            this.original = text;
            this.text = text;
            this.isEditing = true;
            this.isEditingAgain = false;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            ViewData vd = (ViewData) o;
            return equalsOrBothNull(original, vd.original) && equalsOrBothNull(text, vd.text) && isEditing == vd.isEditing
                    && isEditingAgain == vd.isEditingAgain;
        }

        public String getOriginal() {
            return original;
        }

        public String getText() {
            return text;
        }

        @Override
        public int hashCode() {
            return original.hashCode() + text.hashCode() + Boolean.valueOf(isEditing).hashCode() * 29 + Boolean.valueOf(isEditingAgain).hashCode();
        }

        public boolean isEditing() {
            return isEditing;
        }

        public boolean isEditingAgain() {
            return isEditingAgain;
        }

        public void setEditing(boolean isEditing) {
            boolean wasEditing = this.isEditing;
            this.isEditing = isEditing;

            // This is a subsequent edit, so start from where we left off.
            if (!wasEditing && isEditing) {
                isEditingAgain = true;
                original = text;
            }
        }

        public void setText(String text) {
            this.text = text;
        }

        private boolean equalsOrBothNull(Object o1, Object o2) {
            return (o1 == null) ? o2 == null : o1.equals(o2);
        }
    }

    private final class PaymentEditableCell extends AbstractEditableCell<AutoPayReviewDTO, ViewData> {
        private PaymentEditableCell(String[] consumedEvents) {
            super(consumedEvents);
        }

        @Override
        public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, AutoPayReviewDTO value) {
            ViewData viewData = getViewData(context.getKey());
            return viewData == null ? false : viewData.isEditing();
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, AutoPayReviewDTO value, SafeHtmlBuilder sb) {
            Object key = context.getKey();
            ViewData viewData = getViewData(key);
            if (viewData != null && !viewData.isEditing() && value != null && value.equals(viewData.getText())) {
                clearViewData(key);
                viewData = null;
            }

            int subIndex = context.getSubIndex();
            int curSubIndex = 0;

            String paymnetValue = "";
            Iterator<AutoPayReviewPreauthorizedPaymentDTO> i = value.pap().iterator();
            paymentValueFound: while (i.hasNext()) {
                AutoPayReviewPreauthorizedPaymentDTO a = i.next();
                Iterator<AutoPayReviewChargeDTO> j = a.items().iterator();
                while (j.hasNext()) {
                    AutoPayReviewChargeDTO charge = j.next();
                    if (curSubIndex == subIndex) {
                        paymnetValue = charge.suggested().payment().getValue().toString();
                        break paymentValueFound;
                    }
                    ++curSubIndex;
                }
            }
            String toRender = paymnetValue;

            if (viewData != null) {
                String text = viewData.getText();
                if (viewData.isEditing()) {
                    /*
                     * Do not use the renderer in edit mode because the value of a text
                     * input element is always treated as text. SafeHtml isn't valid in the
                     * context of the value attribute.
                     */
                    sb.appendHtmlConstant("<input type=\"text\" value=\"" + text + "\" tabindex=\"-1\"></input>");
                    return;
                } else {
                    // The user pressed enter, but view data still exists.
                    toRender = text;
                }
            }

            if (toRender != null && toRender.trim().length() > 0) {
//                    sb.append(renderer.render(toRender));
                sb.appendHtmlConstant(toRender);
            } else {
                /*
                 * Render a blank space to force the rendered element to have a height.
                 * Otherwise it is not clickable.
                 */
                sb.appendHtmlConstant("\u00A0");
            }
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, AutoPayReviewDTO value, NativeEvent event, ValueUpdater<AutoPayReviewDTO> valueUpdater) {
            Object key = context.getKey();
            ViewData viewData = getViewData(key);
            if (viewData != null && viewData.isEditing()) {
                // Handle the edit event.
//                editEvent(context, parent, value, viewData, event, valueUpdater);
            } else {
                String type = event.getType();
                int keyCode = event.getKeyCode();
                boolean enterPressed = KEYUP.equals(type) && keyCode == KeyCodes.KEY_ENTER;
                if (CLICK.equals(type) || enterPressed) {
                    // Go into edit mode.
                    if (viewData == null) {
                        int curSubIndex = 0;
                        int subIndex = context.getSubIndex();
                        String paymnetValue = "";
                        Iterator<AutoPayReviewPreauthorizedPaymentDTO> i = value.pap().iterator();
                        paymentValueFound: while (i.hasNext()) {
                            AutoPayReviewPreauthorizedPaymentDTO a = i.next();
                            Iterator<AutoPayReviewChargeDTO> j = a.items().iterator();
                            while (j.hasNext()) {
                                AutoPayReviewChargeDTO charge = j.next();
                                if (curSubIndex == subIndex) {
                                    paymnetValue = charge.suggested().payment().getValue().toString();
                                    break paymentValueFound;
                                }
                                ++curSubIndex;
                            }
                        }
                        viewData = new ViewData(paymnetValue);
                        setViewData(key, viewData);
                    } else {
                        viewData.setEditing(true);
                    }
                    edit(context, parent, value);
                }
            }
        }

        private void edit(com.google.gwt.cell.client.Cell.Context context, Element parent, AutoPayReviewDTO value) {
            setValue(context, parent, value);
            InputElement input = getInputElement(parent);
            input.focus();
            input.select();

        }

        private InputElement getInputElement(Element parent) {
            return parent.getFirstChild().<InputElement> cast();
        }

    }

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

    public AutoPayReviewUpdaterDataGrid() {
        suggestedPaymentColumn = new Column<AutoPayReviewDTO, AutoPayReviewDTO>(new PaymentEditableCell(new String[] { CLICK, KEYUP, KEYDOWN, BLUR })) {
            @Override
            public AutoPayReviewDTO getValue(AutoPayReviewDTO object) {
                return object;
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
                    papItemDto.suggested().payment().setValue(new BigDecimal(1000 + j));
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

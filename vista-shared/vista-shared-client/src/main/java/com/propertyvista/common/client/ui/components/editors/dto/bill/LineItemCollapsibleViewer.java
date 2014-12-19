/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author stanp
 */
package com.propertyvista.common.client.ui.components.editors.dto.bill;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.decorators.FieldCollapsableDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;
import com.pyx4j.widgets.client.images.WidgetsImages;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceSubLineItem;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;

public class LineItemCollapsibleViewer extends CViewer<InvoiceLineItemGroupDTO> implements ToggleHandler {

    private static final I18n i18n = I18n.get(LineItemCollapsibleViewer.class);

    private SimplePanel collapsedPanel;

    private SimplePanel expandedPanel;

    public LineItemCollapsibleViewer() {
        setDecorator(new LineItemCollapsibleDecorator(VistaImages.INSTANCE));
        setFormatter(new IFormatter<InvoiceLineItemGroupDTO, IsWidget>() {
            @Override
            public IsWidget format(InvoiceLineItemGroupDTO value) {
                FlowPanel mainPanel = new FlowPanel();

                mainPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                mainPanel.getElement().getStyle().setMarginLeft(2, Unit.EM);
                mainPanel.getElement().getStyle().setPaddingTop(0.3, Unit.EM);

                collapsedPanel = new SimplePanel();
                collapsedPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                collapsedPanel.getElement().getStyle().setPosition(Position.RELATIVE);
                mainPanel.add(collapsedPanel);

                expandedPanel = new SimplePanel();
                expandedPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                expandedPanel.getElement().getStyle().setPosition(Position.RELATIVE);
                mainPanel.add(expandedPanel);

                collapsedPanel.setWidget(createCollapsedContent(value));
                expandedPanel.setWidget(createExpandedContent(value));
                setExpended(false);

                return mainPanel;
            }
        });
    }

    class LineItemCollapsibleDecorator extends FieldCollapsableDecorator<InvoiceLineItemGroupDTO> {
        public LineItemCollapsibleDecorator(WidgetsImages images) {
            super(images);
            addToggleHandler(LineItemCollapsibleViewer.this);
        }
    }

    private void setExpended(boolean expended) {
        ((LineItemCollapsibleDecorator) getDecorator()).setExpended(expended);
    }

    @Override
    public void onToggle(ToggleEvent event) {
        collapsedPanel.setVisible(!event.isToggleOn());
        expandedPanel.setVisible(event.isToggleOn());
    }

    private IsWidget createCollapsedContent(InvoiceLineItemGroupDTO value) {
        BasicFlexFormPanel content = new BasicFlexFormPanel();

        content.getColumnFormatter().setWidth(0, "80%"); //overrides FlexTable's width auto-management for cells
        content.getColumnFormatter().setWidth(2, "20%");
//        content.setWidth(FieldDecoratorBuilder.LABEL_WIDTH + FieldDecoratorBuilder.CONTENT_WIDTH + "em");
        content.setWidth("345px");

        int row = 0;
        if (value != null && !value.total().isNull()) {
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            content.setWidget(row, 0, new HTML(value.getMeta().getCaption()));
            content.setWidget(row, 2, new HTML(fomatMoneyValue(value.total())));

            // styling:
            content.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingLineItem.name());
            content.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingLineItemTitle.name());
            content.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingLineItemAmount.name());
        }

        return content;
    }

    private IsWidget createExpandedContent(InvoiceLineItemGroupDTO value) {
        BasicFlexFormPanel content = new BasicFlexFormPanel();

        content.getColumnFormatter().setWidth(0, "20%"); //overrides FlexTable's width auto-management for cells
        content.getColumnFormatter().setWidth(1, "60%");
        content.getColumnFormatter().setWidth(2, "20%");
//        content.setWidth(FieldDecoratorBuilder.LABEL_WIDTH + FieldDecoratorBuilder.CONTENT_WIDTH + "em");
        content.setWidth("345px");

        int row = 0;
        if (value != null && !value.total().isNull()) {
            content.setWidget(row, 0, new HTML(value.getMeta().getCaption()));
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            // styling:
            content.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingLineItem.name());
            content.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingLineItemTitle.name());

            row++;
            if (value.lineItems() != null && value.lineItems().size() > 0) {
                for (InvoiceLineItem item : value.lineItems()) {
                    if (item instanceof InvoiceDebit) {
                        if (item instanceof InvoiceProductCharge) {
                            InvoiceProductCharge productCharge = (InvoiceProductCharge) item;
                            InvoiceSubLineItem charge = productCharge.chargeSubLineItem();
                            addDetailRecord(content, row++, formatDays(item), charge.description().getValue(), fomatMoneyValue(charge.amount()));
                            for (InvoiceSubLineItem adjustment : productCharge.adjustmentSubLineItems()) {
                                addDetailRecord(content, row++, formatDays(item), adjustment.description().getValue(), fomatMoneyValue(adjustment.amount()));
                            }
                            for (InvoiceSubLineItem concession : productCharge.concessionSubLineItems()) {
                                addDetailRecord(content, row++, formatDays(item), concession.description().getValue(), fomatMoneyValue(concession.amount()));
                            }
                        } else {
                            addDetailRecord(content, row++, formatDays(item), item.description().getValue(), fomatMoneyValue(item.amount()));
                        }
                    } else if (item instanceof InvoiceCredit) {
                        addDetailRecord(content, row++, formatDays(item), item.description().getValue(), fomatMoneyValue(item.amount()));
                    }
                }
            }
            addTotalRecord(content, row++, i18n.tr("Total"), fomatMoneyValue(value.total()));
        }

        return content;
    }

    private String fomatMoneyValue(IPrimitive<BigDecimal> value) {
        return "$" + value.getStringView();
    }

    private void addDetailRecord(BasicFlexFormPanel table, int row, String date, String description, String amount) {
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailItem.name());

        table.setWidget(row, 0, new HTML(date));
        table.getWidget(row, 0).setStyleName(BillingTheme.StyleName.BillingDetailItemDate.name());

        table.setWidget(row, 1, new HTML(description));
        table.getWidget(row, 1).setStyleName(BillingTheme.StyleName.BillingDetailItemTitle.name());

        table.setWidget(row, 2, new HTML(amount));
        table.getWidget(row, 2).setStyleName(BillingTheme.StyleName.BillingDetailItemAmount.name());
    }

    private void addTotalRecord(BasicFlexFormPanel table, int row, String description, String amount) {
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailTotal.name());

        table.setWidget(row, 1, new HTML(description));
        table.getWidget(row, 1).setStyleName(BillingTheme.StyleName.BillingDetailTotalTitle.name());

        table.setWidget(row, 2, new HTML(amount));
        table.getWidget(row, 2).setStyleName(BillingTheme.StyleName.BillingDetailTotalAmount.name());

        table.getWidget(row, 2).getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private static String formatDays(InvoiceLineItem lineItem) {
        if (lineItem instanceof InvoiceProductCharge) {
            return formatDays(((InvoiceProductCharge) lineItem).fromDate().getValue(), ((InvoiceProductCharge) lineItem).toDate().getValue());
        } else if (lineItem instanceof InvoiceAccountCredit) {
            return formatDays(((InvoiceAccountCredit) lineItem).postDate().getValue(), null);
        } else if (lineItem instanceof InvoiceAccountCharge) {
            return formatDays(((InvoiceAccountCharge) lineItem).postDate().getValue(), null);
        } else {
            return formatDays(lineItem.postDate().getValue(), null);
        }
    }

    private static String formatDays(LogicalDate fromDate, LogicalDate toDate) {
        DateTimeFormat formatter = DateTimeFormat.getFormat("MMM dd");
        if (fromDate != null) {
            if (toDate != null) {
                if (fromDate.equals(toDate)) {
                    return formatter.format(fromDate);
                } else {
                    return formatter.format(fromDate) + " - " + formatter.format(toDate);
                }
            } else {
                return formatter.format(fromDate);
            }
        }
        return "";
    }
}

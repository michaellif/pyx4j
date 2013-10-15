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
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.views.bill;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.ui.decorators.BasicCollapsableDecorator;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;

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
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class LineItemCollapsibleViewer extends CEntityContainer<InvoiceLineItemGroupDTO> implements ToggleHandler {

    private static final I18n i18n = I18n.get(LineItemCollapsibleViewer.class);

    private SimplePanel collapsedPanel;

    private SimplePanel expandedPanel;

    public LineItemCollapsibleViewer() {
//        asWidget().setWidth(FormDecoratorBuilder.FULL_WIDTH);
    }

    @Override
    protected IDecorator<?> createDecorator() {
        BasicCollapsableDecorator<?> decorator = new BasicCollapsableDecorator<InvoiceLineItemGroupDTO>(VistaImages.INSTANCE);
        decorator.addToggleHandler(this);
        return decorator;
    }

    @Override
    public final IsWidget createContent() {
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        collapsedPanel = new SimplePanel();
//        collapsedPanel.getElement().getStyle().setMarginLeft(30, Unit.PX);
        collapsedPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        collapsedPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        mainPanel.add(collapsedPanel);

        expandedPanel = new SimplePanel();
//        expandedPanel.getElement().getStyle().setMarginLeft(30, Unit.PX);
        expandedPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        expandedPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        mainPanel.add(expandedPanel);

        setExpended(false);
        return mainPanel;
    }

    @Override
    public Collection<? extends CComponent<?>> getComponents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setComponentsValue(InvoiceLineItemGroupDTO value, boolean fireEvent, boolean populate) {
        collapsedPanel.setWidget(createCollapsedContent(value));
        expandedPanel.setWidget(createExpandedContent(value));
        setExpended(false);
    }

    private void setExpended(boolean expended) {
        ((BasicCollapsableDecorator<?>) getDecorator()).setExpended(expended);
    }

    @Override
    public void onToggle(ToggleEvent event) {
        collapsedPanel.setVisible(!event.isToggleOn());
        expandedPanel.setVisible(event.isToggleOn());
    }

    private IsWidget createCollapsedContent(InvoiceLineItemGroupDTO value) {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        content.setWidth(FormDecoratorBuilder.LABEL_WIDTH + FormDecoratorBuilder.CONTENT_WIDTH + "px");

        content.getColumnFormatter().setWidth(0, FormDecoratorBuilder.LABEL_WIDTH + "px");
        content.getColumnFormatter().setWidth(2, FormDecoratorBuilder.CONTENT_WIDTH + "px");

        int row = 0;
        if (value != null && !value.total().isNull()) {
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            content.setWidget(row, 0, new HTML(value.getMeta().getCaption()));
            content.setWidget(row, 2, new HTML(value.total().getStringView()));

            // styling:
            content.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingLineItem.name());
            content.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingLineItemTitle.name());
            content.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingLineItemAmount.name());
        }

        return content;
    }

    private IsWidget createExpandedContent(InvoiceLineItemGroupDTO value) {
        BasicFlexFormPanel content = new BasicFlexFormPanel();

        content.getColumnFormatter().setWidth(0, FormDecoratorBuilder.LABEL_WIDTH / 2 + "px");
        content.getColumnFormatter().setWidth(1, FormDecoratorBuilder.LABEL_WIDTH / 2 + "px");
        content.getColumnFormatter().setWidth(2, FormDecoratorBuilder.CONTENT_WIDTH + "px");

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
                            addDetailRecord(content, row++, formatDays(item), charge.description().getValue(), charge.amount().getStringView());
                            for (InvoiceSubLineItem adjustment : productCharge.adjustmentSubLineItems()) {
                                addDetailRecord(content, row++, formatDays(item), adjustment.description().getValue(), adjustment.amount().getStringView());
                            }
                            for (InvoiceSubLineItem concession : productCharge.concessionSubLineItems()) {
                                addDetailRecord(content, row++, formatDays(item), concession.description().getValue(), concession.amount().getStringView());
                            }
                        } else {
                            addDetailRecord(content, row++, formatDays(item), item.description().getValue(), item.amount().getStringView());
                        }
                    } else if (item instanceof InvoiceCredit) {
                        addDetailRecord(content, row++, formatDays(item), item.description().getValue(), item.amount().getStringView());
                    }
                }
            }
            addTotalRecord(content, row++, i18n.tr("Total"), value.total().getStringView());
        }

        return content;
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
        table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
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

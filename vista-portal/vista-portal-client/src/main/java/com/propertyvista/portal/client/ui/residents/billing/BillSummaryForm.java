/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryView.Presenter;
import com.propertyvista.portal.domain.dto.BillSummaryDTO;

public class BillSummaryForm extends CEntityDecoratableForm<BillSummaryDTO> {

    private static final I18n i18n = I18n.get(BillSummaryForm.class);

    private Presenter presenter;

    public BillSummaryForm() {
        super(BillSummaryDTO.class, new VistaViewersComponentFactory());
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentBalance()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dueDate()), 10).build());
        content.setWidget(++row, 0, new Anchor(i18n.tr("View Current Bill"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.viewCurrentBill();
            }
        }));
        content.getWidget(row, 0).getElement().getStyle().setMarginLeft(15, Unit.EM);
        content.setWidget(row, 1, new Button(i18n.tr("Pay Now"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.payNow();
            }
        }));

        content.setBR(++row, 0, 2);
        content.setH3(++row, 0, 2, proto().latestActivities().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().latestActivities(), new InvoiceLineItemFolder()));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        return content;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    class InvoiceLineItemFolder extends VistaTableFolder<InvoiceLineItem> {

        public InvoiceLineItemFolder() {
            super(InvoiceLineItem.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(// @formatter:off
                    new EntityFolderColumnDescriptor(proto().amount(), "10em"),
                    new EntityFolderColumnDescriptor(proto().postDate(), "10em"),
                    new EntityFolderColumnDescriptor(proto().description(), "20em")
            ); // formatter:on
        }
    }
}

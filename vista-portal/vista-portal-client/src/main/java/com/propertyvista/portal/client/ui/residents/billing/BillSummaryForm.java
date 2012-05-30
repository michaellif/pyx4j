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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryView.Presenter;
import com.propertyvista.portal.domain.dto.BillSummaryDTO;

public class BillSummaryForm extends CEntityForm<BillSummaryDTO> {

    private static final I18n i18n = I18n.get(BillSummaryForm.class);

    private Presenter presenter;

    public BillSummaryForm() {
        super(BillSummaryDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();

        int row = -1;
        container.setWidget(++row, 0, inject(proto().currentBalance()));
        container.setWidget(++row, 0, inject(proto().dueDate(), new DueDateViewer()));

        return container;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    class DueDateViewer extends CViewer<LogicalDate> {

        @Override
        public IsWidget createContent(LogicalDate value) {
            if (value != null) {
                FlowPanel container = new FlowPanel();
                container.setWidth("100%");
                container.add(DecorationUtils.inline(new Label(i18n.tr("Due Date")), "14em"));
                container.add(DecorationUtils.inline(new Label(value.toString())));

                container.asWidget().getElement().getStyle().setMarginLeft(20, Unit.PX);
                container.asWidget().getElement().getStyle().setMarginTop(20, Unit.PX);
                return container;
            } else {
                return null;
            }
        }

    }
}

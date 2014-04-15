/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.autopay;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.form.EditableFormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityEditor;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class AutoPayViewForm extends CPortalEntityEditor<AutoPayDTO> {

    private static final I18n i18n = I18n.get(AutoPayViewForm.class);

    public AutoPayViewForm(AutoPayView view) {
        super(AutoPayDTO.class, view, i18n.tr("Auto Pay Agreement"), ThemeColor.contrast4);

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.viewable)) {
                    get(proto().coveredItems()).setVisible(isViewable());
                    get(proto().coveredItemsDTO()).setVisible(!isViewable());
                }
            }
        });
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setWidget(
                ++row,
                0,
                inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>(), new FormWidgetDecoratorBuilder(200).labelAlignment(Alignment.left)
                        .build()));

        mainPanel.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
        mainPanel.setWidget(++row, 0, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder() {
            @Override
            public void onAmontValueChange() {
                BigDecimal total = BigDecimal.ZERO;
                for (PreauthorizedPaymentCoveredItemDTO item : getValue()) {
                    if (!item.amount().isNull()) {
                        total = (total.add(item.amount().getValue()));
                    }
                }
                AutoPayViewForm.this.get(AutoPayViewForm.this.proto().total()).setValue(total);
            }
        }));

        mainPanel.setHR(++row, 0, 1);

        mainPanel.setWidget(++row, 0, inject(proto().total(), new CMoneyLabel(), new FormWidgetDecoratorBuilder(100).build()));
        mainPanel.setWidget(++row, 0, inject(proto().nextPaymentDate(), new CDateLabel(), new FormWidgetDecoratorBuilder(100).build()));

        get(proto().coveredItems()).setVisible(isViewable());
        get(proto().coveredItemsDTO()).setVisible(!isViewable());

        return mainPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().paymentMethod()).setVisible(!getValue().paymentMethod().isNull());
        ((EditableFormDecorator<AutoPayDTO>) getDecorator()).getBtnEdit().setVisible(
                !getValue().paymentMethod().isNull() && !getValue().isDeleted().getValue(false) && !getValue().leaseStatus().getValue().isNoAutoPay()
                        && SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.forAutoPay()));
    }
}

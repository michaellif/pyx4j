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
package com.propertyvista.portal.web.client.ui.financial.autopay;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class AutoPayConfirmationForm extends CPortalEntityForm<AutoPayDTO> {

    private static final I18n i18n = I18n.get(AutoPayConfirmationForm.class);

    public AutoPayConfirmationForm() {
        super(AutoPayDTO.class, null, i18n.tr("Automatic Payment Submitted Successfully!"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 200).labelAlignment(Alignment.left).build());
        mainPanel.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().total()), 100).build());

        mainPanel.setBR(++row, 0, 1);

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), 100).labelWidth(20).build());

        return mainPanel;
    }
}

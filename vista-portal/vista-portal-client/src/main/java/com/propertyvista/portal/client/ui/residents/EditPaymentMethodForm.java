/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.domain.dto.PaymentMethodDTO;

public class EditPaymentMethodForm extends CEntityEditor<PaymentMethodDTO> {

    private static I18n i18n = I18n.get(EditPaymentMethodForm.class);

    public static final String SUBTITLE_STYLE_PREFIX = "EditPaymentMethodFormSubtitle";

    public EditPaymentMethodForm() {
        super(PaymentMethodDTO.class);

    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();
        int row = 0;

        container.setH1(row++, 0, 1, i18n.tr("Edit Payment Information"));

        container.setWidget(row++, 0, new WidgetDecorator(inject(proto().number(), new CLabel())));
        container.setWidget(row++, 0, new WidgetDecorator(inject(proto().nameOnAccount(), new CLabel())));
        container.setWidget(row++, 0, new WidgetDecorator(inject(proto().expiryDate())));
        container.setWidget(row++, 0, new WidgetDecorator(inject(proto().securityCode())));

//        container.add(new VistaLineSeparator(100, Unit.PCT));
//
//        subtitle = new VistaHeaderBar(proto().billingAddress().getMeta().getCaption(), "100%");
//        subtitle.getElement().getStyle().setBackgroundColor("transparent");
//        container.add(subtitle);
//        AddressUtils.injectIAddress(container, proto().billingAddress(), this);

//        container.add(new VistaLineSeparator(100, Unit.PCT));
//
//        subtitle = new VistaHeaderBar(proto().billingAddress().getMeta().getCaption(), "100%");
//        subtitle.getElement().getStyle().setBackgroundColor("transparent");
//        container.add(subtitle);
//        container.add(inject(proto().billingAddress(), new CAddressStructured()));

        return container;
    }

}

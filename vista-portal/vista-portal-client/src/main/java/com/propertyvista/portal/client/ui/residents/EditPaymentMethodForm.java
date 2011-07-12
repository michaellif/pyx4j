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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;

public class EditPaymentMethodForm extends CEntityForm<PaymentMethodDTO> {

    private static I18n i18n = I18nFactory.getI18n(EditPaymentMethodForm.class);

    public static final String SUBTITLE_STYLE_PREFIX = "EditPaymentMethodFormSubtitle";

    public EditPaymentMethodForm() {
        super(PaymentMethodDTO.class);

    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        container.add(new VistaHeaderBar(i18n.tr("Edit Payment Information"), "100%"));

        VistaHeaderBar subtitle = new VistaHeaderBar(i18n.tr("Payment Card"), "100%");
        subtitle.getElement().getStyle().setBackgroundColor("transparent");
        container.add(subtitle);

        DecorationData decor = new DecorationData();
        decor.componentCaption = i18n.tr("Card Number");
        decor.editable = false;
        container.add(new VistaWidgetDecorator(inject(proto().cardNumber()), decor));

        decor = new DecorationData();
        decor.editable = false;
        container.add(new VistaWidgetDecorator(inject(proto().nameOnAccount()), decor));

        container.add(new VistaWidgetDecorator(inject(proto().expiry())));

        decor = new DecorationData();
        decor.componentCaption = i18n.tr("CSC (if applicable)");
        container.add(new VistaWidgetDecorator(inject(proto().verificationDigits()), decor));

        container.add(new VistaLineSeparator(100, Unit.PCT));

        subtitle = new VistaHeaderBar(proto().billingAddress().getMeta().getCaption(), "100%");
        subtitle.getElement().getStyle().setBackgroundColor("transparent");
        container.add(subtitle);
        AddressUtils.injectIAddress(container, proto().billingAddress(), this);

        return container;
    }

}

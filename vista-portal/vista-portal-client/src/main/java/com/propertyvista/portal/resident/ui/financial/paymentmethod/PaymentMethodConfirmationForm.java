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
package com.propertyvista.portal.resident.ui.financial.paymentmethod;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.resident.ui.financial.paymentmethod.PaymentMethodConfirmationView.PaymentMethodConfirmationPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class PaymentMethodConfirmationForm extends CPortalEntityForm<PaymentMethodDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodConfirmationForm.class);

    public PaymentMethodConfirmationForm(PaymentMethodConfirmationView view) {
        super(PaymentMethodDTO.class, view, i18n.tr("New Payment Method Submitted Successfully!"), new Button(i18n.tr("Continue"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        }), ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()).decorate().componentWidth(250)
                .labelAlignment(Alignment.left);
        formPanel.hr();
        formPanel.append(Location.Left, createAutoPaySignupPanel());
        return formPanel;
    }

    private Widget createAutoPaySignupPanel() {
        VerticalPanel text = new VerticalPanel();
        text.add(new HTML(i18n.tr("Want an Easy way to save time on your payments?")));
        text.add(new HTML(i18n.tr("Let us manage your monthly payments for you.")));
        text.add(new Anchor(i18n.tr("Sign up for Auto Pay today"), new Command() {
            @Override
            public void execute() {
                ((PaymentMethodConfirmationPresenter) getView().getPresenter()).goToAutoPay();
            }
        }));

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Image(VistaImages.INSTANCE.recurringCredit()));
        panel.add(text);

        return panel;
    }
}

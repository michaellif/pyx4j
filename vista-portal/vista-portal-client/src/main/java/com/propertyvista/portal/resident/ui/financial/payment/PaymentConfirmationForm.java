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
package com.propertyvista.portal.resident.ui.financial.payment;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.resident.ui.financial.payment.PaymentConfirmationView.PaymentConfirmationPresenter;
import com.propertyvista.portal.shared.ui.AbstractFormView;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class PaymentConfirmationForm extends CPortalEntityForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentConfirmationForm.class);

    private static final String headerUndefined = i18n.tr("Payment Status Undefined...");

    private static final String headerSuccess = i18n.tr("Payment Submitted Successfully!");

    private static final String headerFailed = i18n.tr("Payment Submission Failed!");

    private Widget autoPaySignupPanel;

    public PaymentConfirmationForm(AbstractFormView<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class, view, "", ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().id(), new CNumberLabel(), new FieldDecoratorBuilder().customLabel(i18n.tr("Reference Number")).build()));
        content.setWidget(++row, 0, inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>(), new FieldDecoratorBuilder().build()));
        content.setWidget(++row, 0, inject(proto().amount(), new FieldDecoratorBuilder().build()));
        content.setWidget(++row, 0, inject(proto().transactionAuthorizationNumber(), new FieldDecoratorBuilder().build()));
        content.setWidget(++row, 0, inject(proto().convenienceFee(), new FieldDecoratorBuilder().build()));
        content.setWidget(++row, 0, inject(proto().convenienceFeeTransactionAuthorizationNumber(), new FieldDecoratorBuilder().build()));

        content.setHR(++row, 0, 1);

        content.setWidget(++row, 0, inject(proto().transactionErrorMessage(), new FieldDecoratorBuilder().build()));

        content.setWidget(++row, 0, autoPaySignupPanel = createAutoPaySignupPanel());

        // tweak:
        get(proto().transactionErrorMessage()).asWidget().setStyleName(VistaTheme.StyleName.ErrorMessage.name());

        return content;
    }

    private Widget createAutoPaySignupPanel() {
        VerticalPanel text = new VerticalPanel();
        text.add(new HTML(i18n.tr("Want an Easy way to save time on your payments?")));
        text.add(new HTML(i18n.tr("Let us manage your monthly payments for you.")));
        text.add(new Anchor(i18n.tr("Sign up for Auto Pay today"), new Command() {
            @Override
            public void execute() {
                ((PaymentConfirmationPresenter) getView().getPresenter()).goToAutoPay();
            }
        }));

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Image(VistaImages.INSTANCE.recurringCredit()));
        panel.add(text);

        return panel;
    }

    @Override
    public void onReset() {
        super.onReset();

        if (getDecorator() instanceof FormDecorator) {
            FormDecorator decorator = ((FormDecorator) getDecorator());
            decorator.setCaption(headerUndefined);
            decorator.getCaptionLabel().removeStyleName(VistaTheme.StyleName.ErrorMessage.name());
        }

        get(proto().transactionErrorMessage()).setVisible(false);
        get(proto().transactionAuthorizationNumber()).setVisible(false);
        get(proto().convenienceFeeTransactionAuthorizationNumber()).setVisible(false);
        autoPaySignupPanel.setVisible(true);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getDecorator() instanceof FormDecorator) {
            FormDecorator decorator = ((FormDecorator) getDecorator());
            if (getValue().paymentStatus().getValue().isFailed()) {
                decorator.setCaption(headerFailed);
                decorator.getCaptionLabel().addStyleName(VistaTheme.StyleName.ErrorMessage.name());
            } else {
                decorator.setCaption(headerSuccess);
            }
        }

        if (getValue().paymentStatus().getValue().isFailed()) {
            get(proto().transactionErrorMessage()).setVisible(true);
            autoPaySignupPanel.setVisible(false);
        } else {
            get(proto().transactionAuthorizationNumber()).setVisible(!getValue().transactionAuthorizationNumber().isNull());
            get(proto().convenienceFeeTransactionAuthorizationNumber()).setVisible(!getValue().convenienceFeeTransactionAuthorizationNumber().isNull());
        }

        get(proto().convenienceFee()).setVisible(!getValue().convenienceFee().isNull());
    }
}

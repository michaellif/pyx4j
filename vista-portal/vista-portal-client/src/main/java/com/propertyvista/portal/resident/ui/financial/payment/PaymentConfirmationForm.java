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
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.resident.ui.financial.payment.PaymentConfirmationView.PaymentConfirmationPresenter;
import com.propertyvista.portal.shared.ui.AbstractFormView;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.CCurrencyMoneyLabel;

public class PaymentConfirmationForm extends CPortalEntityForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentConfirmationForm.class);

    private static final String headerUndefined = i18n.tr("Waiting for Payment Processing...");

    private static final String headerSuccess = i18n.tr("Payment Submitted Successfully!");

    private static final String headerFailed = i18n.tr("Payment Submission Failed!");

    private Widget autoPaySignupPanel;

    private HTML errorMessage = new HTML();

    public PaymentConfirmationForm(AbstractFormView<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class, view, i18n.tr("Payment Submitted Successfully!"), new Button(i18n.tr("Continue"), new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        }), ThemeColor.contrast4);

        errorMessage.addStyleName(VistaTheme.StyleName.ErrorMessage.name());
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().customLabel(i18n.tr("Reference Number"));
        formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()).decorate();
        formPanel.append(Location.Left, proto().amount(), new CCurrencyMoneyLabel(i18n.tr("CAD $"))).decorate();
        formPanel.append(Location.Left, proto().transactionAuthorizationNumber()).decorate();
        formPanel.append(Location.Left, proto().convenienceFee()).decorate();
        formPanel.append(Location.Left, proto().convenienceFeeTransactionAuthorizationNumber()).decorate();

        formPanel.hr();

        formPanel.append(Location.Left, proto().transactionErrorMessage()).decorate().labelWidth(250);

        formPanel.append(Location.Left, autoPaySignupPanel = createAutoPaySignupPanel());

        formPanel.br();
        formPanel.append(Location.Left, errorMessage);

        // tweak:
        get(proto().transactionErrorMessage()).asWidget().setStyleName(VistaTheme.StyleName.ErrorMessage.name());
        errorMessage.setVisible(false);

        return formPanel;
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
            FormDecorator<?> decorator = ((FormDecorator<?>) getDecorator());
            decorator.setCaption(headerUndefined);
            decorator.getCaptionLabel().addStyleName(VistaTheme.StyleName.WarningMessage.name());
            decorator.getCaptionLabel().removeStyleName(VistaTheme.StyleName.ErrorMessage.name());
        }

        get(proto().transactionErrorMessage()).setVisible(false);
        get(proto().transactionAuthorizationNumber()).setVisible(false);
        get(proto().convenienceFeeTransactionAuthorizationNumber()).setVisible(false);
        autoPaySignupPanel.setVisible(true);
        errorMessage.setVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getDecorator() instanceof FormDecorator) {
            FormDecorator<?> decorator = ((FormDecorator<?>) getDecorator());
            decorator.getCaptionLabel().removeStyleName(VistaTheme.StyleName.WarningMessage.name());
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

    void didplayError(String message) {
        FormDecorator<?> decorator = ((FormDecorator<?>) getDecorator());
        decorator.setCaption(headerFailed);
        decorator.getCaptionLabel().addStyleName(VistaTheme.StyleName.ErrorMessage.name());

        errorMessage.setHTML(message);
        errorMessage.setVisible(true);

        autoPaySignupPanel.setVisible(false);
    }
}

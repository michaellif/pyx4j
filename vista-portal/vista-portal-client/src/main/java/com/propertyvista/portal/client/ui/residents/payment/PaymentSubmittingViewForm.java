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
package com.propertyvista.portal.client.ui.residents.payment;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.ui.residents.ViewBaseImpl;

public class PaymentSubmittingViewForm extends CEntityDecoratableForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentSubmittingViewForm.class);

    private static final String headerSuccess = i18n.tr("Payment Submitted Successfully!");

    private static final String headerFailed = i18n.tr("Payment Submittion Failed!");

    private static final HTML header = new HTML();

    private final ViewBaseImpl<PaymentRecordDTO> view;

    public PaymentSubmittingViewForm(ViewBaseImpl<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class);
        this.view = view;
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, 2, header);
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().transactionErrorMessage()), 30).build());

        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().paymentStatus()), 10).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 30).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().amount()), 20).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().transactionAuthorizationNumber()), 20).build());

        content.setBR(++row, 0, 1);
        content.setHR(++row, 0, 2);

        content.setWidget(++row, 0, 2, createAutoPaySignupPanel());

        return content;
    }

    @Override
    public void onReset() {
        super.onReset();

        header.setHTML(i18n.tr("Payment status indefined..."));
        header.setStyleName(VistaTheme.StyleName.infoMessage.name());
        get(proto().transactionErrorMessage()).setVisible(false);
        get(proto().transactionAuthorizationNumber()).setVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // TODO : list of succeeded statuses should be defined somehow!
        if (getValue().paymentStatus().getValue() != PaymentStatus.Rejected) {
            header.setHTML(headerSuccess);
            header.setStyleName(VistaTheme.StyleName.infoMessage.name());

            get(proto().transactionAuthorizationNumber()).setVisible(!getValue().transactionAuthorizationNumber().isNull());
        } else {
            header.setHTML(headerFailed);
            header.setStyleName(VistaTheme.StyleName.warningMessage.name());

            get(proto().transactionErrorMessage()).setVisible(true);
        }
    }

    private Widget createAutoPaySignupPanel() {
        VerticalPanel text = new VerticalPanel();
        text.add(new HTML(i18n.tr("Want an Easy way to save time on your payments?")));
        text.add(new HTML(i18n.tr("Let us manage your monthly payments for you.")));
        text.add(new Anchor(i18n.tr("Sign up for Auto Pay today"), new Command() {
            @Override
            public void execute() {
                ((PaymentSubmittingView.Presenter) view.getPresenter()).goToAutoPay();
            }
        }));
        text.getElement().getStyle().setMarginLeft(0.2, Unit.EM);

        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(new Image(VistaImages.INSTANCE.recurringCredit()));
        panel.add(text);

        return panel;
    }
}

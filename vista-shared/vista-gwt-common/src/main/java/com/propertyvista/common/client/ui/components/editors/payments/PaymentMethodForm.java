/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.resources.VistaResources;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentMethodForm<E extends AbstractPaymentMethod> extends PaymentMethodEditor<E> {

    private static final I18n i18n = I18n.get(PaymentMethodForm.class);

    private final FlowPanel paymentTypeImagesPanel = new FlowPanel();

    private final CCheckBox iAgreeBox = new CCheckBox();

    protected final CLabel<String> legalTerms = new CLabel<String>();

    private final boolean twoColumns;

    public PaymentMethodForm(Class<E> clazz) {
        this(clazz, false);
    }

    public PaymentMethodForm(Class<E> clazz, boolean twoColumns) {
        super(clazz, new VistaEditorsComponentFactory());
        this.twoColumns = twoColumns;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();
        container.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());

        int row = -1;
        paymentTypeImagesPanel.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorImages.name());
        Image paymentTypeImage;
        FlowPanel holder;

        for (PaymentType type : defaultPaymentTypes()) {
            switch (type) {
            case Echeck:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentECheque().getSafeUri());
                break;
            case CreditCard:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentCredit().getSafeUri());
                break;
            case Interac:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentInterac().getSafeUri());
                break;
            default:
                paymentTypeImage = null;
                break;
            }
            if (paymentTypeImage != null) {
                holder = new FlowPanel();
                holder.add(paymentTypeImage);
                paymentTypeImagesPanel.add(holder);
            }
        }

        container.setWidget(++row, 0, paymentTypeImagesPanel);
        container.setWidget(row, 1,
                inject(proto().type(), new CRadioGroupEnum<PaymentType>(PaymentType.class, defaultPaymentTypes(), RadioGroup.Layout.VERTICAL)));

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());
        instrumentsPanel.add(paymentDetailsHolder);
        container.setWidget(row, 2, instrumentsPanel);

        container.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = container.getWidget(row, 0);
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidget(++row, 0, inject(proto().billingAddress(), new AddressStructuredEditor(twoColumns)));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setBR(++row, 0, 3);
        container.setWidget(++row, 0, createLegalTermsPanel());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        // tweaks:
        get(proto().type()).asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());
        get(proto().type()).addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                selectPaymentDetailsEditor(event.getValue(), false);
                loadLegalTerms(event.getValue());
            }
        });

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue(), get(proto().billingAddress()));
                get(proto().billingAddress()).setEditable(!event.getValue());
            }
        });

        return container;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // set single-available option preselected for new items: 
        @SuppressWarnings("unchecked")
        CRadioGroup<PaymentType> type = ((CRadioGroup<PaymentType>) get(proto().type()));
        if (getValue().id().isNull() && type.getOptions().size() == 1) {
            type.setValue(type.getOptions().get(0));
        }

        setPaymentTypeSelectionEditable(getValue().id().isNull());

        if (!getValue().type().isNull()) {
            loadLegalTerms(getValue().type().getValue());
        }

        iAgreeBox.setValue(false);
    }

    private Widget createLegalTermsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        panel.setH1(0, 0, 3, i18n.tr("Pre-Authorized Debit Agreement"));

        panel.setWidget(1, 0, new ScrollPanel(legalTerms.asWidget()));
        panel.getWidget(1, 0).setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTerms.name());

        panel.setWidget(2, 0, new DecoratorBuilder(iAgreeBox, 5).customLabel(i18n.tr("I Agree")).build());
        iAgreeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onIAgree(event.getValue());
            }
        });

        return panel;
    }

    protected void loadLegalTerms(PaymentType type) {
        switch (type) {
        case Echeck:
            legalTerms.setValue(VistaResources.INSTANCE.paymentPreauthorisedPAD().getText());
            break;
        case CreditCard:
            legalTerms.setValue(VistaResources.INSTANCE.paymentPreauthorisedCC().getText());
            break;
        default:
            assert false : "Illegal payment method type!";
            break;
        }
    }

    protected void onIAgree(boolean set) {
        // Implements meaningful in derived classes...
    }
}

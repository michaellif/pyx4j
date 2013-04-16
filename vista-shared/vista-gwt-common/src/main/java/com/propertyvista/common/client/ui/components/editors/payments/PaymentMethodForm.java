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

import java.text.ParseException;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentMethodForm<E extends AbstractPaymentMethod> extends PaymentMethodEditor<E> {

    private static final I18n i18n = I18n.get(PaymentMethodForm.class);

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
        VerticalPanel paymentMethods = new VerticalPanel();
        paymentMethods.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());
        paymentMethods.setWidth("100%");

        Widget w;
        paymentMethods.add(w = inject(proto().type(), createPaymentTypesRadioGroup()).asWidget());
        w.getElement().getStyle().setMarginLeft(15, Unit.EM);

        paymentMethods.add(paymentDetailsHolder);
        DOM.setElementProperty(DOM.getParent(paymentDetailsHolder.getElement()), "align", HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
        paymentDetailsHolder.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());

        // Form content pane:
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, paymentMethods);

        content.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = content.getWidget(row, 0);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        content.setWidget(++row, 0, inject(proto().billingAddress(), new AddressStructuredEditor(twoColumns)));

        // tweaks:
        get(proto().type()).asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());
        get(proto().type()).addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                selectPaymentDetailsEditor(event.getValue(), false);
            }
        });

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue(), get(proto().billingAddress()));
                get(proto().billingAddress()).setEditable(!event.getValue());
            }
        });

        return content;
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
    }

    private CRadioGroupEnum<PaymentType> createPaymentTypesRadioGroup() {
        CRadioGroupEnum<PaymentType> pmRadioGroup = new CRadioGroupEnum<PaymentType>(PaymentType.class, RadioGroup.Layout.HORISONTAL);
        pmRadioGroup.setFormat(new IFormat<PaymentType>() {
            @Override
            public PaymentType parse(String string) throws ParseException {
                return null;
            }

            @Override
            public String format(PaymentType value) {
                Image paymentTypeImage;
                FlowPanel holder = null;

                if (value != null) {
                    switch (value) {
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
                        return holder.getElement().getInnerHTML();
                    }

                    return value.toString();
                }

                return "";
            }
        });

        pmRadioGroup.setOptions(defaultPaymentTypes());

        return pmRadioGroup;
    }
}

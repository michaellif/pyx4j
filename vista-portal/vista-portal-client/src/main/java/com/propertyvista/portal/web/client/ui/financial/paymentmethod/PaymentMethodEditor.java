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
package com.propertyvista.portal.web.client.ui.financial.paymentmethod;

import java.text.ParseException;
import java.util.Set;

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

import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.web.client.ui.financial.PortalPaymentTypesUtil;

public class PaymentMethodEditor<E extends AbstractPaymentMethod> extends com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor<E> {

    private static final I18n i18n = I18n.get(PaymentMethodEditor.class);

    public PaymentMethodEditor(Class<E> clazz) {
        super(clazz, new VistaEditorsComponentFactory());
    }

    @Override
    public Set<PaymentType> defaultPaymentTypes() {
        return PortalPaymentTypesUtil.getAllowedPaymentTypes(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        if (isViewable()) {
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type(), new CEnumLabel()), 15).build());
            content.setWidget(++row, 0, paymentDetailsHolder);
        } else {
            VerticalPanel paymentMethods = new VerticalPanel();
            paymentMethods.setWidth("100%");

            paymentMethods.add(inject(proto().type(), createPaymentTypesRadioGroup()).asWidget());
            paymentMethods.add(paymentDetailsHolder);
            DOM.setElementProperty(DOM.getParent(paymentDetailsHolder.getElement()), "align", HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());

            get(proto().type()).asWidget().getElement().getStyle().setMarginLeft(15, Unit.EM);

            content.setWidget(++row, 0, paymentMethods);
        }

        content.setH1(++row, 0, 1, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = content.getWidget(row, 0);

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        content.setWidget(++row, 0, inject(proto().billingAddress(), new AddressSimpleEditor()));

        // tweaks:
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
    public void onReset() {
        super.onReset();

        setPaymentTypeSelectionEditable(true);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable() && get(proto().type()) instanceof CRadioGroupEnum) {
            // set single-available option preselected for new items: 
            CRadioGroupEnum<PaymentType> type = ((CRadioGroupEnum<PaymentType>) get(proto().type()));
            if (getValue().id().isNull() && !type.getOptions().isEmpty()) {
                type.setValue(type.getOptions().get(0), false, populate);
            }
            setPaymentTypeSelectionEditable(getValue().id().isNull());
        }
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
                        paymentTypeImage = new Image(VistaImages.INSTANCE.paymentECheque());
                        break;
                    case CreditCard:
                        paymentTypeImage = new Image(VistaImages.INSTANCE.paymentCredit());
                        break;
                    case DirectBanking:
                        paymentTypeImage = new Image(VistaImages.INSTANCE.paymentDirectBanking());
                        break;
                    case Interac:
                        paymentTypeImage = new Image(VistaImages.INSTANCE.paymentInterac());
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

    @Override
    protected void setPaymentDetailsWidget(Widget w) {
        super.setPaymentDetailsWidget(w);

        // hide details while editing got some types:
        PaymentType type = get(proto().type()).getValue();

        if (type != null && isEditable()) {
            switch (type) {
            case Cash:
                break;
            case Check:
                break;
            case CreditCard:
                break;
            case DirectBanking:
                setPaymentDetailsWisible(false);
                break;
            case Echeck:
                break;
            case Interac:
                break;
            default:
                break;
            }
        }
    }
}

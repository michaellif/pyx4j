/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 20011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.editors;

import java.util.Date;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public abstract class PortalPaymentMethodEditor<E extends AbstractPaymentMethod> extends PaymentMethodEditor<E> {

    private static final I18n i18n = I18n.get(PortalPaymentMethodEditor.class);

    public PortalPaymentMethodEditor(Class<E> clazz) {
        super(clazz, new VistaEditorsComponentFactory());
    }

    @Override
    public abstract Set<PaymentType> getPaymentTypes();

    @Override
    protected abstract Set<CreditCardType> getAllowedCardTypes();

    protected abstract Set<CreditCardType> getConvienceFeeApplicableCardTypes();

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().type(), new CComboBox<PaymentType>(), new FormWidgetDecoratorBuilder().build()));
        content.setWidget(++row, 0, paymentDetailsHolder);

        content.setH4(++row, 0, 1, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = content.getWidget(row, 0);
        content.setWidget(++row, 0, inject(proto().sameAsCurrent(), new FormWidgetDecoratorBuilder().build()));
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
    protected CEntityForm<?> createEcheckInfoEditor() {
        return new EcheckInfoEditor() {
            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel panel = new BasicFlexFormPanel();

                int row = -1;
                panel.setWidget(++row, 0, inject(proto().nameOn(), new FormWidgetDecoratorBuilder().build()));
                panel.setWidget(++row, 0, inject(proto().accountNo(), accountEditor, new FormWidgetDecoratorBuilder().build()));

                panel.setWidget(++row, 0, inject(proto().branchTransitNumber(), new FormWidgetDecoratorBuilder(150).build()));
                panel.setWidget(++row, 0, inject(proto().bankId(), new FormWidgetDecoratorBuilder(50).build()));

                if (!isViewable() && isEditable()) {
                    Image image = new Image(VistaImages.INSTANCE.eChequeGuideNarrow().getSafeUri());
                    image.getElement().getStyle().setMarginTop(1, Unit.EM);
                    panel.setWidget(++row, 0, image);
                    panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
                }

                return panel;
            }
        };
    }

    @Override
    protected CEntityForm<?> createCreditCardInfoEditor() {
        return new CreditCardInfoEditor() {
            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel panel = new BasicFlexFormPanel();

                int row = -1;
                CMonthYearPicker monthYearPicker = new CMonthYearPicker(false);
                panel.setWidget(++row, 0, inject(proto().nameOn(), new FormWidgetDecoratorBuilder().build()));
                panel.setWidget(++row, 0, inject(proto().cardType(), typeSelector, new FormWidgetDecoratorBuilder().build()));

                panel.setWidget(++row, 0, inject(proto().card(), cardEditor, new FormWidgetDecoratorBuilder().build()));
                panel.setWidget(++row, 0, inject(proto().expiryDate(), monthYearPicker, new FormWidgetDecoratorBuilder(125).build()));
                panel.setWidget(++row, 0, inject(proto().securityCode(), new FormWidgetDecoratorBuilder(50).build()));

                // tweak:
                monthYearPicker.setYearRange(new Range(1900 + new Date().getYear(), 10));
                get(proto().securityCode()).setVisible(isEditable());

                get(proto().cardType()).addValueChangeHandler(new ValueChangeHandler<CreditCardType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<CreditCardType> event) {
                        decorateConvienceFeeApplicableCard(event.getValue());
                    }
                });

                return panel;
            }

            @Override
            protected Set<CreditCardType> getAllowedCardTypes() {
                return PortalPaymentMethodEditor.this.getAllowedCardTypes();
            }

            protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
                return PortalPaymentMethodEditor.this.getConvienceFeeApplicableCardTypes();
            }

            private void decorateConvienceFeeApplicableCard(CreditCardType type) {
                if (type != null && getConvienceFeeApplicableCardTypes().contains(type)) {
                    get(proto().cardType()).setNote(i18n.tr("*Web Payment Fee will apply"), NoteStyle.Warn);
                } else {
                    get(proto().cardType()).setNote(null);
                }
            }

            @Override
            public void onReset() {
                super.onReset();
                get(proto().cardType()).setNote(null);
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                decorateConvienceFeeApplicableCard(getValue().cardType().getValue());
            }
        };
    }

    @Override
    public void onReset() {
        super.onReset();

        setPaymentTypeSelectionEditable(true);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (get(proto().type()).isEditable() && get(proto().type()) instanceof CComboBox) {
            CComboBox<PaymentType> type = ((CComboBox<PaymentType>) get(proto().type()));
            type.setOptions(getPaymentTypes());
            // set single-available option preselected for new items: 
            if (getValue().id().isNull() && !type.getOptions().isEmpty()) {
                type.setValue(type.getOptions().get(0), true, populate);
            }
            setPaymentTypeSelectionEditable(getValue().id().isNull());
        }
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

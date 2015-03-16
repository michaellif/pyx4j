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
 */
package com.propertyvista.portal.shared.ui.util.editors;

import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;

public abstract class PortalPaymentMethodEditor<E extends AbstractPaymentMethod> extends PaymentMethodEditor<E> {

    private static final I18n i18n = I18n.get(PortalPaymentMethodEditor.class);

    public PortalPaymentMethodEditor(Class<E> clazz) {
        super(clazz, new VistaEditorsComponentFactory());
    }

    @Override
    public abstract Set<PaymentType> getDefaultPaymentTypes();

    @Override
    protected abstract Set<CreditCardType> getAllowedCardTypes();

    protected abstract Set<CreditCardType> getConvienceFeeApplicableCardTypes();

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().type(), new CComboBox<PaymentType>()).decorate();
        formPanel.append(Location.Left, paymentDetailsHolder);

        billingAddressHeader = formPanel.h4(proto().billingAddress().getMeta().getCaption());
        formPanel.append(Location.Left, proto().sameAsCurrent()).decorate();
        formPanel.append(Location.Left, proto().billingAddress(), new InternationalAddressEditor());

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

        return formPanel;
    }

    @Override
    protected CForm<?> createEcheckInfoEditor() {
        return new EcheckInfoEditor() {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().nameOn()).decorate();
                formPanel.append(Location.Left, proto().accountNo(), accountEditor).decorate();

                formPanel.append(Location.Left, proto().branchTransitNumber()).decorate().componentWidth(80);
                formPanel.append(Location.Left, proto().bankId()).decorate().componentWidth(50);

                if (!isViewable() && isEditable()) {
                    Image image = new Image(VistaImages.INSTANCE.eChequeGuideNarrow().getSafeUri());
                    image.getElement().getStyle().setMarginTop(1, Unit.EM);
                    formPanel.append(Location.Left, image);
                }

                return formPanel;
            }
        };
    }

    @Override
    protected CForm<?> createCreditCardInfoEditor() {
        return new CreditCardInfoEditor() {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().nameOn()).decorate();
                formPanel.append(Location.Left, proto().cardType(), typeSelector).decorate();

                formPanel.append(Location.Left, proto().card(), cardEditor).decorate();
                formPanel.append(Location.Left, proto().expiryDate(), monthYearPicker).decorate().componentWidth(125);
                formPanel.append(Location.Left, proto().securityCode()).decorate().componentWidth(50);

                contentTweaks();
                return formPanel;
            }

            @Override
            protected void contentTweaks() {
                super.contentTweaks();
                get(proto().cardType()).addValueChangeHandler(new ValueChangeHandler<CreditCardType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<CreditCardType> event) {
                        decorateConvienceFeeApplicableCard(event.getValue());
                    }
                });
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

        setPaymentTypeSelectionEditable(getValue().id().isNull());
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

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-01
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;

public class PaymentForm extends CEntityDecoratableForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentForm.class);

    private PaymentView.Presenter presenter;

    private Widget paymentMethodEditorSeparator;

    private final CComboBox<PaymentMethod> profiledPaymentMethodsCombo = new CComboBox<PaymentMethod>() {
        @Override
        public String getItemName(PaymentMethod o) {
            if (o == null) {
                return super.getItemName(o);
            } else {
                return o.getStringView();
            }
        }
    };

    private final PaymentMethodForm paymentMethodEditor = new PaymentMethodForm() {
        @Override
        public Collection<PaymentType> getPaymentOptions() {
            return PaymentType.avalableInPortal();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured, ?> comp) {
            if (set) {
                assert (presenter != null);
                presenter.getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                    @Override
                    public void onSuccess(AddressStructured result) {
                        comp.setValue(result, false);
                    }
                });
            } else {
                comp.setValue(EntityFactory.create(AddressStructured.class), false);
            }
        }
    };

    public PaymentForm() {
        super(PaymentRecordDTO.class);
    }

    public void setPresenter(PaymentView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, createDetailsPanel());
        main.setH1(1, 0, 1, i18n.tr("Payment Method"));
        paymentMethodEditorSeparator = main.getWidget(1, 0);
        main.setWidget(2, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        // tweaks:
        paymentMethodEditor.addTypeSelectionValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setupAddThisPaymentMethodToProfile(event.getValue());
            }
        });

        return new ScrollPanel(main);
    }

    private IsWidget createDetailsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseStatus()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant(), new CEntityLabel<LeaseParticipant>()), 25).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitNumber()), 15).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createdDate()), 10).build());
//        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().paymentStatus()), 10).build());
//        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionAuthorizationNumber()), 10).build());
//        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionErrorMessage()), 20).build());
        panel.setHR(++row, 0, 1);
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notes()), 25).build());
        panel.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().paymentSelect(), new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), 20)
                        .build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 25).build());
        profiledPaymentMethodsCombo.setMandatory(true);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), 5).labelWidth(20).build());

        // tweak UI:
        get(proto().id()).setViewable(true);
        get(proto().unitNumber()).setViewable(true);
        get(proto().leaseId()).setViewable(true);
        get(proto().leaseStatus()).setViewable(true);
//        get(proto().paymentStatus()).setViewable(true);
        get(proto().createdDate()).setViewable(true);

        get(proto().paymentSelect()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.reset();

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setEnabled(true);
                        paymentMethodEditor.initNew(PaymentType.Echeck);
                        paymentMethodEditor.setVisible(!getValue().leaseParticipant().isNull());
                        paymentMethodEditorSeparator.setVisible(!getValue().leaseParticipant().isNull());

                        paymentMethodEditor.getValue().isOneTimePayment().setValue(Boolean.TRUE);

                        setProfiledPaymentMethodsVisible(false);
                        break;
                    case Profiled:
                        paymentMethodEditor.setEnabled(false);
                        paymentMethodEditor.setVisible(false);
                        paymentMethodEditorSeparator.setVisible(false);

                        profiledPaymentMethodsCombo.reset();
                        setProfiledPaymentMethodsVisible(true);
                        break;
                    }
                }
            }
        });

        profiledPaymentMethodsCombo.addValueChangeHandler(new ValueChangeHandler<PaymentMethod>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentMethod> event) {
                paymentMethodEditor.setVisible(event.getValue() != null);
                paymentMethodEditorSeparator.setVisible(event.getValue() != null);
                if (event.getValue() != null) {
                    paymentMethodEditor.setValue(event.getValue(), false);
                }
            }
        });

        return panel;
    }

    @Override
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

        get(proto().id()).setVisible(!getValue().id().isNull());
        get(proto().paymentSelect()).setVisible(!isViewable() && !getValue().leaseParticipant().isNull());
        setProfiledPaymentMethodsVisible(false);

        checkProfiledPaymentMethods();

        paymentMethodEditor.setVisible(!getValue().leaseParticipant().isNull());
        paymentMethodEditorSeparator.setVisible(!getValue().leaseParticipant().isNull());
        paymentMethodEditor.setBillingAddressAsCurrentEnabled(!getValue().leaseParticipant().isNull());

//        if (isEditable()) {
//            get(proto().transactionAuthorizationNumber()).setVisible(false);
//            get(proto().transactionErrorMessage()).setVisible(false);
//        } else {
//            boolean transactionResult = getValue().paymentMethod().isNull() ? false
//                    : (getValue().paymentMethod().type().getValue().isTransactable() && getValue().paymentStatus().getValue().isProcessed());
//
//            get(proto().transactionAuthorizationNumber()).setVisible(transactionResult);
//            get(proto().transactionErrorMessage()).setVisible(transactionResult && !getValue().transactionErrorMessage().isNull());
//        }
    }

    private void checkProfiledPaymentMethods() {
        get(proto().paymentSelect()).setEnabled(false);

        profiledPaymentMethodsCombo.reset();
        profiledPaymentMethodsCombo.setOptions(null);

        presenter.getProfiledPaymentMethods(new DefaultAsyncCallback<List<PaymentMethod>>() {
            @Override
            public void onSuccess(List<PaymentMethod> result) {
                get(proto().paymentSelect()).setEnabled(!result.isEmpty());
                get(proto().paymentSelect()).setVisible(!result.isEmpty());
                get(proto().paymentSelect()).reset();

                profiledPaymentMethodsCombo.setOptions(result);
                profiledPaymentMethodsCombo.setMandatory(true);

                if (result.isEmpty()) {
                    get(proto().paymentSelect()).setValue(PaymentSelect.New, getValue().getPrimaryKey() == null);
                } else {
                    if (getValue().getPrimaryKey() == null) {
                        get(proto().paymentSelect()).setValue(PaymentSelect.Profiled, true);
                    } else {
                        if (getValue().paymentMethod().customer().isNull()) {
                            get(proto().paymentSelect()).setValue(PaymentSelect.New, false);
                        } else {
                            get(proto().paymentSelect()).setValue(PaymentSelect.Profiled, false);
//                            profiledPaymentMethodsCombo.setValueByString(getValue().paymentMethod().getStringView());
                            // TODO: find out why this setValue doen't work!?
                            profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), false);
                            setProfiledPaymentMethodsVisible(true);
                            paymentMethodEditor.setViewable(true);
                        }
                    }
                }
            }
        });
    }

    private void setProfiledPaymentMethodsVisible(boolean visible) {
        profiledPaymentMethodsCombo.setVisible(visible && !isViewable());
        get(proto().addThisPaymentMethodToProfile()).setVisible(!visible && !isViewable());
        setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
    }

    private void setupAddThisPaymentMethodToProfile(PaymentType paymentType) {
        get(proto().addThisPaymentMethodToProfile()).setEnabled(PaymentType.avalableInProfile().contains(paymentType));
        get(proto().addThisPaymentMethodToProfile()).setValue(Boolean.FALSE);
        if (paymentType != null) {
            switch (paymentType) {
            case Cash:
            case Check:
            case EFT:
            case Interac:
                break;
            case CreditCard:
            case Echeck:
                get(proto().addThisPaymentMethodToProfile()).setValue(Boolean.TRUE);
                break;
            }
        }
    }

}

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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;
import com.propertyvista.portal.client.ui.residents.payment.LegalTermsDialog.TermsType;

public class PaymentForm extends CEntityDecoratableForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentForm.class);

    private PaymentView.Presenter presenter;

    private Widget paymentMethodEditorSeparator;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final PaymentMethodForm<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Collection<PaymentType> defaultPaymentTypes() {
            return PortalPaymentTypesUtil.getAllowedPaymentTypes();
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

        main.setBR(3, 0, 1);
        main.setHR(4, 0, 1);
        main.setWidget(5, 0, createLegalTermsPanel());
        main.getFlexCellFormatter().setAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

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
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTermParticipant(), new CEntityLabel<LeaseTermParticipant>()), 25).build());
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
                new DecoratorBuilder(inject(proto().selectPaymentMethod(),
                        new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), 20).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 25).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), 5).labelWidth(20).build());

        // tweak UI:
        get(proto().id()).setViewable(true);
        get(proto().unitNumber()).setViewable(true);
        get(proto().leaseId()).setViewable(true);
        get(proto().leaseStatus()).setViewable(true);
//        get(proto().paymentStatus()).setViewable(true);
        get(proto().createdDate()).setViewable(true);

        get(proto().selectPaymentMethod()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.reset();
                paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setViewable(false);

                        if (getValue().allowedPaymentTypes().isEmpty()) {
                            paymentMethodEditor.initNew(null);
                            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("There are no payment methods allowed!"));
                        } else {
                            // set preferred value:
                            if (getValue().allowedPaymentTypes().contains(PaymentType.Echeck)) {
                                paymentMethodEditor.initNew(PaymentType.Echeck);
                            } else {
                                paymentMethodEditor.initNew(null);
                            }
                        }

                        paymentMethodEditor.setVisible(!getValue().leaseTermParticipant().isNull());
                        paymentMethodEditorSeparator.setVisible(!getValue().leaseTermParticipant().isNull());

                        paymentMethodEditor.getValue().isOneTimePayment().setValue(Boolean.TRUE);

                        setProfiledPaymentMethodsVisible(false);
                        break;
                    case Profiled:
                        paymentMethodEditor.setViewable(true);
                        paymentMethodEditor.setVisible(false);
                        paymentMethodEditorSeparator.setVisible(false);

                        profiledPaymentMethodsCombo.reset();
                        setProfiledPaymentMethodsVisible(true);
                        break;
                    }
                }
            }
        });

        profiledPaymentMethodsCombo.addValueChangeHandler(new ValueChangeHandler<LeasePaymentMethod>() {
            @Override
            public void onValueChange(ValueChangeEvent<LeasePaymentMethod> event) {
                paymentMethodEditor.setVisible(event.getValue() != null);
                paymentMethodEditorSeparator.setVisible(event.getValue() != null);
                if (event.getValue() != null) {
                    paymentMethodEditor.populate(event.getValue());
                }
            }
        });

        return panel;
    }

    @Override
    public void onReset() {
        super.onReset();

        profiledPaymentMethodsCombo.setVisible(false);
        profiledPaymentMethodsCombo.setOptions(null);
        profiledPaymentMethodsCombo.reset();

        paymentMethodEditor.reset();
        paymentMethodEditor.setVisible(false);
        paymentMethodEditor.setViewable(true);
        paymentMethodEditorSeparator.setVisible(false);

        paymentMethodEditor.setPaymentTypeSelectionEditable(true);

        get(proto().selectPaymentMethod()).setVisible(false);
        get(proto().addThisPaymentMethodToProfile()).setVisible(false);
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        get(proto().id()).setVisible(!getValue().id().isNull());

        paymentMethodEditor.setPaymentTypes(getValue().allowedPaymentTypes());
        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));

        loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();
                boolean isProfiledMethod = profiledPaymentMethodsCombo.getOptions().contains(getValue().paymentMethod());

                get(proto().selectPaymentMethod()).reset();
                get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setValue((isProfiledMethod ? PaymentSelect.Profiled : PaymentSelect.New), !isProfiledMethod, populate);

                profiledPaymentMethodsCombo.setVisible(isProfiledMethod);
            }
        });

        if (getValue().paymentMethod().isOneTimePayment().isBooleanTrue()) {
            paymentMethodEditor.setVisible(true);
            paymentMethodEditor.setViewable(false);
            paymentMethodEditorSeparator.setVisible(true);
            get(proto().addThisPaymentMethodToProfile()).setVisible(true);
            setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
        } else {
            profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), true, populate);
        }

        // TODO : this is the HACK - check CComponent.setVisible implementation!!!
        paymentMethodEditor.setBillingAddressVisible(getValue().paymentMethod().type().getValue() != PaymentType.Cash);
    }

    @Override
    public void addValidations() {
        get(proto().amount()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal, ?> component, BigDecimal value) {
                if (value != null) {
                    return (value.compareTo(BigDecimal.ZERO) > 0 ? null
                            : new ValidationError(component, i18n.tr("Payment amount should be greater then zero!")));
                }
                return null;
            }
        });
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        presenter.getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(List<LeasePaymentMethod> result) {
                profiledPaymentMethodsCombo.setOptions(result);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        });
    }

    private void checkProfiledPaymentMethods(final boolean populate) {
        get(proto().selectPaymentMethod()).setEnabled(false);

        profiledPaymentMethodsCombo.reset();
        profiledPaymentMethodsCombo.setOptions(null);

        presenter.getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(List<LeasePaymentMethod> result) {
                get(proto().selectPaymentMethod()).reset();
                get(proto().selectPaymentMethod()).setEnabled(!result.isEmpty());
                get(proto().selectPaymentMethod()).setVisible(!result.isEmpty());

                profiledPaymentMethodsCombo.setOptions(result);

                if (result.isEmpty()) {
                    get(proto().selectPaymentMethod()).setValue(PaymentSelect.New, getValue().getPrimaryKey() == null, populate);
                } else {
                    if (getValue().getPrimaryKey() == null) {
                        get(proto().selectPaymentMethod()).setValue(PaymentSelect.Profiled, true, populate);
                    } else {
                        if (getValue().paymentMethod().customer().isNull()) {
                            get(proto().selectPaymentMethod()).setValue(PaymentSelect.New, false, populate);
                        } else {
                            get(proto().selectPaymentMethod()).setValue(PaymentSelect.Profiled, false, populate);
                            profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), false, populate);
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
        get(proto().addThisPaymentMethodToProfile()).setVisible(!visible && !isViewable() && !getValue().paymentMethod().type().isNull());
        setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
    }

    private void setupAddThisPaymentMethodToProfile(PaymentType paymentType) {
        if (paymentType != null) {
            switch (paymentType) {
            case CreditCard:
                get(proto().addThisPaymentMethodToProfile()).setValue(true);
                get(proto().addThisPaymentMethodToProfile()).setEnabled(true);
                break;

            case Echeck:
                get(proto().addThisPaymentMethodToProfile()).setValue(true);
                get(proto().addThisPaymentMethodToProfile()).setEnabled(false);
                break;

            default:
                get(proto().addThisPaymentMethodToProfile()).setValue(false);
                get(proto().addThisPaymentMethodToProfile()).setEnabled(false);
                break;
            }
        }
    }

    private Widget createLegalTermsPanel() {
        FlowPanel panel = new FlowPanel();

        panel.add(new HTML(i18n.tr("By pressing Submit you are acknowledgeing our ")));
        panel.add(new Anchor(i18n.tr("Terms and Conditions"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.TermsAndConditions).show();
            }
        }));

        panel.add(new HTML(",&nbsp"));
        panel.getWidget(panel.getWidgetCount() - 1).getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        panel.add(new Anchor(i18n.tr("Privacy Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.PrivacyPolicy).show();
            }
        }));

        panel.add(new HTML("&nbsp" + i18n.tr("and") + "&nbsp"));
        panel.getWidget(panel.getWidgetCount() - 1).getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        panel.add(new Anchor(i18n.tr("Billing And Refund Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.BillingAndRefundPolicy).show();
            }
        }));

        panel.setWidth("80%");
        panel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        return panel;
    }
}

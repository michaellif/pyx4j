/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment;

import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.common.client.ui.wizard.VistaWizardForm;
import com.propertyvista.common.client.ui.wizard.VistaWizardStep;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.PaymentDataDTO.PaymentSelect;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.ui.residents.payment.LegalTermsDialog.TermsType;

public class PaymentWizardForm extends VistaWizardForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentWizardForm.class);

    private static final String DETAILS_STEP_TITLE = i18n.tr("Details");

    private static final String SELECTPAYMENTMETHOD_STEP_TITLE = i18n.tr("Payment Method Selection");

    private static final String PAYMENTMETHOD_STEP_TITLE = i18n.tr("Payment Method");

    private static final String CONFIRMATION_STEP_TITLE = i18n.tr("Confirmation");

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    private final PaymentMethodForm<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Collection<PaymentType> defaultPaymentTypes() {
            return PortalPaymentTypesUtil.getAllowedPaymentTypes();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured, ?> comp) {
            if (set) {
                assert (getView().getPresenter() != null);
                ((PaymentWizardView.Persenter) getView().getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
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

    public PaymentWizardForm(IWizard<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class, view);

        addStep(createDetailsStep());
        addStep(createSelectPaymentMethodStep());
        addStep(createPaymentMethodStep());
        addStep(createConfirmationStep());
    }

    private FormFlexPanel createDetailsStep() {
        FormFlexPanel panel = new FormFlexPanel(DETAILS_STEP_TITLE);
        int row = -1;

        panel.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().leaseTermParticipant(), new CEntityLabel<LeaseTermParticipant>()), 25).customLabel(i18n.tr("Tenant"))
                        .build());

        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, inject(proto().propertyAddress(), new AddressSimpleEditor()));

        panel.setHR(++row, 0, 1);
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());

        // tweak UI:
        get(proto().propertyAddress()).setViewable(true);

        return panel;
    }

    private FormFlexPanel createSelectPaymentMethodStep() {
        FormFlexPanel panel = new FormFlexPanel(SELECTPAYMENTMETHOD_STEP_TITLE);
        int row = -1;

        panel.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().selectPaymentMethod(),
                        new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), 20).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 25).build());

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

                        paymentMethodEditor.getValue().isProfiledMethod().setValue(Boolean.FALSE);

                        setProfiledPaymentMethodsVisible(false);
                        break;

                    case Profiled:
                        paymentMethodEditor.setViewable(true);

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
                if (event.getValue() != null) {
                    paymentMethodEditor.populate(event.getValue());
                }
            }
        });

        return panel;
    }

    private FormFlexPanel createPaymentMethodStep() {
        FormFlexPanel panel = new FormFlexPanel(PAYMENTMETHOD_STEP_TITLE);
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), 5).build());

        // tweaks:
        paymentMethodEditor.addTypeSelectionValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setupAddThisPaymentMethodToProfile(event.getValue());
            }
        });

        return panel;
    }

    private FormFlexPanel createConfirmationStep() {
        FormFlexPanel panel = new FormFlexPanel(CONFIRMATION_STEP_TITLE);
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, createLegalTermsPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    @Override
    protected void onStepChange(SelectionEvent<VistaWizardStep> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().getStepTitle().equals(CONFIRMATION_STEP_TITLE)) {
            confirmationDetailsHolder.clear();
            confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
        }
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

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

        if (getValue().paymentMethod().isProfiledMethod().isBooleanTrue()) {
            profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), true, populate);
        } else {
            paymentMethodEditor.setViewable(false);
        }

        // TODO : this is the HACK - check CComponent.setVisible implementation!!!
        paymentMethodEditor.setBillingAddressVisible(getValue().paymentMethod().type().getValue() != PaymentType.Cash);
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        ((PaymentWizardView.Persenter) getView().getPresenter()).getProfiledPaymentMethods(new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(List<LeasePaymentMethod> result) {
                profiledPaymentMethodsCombo.setOptions(result);
                if (callback != null) {
                    callback.onSuccess(null);
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

    private Widget createConfirmationDetailsPanel() {
        VerticalPanel panel = new VerticalPanel();
        Widget w;

        panel.add(new HTML(getValue().leaseTermParticipant().getStringView()));
        panel.add(new HTML(getValue().propertyAddress().getStringView()));

        panel.add(new HTML("<br/>"));

        HorizontalPanel pm = new HorizontalPanel();
        pm.add(w = new HTML(i18n.tr("Payment Method:")));
        w.setWidth("10em");
        pm.add(w = new HTML(get(proto().paymentMethod()).getValue().getStringView()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        panel.add(pm);

        HorizontalPanel amount = new HorizontalPanel();
        amount.add(w = new HTML(i18n.tr("Amount to pay:")));
        w.setWidth("10em");
        amount.add(w = new HTML(((CTextFieldBase<?, ?>) get(proto().amount())).getFormattedValue()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        panel.add(amount);

        return panel;
    }

    private Widget createLegalTermsPanel() {
        FlowPanel panel = new FlowPanel();
        Widget w;

        panel.add(new HTML(i18n.tr("By pressing Submit you are acknowledgeing our")));
        panel.add(w = new Anchor(i18n.tr("Terms Of Use"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.TermsOfUse).show();
            }
        }));

        panel.add(w = new HTML(",&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        panel.add(w = new Anchor(i18n.tr("Privacy Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.PrivacyPolicy).show();
            }
        }));

        panel.add(w = new HTML("&nbsp" + i18n.tr("and") + "&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        panel.add(w = new Anchor(i18n.tr("Billing And Refund Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.BillingAndRefundPolicy).show();
            }
        }));

        return panel;
    }
}

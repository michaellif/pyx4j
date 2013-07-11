/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.payment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PaymentDataDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentForm extends CrmEntityForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentForm.class);

    private final Widget paymentMethodEditorSeparator;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final PaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Collection<PaymentType> defaultPaymentTypes() {
            return PaymentType.avalableInCrm();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured> comp) {
            if (set) {
                ((PaymentEditorView.Presenter) ((PaymentEditorView) getParentView()).getPresenter()).getCurrentAddress(
                        new DefaultAsyncCallback<AddressStructured>() {
                            @Override
                            public void onSuccess(AddressStructured result) {
                                comp.setValue(result, false);
                            }
                        }, PaymentForm.this.getValue().leaseTermParticipant());
            } else {
                comp.setValue(EntityFactory.create(AddressStructured.class), false);
            }

        }

        @Override
        protected String getNameOn() {
            return PaymentForm.this.getValue().leaseTermParticipant().leaseParticipant().customer().person().name().getStringView();
        }

        @Override
        protected CEntityForm<?> createEcheckInfoEditor() {
            return new EcheckInfoEditor() {
                @SuppressWarnings("rawtypes")
                @Override
                public IsWidget createContent() {
                    IsWidget content = super.createContent();

                    if (SecurityController.checkBehavior(VistaCrmBehavior.Billing)) {
                        ((CField) get(proto().accountNo())).setNavigationCommand(new Command() {
                            @Override
                            public void execute() {
                                GWT.<RevealAccountNumberService> create(RevealAccountNumberService.class).obtainUnobfuscatedAccountNumber(
                                        new DefaultAsyncCallback<EcheckInfo>() {
                                            @Override
                                            public void onSuccess(EcheckInfo result) {
                                                MessageDialog.info(i18n.tr("Account Number") + ": <b>" + result.accountNo().newNumber().getStringView()
                                                        + "</b>");
                                            }
                                        }, EntityFactory.createIdentityStub(EcheckInfo.class, getValue().getPrimaryKey()));
                            }
                        });
                    }

                    return content;
                };
            };
        }
    };

    public PaymentForm(IForm<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        content.setWidget(0, 0, createDetailsPanel());
        content.setH1(1, 0, 1, i18n.tr("Payment Method"));
        paymentMethodEditorSeparator = content.getWidget(1, 0);
        content.setWidget(2, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        // tweaks:
        paymentMethodEditor.addTypeSelectionValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setupAddThisPaymentMethodToProfile(event.getValue());
            }
        });

        selectTab(addTab(content));
    }

    @SuppressWarnings("unchecked")
    private IsWidget createDetailsPanel() {

        FormFlexPanel left = new FormFlexPanel();
        int row = -1;

        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().id(), new CNumberLabel()), "100px").build());
        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().propertyCode()), "100px").build());
        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().unitNumber()), "100px").build());
        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().leaseId()), "100px").build());
        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().leaseStatus()), "100px").build());
        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().billingAccount().accountNumber()), "100px").build());
        get(proto().billingAccount().accountNumber()).setViewable(true);

        left.setWidget(
                ++row,
                0,
                new PaymentFormDecoratorBuilder(inject(proto().leaseTermParticipant(),
                        new CEntitySelectorHyperlink<LeaseTermParticipant<? extends LeaseParticipant<?>>>() {
                            @Override
                            protected AppPlace getTargetPlace() {
                                if (getValue().isInstanceOf(LeaseTermTenant.class)) {
                                    return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().leaseParticipant().getPrimaryKey());
                                } else if (getValue().isInstanceOf(LeaseTermGuarantor.class)) {
                                    return AppPlaceEntityMapper.resolvePlace(Guarantor.class, getValue().leaseParticipant().getPrimaryKey());
                                } else {
                                    throw new IllegalArgumentException("Incorrect LeaseParticipant value!");
                                }
                            }

                            @Override
                            protected AbstractEntitySelectorDialog<LeaseTermParticipant<? extends LeaseParticipant<?>>> getSelectorDialog() {
                                return new EntitySelectorListDialog<LeaseTermParticipant<? extends LeaseParticipant<?>>>(i18n.tr("Select Tenant To Pay"),
                                        false, PaymentForm.this.getValue().participants()) {
                                    @Override
                                    public boolean onClickOk() {
                                        CComponent<?> comp = get(PaymentForm.this.proto().leaseTermParticipant());
                                        ((CComponent<LeaseTermParticipant<? extends LeaseParticipant<?>>>) comp).setValue(getSelectedItems().get(0));
                                        return true;
                                    }
                                };
                            }
                        }), "200px").build());

        left.setWidget(
                ++row,
                0,
                new PaymentFormDecoratorBuilder(inject(proto().selectPaymentMethod(), new CRadioGroupEnum<PaymentDataDTO.PaymentSelect>(
                        PaymentDataDTO.PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), "200px").labelWidth("20em").build());

        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), "200px").build());

        left.setWidget(++row, 0, new PaymentFormDecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), "30px").labelWidth("20em").build());

        FormFlexPanel right = new FormFlexPanel();
        row = -1;

        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().amount()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().createdDate()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().receivedDate()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().targetDate()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().finalizeDate()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().paymentStatus()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().lastStatusChangeDate()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().transactionAuthorizationNumber()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().transactionErrorMessage()), "100px").build());
        right.setWidget(++row, 1, new PaymentFormDecoratorBuilder(inject(proto().notes()), "100px").build());

        // tweak UI:
        get(proto().id()).setViewable(true);
        get(proto().propertyCode()).setViewable(true);
        get(proto().unitNumber()).setViewable(true);
        get(proto().leaseId()).setViewable(true);
        get(proto().leaseStatus()).setViewable(true);
        get(proto().paymentStatus()).setViewable(true);
        get(proto().createdDate()).setViewable(true);
        get(proto().receivedDate()).setViewable(true);
        get(proto().finalizeDate()).setViewable(true);
        get(proto().lastStatusChangeDate()).setViewable(true);

        CComponent<?> comp = get(proto().leaseTermParticipant());
        ((CComponent<LeaseTermParticipant<? extends LeaseParticipant<?>>>) comp)
                .addValueChangeHandler(new ValueChangeHandler<LeaseTermParticipant<? extends LeaseParticipant<?>>>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LeaseTermParticipant<? extends LeaseParticipant<?>>> event) {
                        changeLeaseParticipant();
                    }
                });

        get(proto().selectPaymentMethod()).addValueChangeHandler(new ValueChangeHandler<PaymentDataDTO.PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentDataDTO.PaymentSelect> event) {
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
                            } else if (getValue().allowedPaymentTypes().contains(PaymentType.Cash)) {
                                paymentMethodEditor.initNew(PaymentType.Cash);
                            } else {
                                paymentMethodEditor.initNew(null);
                            }
                        }

                        paymentMethodEditor.setVisible(!getValue().leaseTermParticipant().isNull());
                        paymentMethodEditorSeparator.setVisible(!getValue().leaseTermParticipant().isNull());

                        paymentMethodEditor.getValue().isProfiledMethod().setValue(Boolean.FALSE);

                        setProfiledPaymentMethodsVisible(false);
                        break;

                    case Profiled:
                        paymentMethodEditor.setViewable(true);
                        paymentMethodEditor.setVisible(false);
                        paymentMethodEditorSeparator.setVisible(false);

                        profiledPaymentMethodsCombo.reset();
                        setProfiledPaymentMethodsVisible(true);
                        if (profiledPaymentMethodsCombo.getOptions().size() == 1) {
                            profiledPaymentMethodsCombo.setValue(profiledPaymentMethodsCombo.getOptions().get(0));
                        }
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
                    paymentMethodEditor.setViewable(true);
                    paymentMethodEditor.populate(event.getValue());
                }
            }
        });

        FormFlexPanel formPanel = new FormFlexPanel();

        formPanel.setWidget(0, 0, left);
        formPanel.setWidget(0, 1, right);
        formPanel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        formPanel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        return formPanel;
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

        get(proto().selectPaymentMethod()).setVisible(false);
        get(proto().addThisPaymentMethodToProfile()).setVisible(false);
        get(proto().profiledPaymentMethod()).setNote(null);
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        boolean isNew = getValue().id().isNull();

        // hide some non-relevant fields:
        get(proto().id()).setVisible(!isNew);
        get(proto().receivedDate()).setVisible(!isEditable());
        get(proto().finalizeDate()).setVisible(!isEditable());
        get(proto().paymentStatus()).setVisible(!isNew);
        get(proto().lastStatusChangeDate()).setVisible(!isNew);

        get(proto().profiledPaymentMethod()).setNote(getValue().notice().getValue());

        if (isEditable()) {
            paymentMethodEditor.setPaymentTypes(getValue().allowedPaymentTypes());
            paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));

            if (isNew) {
                get(proto().leaseTermParticipant()).setEditable(true);
            } else {
                get(proto().leaseTermParticipant()).setEditable(false);

                loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();
                        boolean isProfiledMethod = profiledPaymentMethodsCombo.getOptions().contains(getValue().paymentMethod());

                        get(proto().selectPaymentMethod()).reset();
                        get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                        get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
                        get(proto().selectPaymentMethod()).setValue(
                                (isProfiledMethod ? PaymentDataDTO.PaymentSelect.Profiled : PaymentDataDTO.PaymentSelect.New), false, populate);

                        profiledPaymentMethodsCombo.setVisible(isProfiledMethod);
                    }
                });

                if (getValue().paymentMethod().isProfiledMethod().isBooleanTrue()) {
                    profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), true, populate);
                } else {
                    paymentMethodEditor.setVisible(true);
                    paymentMethodEditor.setViewable(false);
                    paymentMethodEditorSeparator.setVisible(true);
                    get(proto().addThisPaymentMethodToProfile()).setVisible(true);
                    setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
                }

                // TODO : this is the HACK - check CComponent.setVisible implementation!!!
                paymentMethodEditor.setBillingAddressVisible(getValue().paymentMethod().type().getValue() != PaymentType.Cash);
            }

            get(proto().transactionAuthorizationNumber()).setVisible(false);
            get(proto().transactionErrorMessage()).setVisible(false);

        } else { // view mode:

            if (getValue().paymentMethod().isProfiledMethod().isBooleanTrue()) {
                profiledPaymentMethodsCombo.setVisible(true);
                profiledPaymentMethodsCombo.setValue(getValue().paymentMethod());
            } else {
                paymentMethodEditor.setVisible(true);
                paymentMethodEditorSeparator.setVisible(true);
            }

            boolean transactionResult = getValue().paymentMethod().isNull() ? false
                    : (getValue().paymentMethod().type().getValue().isTransactable() && getValue().paymentStatus().getValue().isProcessed());

            get(proto().transactionAuthorizationNumber()).setVisible(transactionResult);
            get(proto().transactionErrorMessage()).setVisible(transactionResult && !getValue().transactionErrorMessage().isNull());
        }
    }

    @Override
    public void addValidations() {
        get(proto().amount()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
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
        ((PaymentEditorView.Presenter) ((PaymentEditorView) getParentView()).getPresenter()).getProfiledPaymentMethods(
                new DefaultAsyncCallback<List<LeasePaymentMethod>>() {
                    @Override
                    public void onSuccess(List<LeasePaymentMethod> result) {
                        profiledPaymentMethodsCombo.setOptions(result);
                        if (callback != null) {
                            callback.onSuccess(null);
                        }
                    }
                }, getValue().leaseTermParticipant());
    }

    private void changeLeaseParticipant() {
        paymentMethodEditor.reset();
        paymentMethodEditor.setBillingAddressAsCurrentEnabled(true);
        paymentMethodEditor.setPaymentTypes(getValue().allowedPaymentTypes());
        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));
        loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();
                get(proto().selectPaymentMethod()).reset();
                get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setValue(hasProfiledMethods ? PaymentDataDTO.PaymentSelect.Profiled : PaymentDataDTO.PaymentSelect.New);
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
}
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
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
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

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.misc.PapExpirationWarning;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.AbstractPmcUser;
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

    private final Widget preauthorizedPaymentMethodViewerHeader, paymentMethodEditorHeader;

    private final CComboBox<LeasePaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<LeasePaymentMethod>();

    private final PaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Collection<PaymentType> defaultPaymentTypes() {
            return PaymentType.avalableInCrm();
        }

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressSimple> comp) {
            if (set) {
                ((PaymentEditorView.Presenter) ((PaymentEditorView) getParentView()).getPresenter()).getCurrentAddress(
                        new DefaultAsyncCallback<AddressSimple>() {
                            @Override
                            public void onSuccess(AddressSimple result) {
                                comp.setValue(result, false);
                            }
                        }, PaymentForm.this.getValue().leaseTermParticipant());
            } else {
                comp.setValue(EntityFactory.create(AddressSimple.class), false);
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

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        content.setWidget(++row, 0, createDetailsPanel());

        content.setH1(++row, 0, 1, i18n.tr("Preauthorized Payment"));
        preauthorizedPaymentMethodViewerHeader = content.getWidget(row, 0);
        content.setWidget(++row, 0, inject(proto().preauthorizedPayment(), new PreauthorizedPaymentViewer()));

        content.setH1(++row, 0, 1, i18n.tr("Payment Method"));
        paymentMethodEditorHeader = content.getWidget(row, 0);
        content.setWidget(++row, 0, inject(proto().paymentMethod(), paymentMethodEditor));

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

        TwoColumnFlexFormPanel left = new TwoColumnFlexFormPanel();
        int row = -1;

        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().propertyCode()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().unitNumber()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseId()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseStatus()), 10).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingAccount().accountNumber(), new CLabel<String>()), 10).build());

        left.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().leaseTermParticipant(),
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
                        }), 22).build());

        left.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().selectPaymentMethod(), new CRadioGroupEnum<PaymentDataDTO.PaymentSelect>(
                        PaymentDataDTO.PaymentSelect.class, RadioGroup.Layout.HORISONTAL))).build());

        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 30).build());
        left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), 3).build());

        TwoColumnFlexFormPanel right = new TwoColumnFlexFormPanel();
        row = -1;

        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().amount()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().createdBy(), new CEntityLabel<AbstractPmcUser>()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().createdDate()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().receivedDate()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().targetDate()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().finalizeDate()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().paymentStatus()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().lastStatusChangeDate()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().transactionAuthorizationNumber()), 10).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().transactionErrorMessage()), 22).build());
        right.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().notes()), 22).build());

        // tweak UI:
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
                        paymentMethodEditorHeader.setVisible(!getValue().leaseTermParticipant().isNull());

                        paymentMethodEditor.getValue().isProfiledMethod().setValue(Boolean.FALSE);

                        setProfiledPaymentMethodsVisible(false);
                        break;

                    case Profiled:
                        paymentMethodEditor.setViewable(true);
                        paymentMethodEditor.setVisible(false);
                        paymentMethodEditorHeader.setVisible(false);

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
                paymentMethodEditorHeader.setVisible(event.getValue() != null);
                if (event.getValue() != null) {
                    paymentMethodEditor.setViewable(true);
                    paymentMethodEditor.populate(event.getValue());
                }
            }
        });

        TwoColumnFlexFormPanel formPanel = new TwoColumnFlexFormPanel();

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
        paymentMethodEditorHeader.setVisible(false);

        get(proto().preauthorizedPayment()).setVisible(false);
        preauthorizedPaymentMethodViewerHeader.setVisible(false);

        get(proto().selectPaymentMethod()).setVisible(false);
        get(proto().addThisPaymentMethodToProfile()).setVisible(false);
        get(proto().createdBy()).setVisible(false);

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
        get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());

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
                    paymentMethodEditorHeader.setVisible(true);
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
                paymentMethodEditorHeader.setVisible(true);
            }

            boolean transactionResult = getValue().paymentMethod().isNull() ? false
                    : (getValue().paymentMethod().type().getValue().isTransactable() && getValue().paymentStatus().getValue().isProcessed());

            get(proto().transactionAuthorizationNumber()).setVisible(transactionResult);
            get(proto().transactionErrorMessage()).setVisible(transactionResult && !getValue().transactionErrorMessage().isNull());

            get(proto().preauthorizedPayment()).setVisible(!getValue().preauthorizedPayment().isNull());
            preauthorizedPaymentMethodViewerHeader.setVisible(!getValue().preauthorizedPayment().isNull());
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

    private class PreauthorizedPaymentViewer extends CEntityDecoratableForm<PreauthorizedPayment> {

        private final PapExpirationWarning expirationWarning = new PapExpirationWarning();

        public PreauthorizedPaymentViewer() {
            super(PreauthorizedPayment.class);
            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, 2, expirationWarning.getExpirationWarningPanel());

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creationDate()), 9).build());
            content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().createdBy(), new CEntityLabel<AbstractPmcUser>()), 22).build());

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 22).build());

            content.setBR(++row, 0, 2);

            content.setWidget(++row, 0, 2, inject(proto().coveredItems(), new PapCoveredItemFolder()));

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            expirationWarning.prepareView(getValue().expiring());
            setEditable(getValue().expiring().isNull());

            get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
        }
    }
}
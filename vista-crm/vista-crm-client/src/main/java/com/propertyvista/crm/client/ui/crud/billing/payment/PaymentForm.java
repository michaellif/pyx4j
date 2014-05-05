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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
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

    private final NoticeViewer noticeViewer = new NoticeViewer();

    private final PaymentMethodEditor<LeasePaymentMethod> paymentMethodEditor = new PaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {
        @Override
        public Set<PaymentType> getPaymentTypes() {
            return PaymentForm.this.getValue().allowedPaymentsSetup().allowedPaymentTypes().getValue();
        }

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            return PaymentForm.this.getValue().allowedPaymentsSetup().allowedCardTypes().getValue();
        };

        @Override
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<?, AddressSimple, ?> comp) {
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
        protected CForm<?> createEcheckInfoEditor() {
            return new EcheckInfoEditor() {
                @SuppressWarnings("rawtypes")
                @Override
                protected IsWidget createContent() {
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

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Dual, noticeViewer.getNoticePanel());

        formPanel.append(Location.Dual, createDetailsPanel());

        preauthorizedPaymentMethodViewerHeader = formPanel.h1(i18n.tr("Preauthorized Payment"));
        formPanel.append(Location.Dual, proto().preauthorizedPayment(), new PreauthorizedPaymentViewer());

        paymentMethodEditorHeader = formPanel.h1(i18n.tr("Payment Method"));
        formPanel.append(Location.Dual, proto().paymentMethod(), paymentMethodEditor);

        // tweaks:
        paymentMethodEditor.addTypeSelectionValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setupAddThisPaymentMethodToProfile(event.getValue());
                updateVisibility(event.getValue());
            }
        });

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    @SuppressWarnings("unchecked")
    private IsWidget createDetailsPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().propertyCode()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().unitNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().leaseId()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().leaseStatus()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().billingAccount().accountNumber(), new CLabel<String>()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().leaseTermParticipant(), new CEntitySelectorHyperlink<LeaseTermParticipant<? extends LeaseParticipant<?>>>() {
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
            protected IShowable getSelectorDialog() {
                return new EntitySelectorListDialog<LeaseTermParticipant<? extends LeaseParticipant<?>>>(i18n.tr("Select Tenant To Pay"), false,
                        PaymentForm.this.getValue().participants()) {
                    @Override
                    public boolean onClickOk() {
                        CComponent<?, ?, ?> comp = get(PaymentForm.this.proto().leaseTermParticipant());
                        ((CComponent<?, LeaseTermParticipant<? extends LeaseParticipant<?>>, ?>) comp).setValue(getSelectedItems().get(0));
                        return true;
                    }
                };
            }
        }).decorate();

        formPanel.append(Location.Left, proto().selectPaymentMethod(),
                new CRadioGroupEnum<PaymentDataDTO.PaymentSelect>(PaymentDataDTO.PaymentSelect.class, RadioGroup.Layout.HORISONTAL)).decorate();

        formPanel.append(Location.Left, proto().profiledPaymentMethod(), profiledPaymentMethodsCombo).decorate();
        formPanel.append(Location.Left, proto().storeInProfile()).decorate().componentWidth(50);
        formPanel.append(Location.Left, proto().convenienceFee()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().amount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().createdDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().receivedDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().targetDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().finalizeDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().paymentStatus()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().rejectedWithNSF()).decorate().componentWidth(60);
        formPanel.append(Location.Right, proto().lastStatusChangeDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().transactionAuthorizationNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().convenienceFeeTransactionAuthorizationNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().transactionErrorMessage()).decorate();
        formPanel.append(Location.Right, proto().notes()).decorate();

        // tweak UI:
        CComponent<?, ?, ?> comp = get(proto().leaseTermParticipant());
        ((CComponent<?, LeaseTermParticipant<? extends LeaseParticipant<?>>, ?>) comp)
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
                paymentMethodEditor.setElectronicPaymentsEnabled(getValue().allowedPaymentsSetup().electronicPaymentsAllowed().getValue(Boolean.FALSE));

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setEditable(true);

                        if (getValue().allowedPaymentsSetup().allowedPaymentTypes().isEmpty()) {
                            paymentMethodEditor.initNew(null);
                            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("There are no payment methods allowed!"));
                        } else {
                            // set preferred value:
                            if (getValue().allowedPaymentsSetup().allowedPaymentTypes().contains(PaymentType.Echeck)) {
                                paymentMethodEditor.initNew(PaymentType.Echeck);
                            } else if (getValue().allowedPaymentsSetup().allowedPaymentTypes().contains(PaymentType.Cash)) {
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
                        paymentMethodEditor.setEditable(false);
                        paymentMethodEditor.setVisible(false);
                        paymentMethodEditor.setPaymentTypesEnabled(EnumSet.allOf(PaymentType.class), false);
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
                    paymentMethodEditor.populate(event.getValue());
                    updateVisibility(event.getValue().type().getValue());
                }
            }
        });

        return formPanel;
    }

    @Override
    public void onReset() {
        super.onReset();

        profiledPaymentMethodsCombo.setVisible(false);
        profiledPaymentMethodsCombo.setOptions(null);
        profiledPaymentMethodsCombo.reset();

        paymentMethodEditor.reset();
        paymentMethodEditor.setEditable(false);

// TODO : investigate why invisible paymentMethodEditor is not populated!?          
//        paymentMethodEditor.setVisible(false);
//        paymentMethodEditorHeader.setVisible(false);

        get(proto().preauthorizedPayment()).setVisible(false);
        preauthorizedPaymentMethodViewerHeader.setVisible(false);

        get(proto().selectPaymentMethod()).setVisible(false);
        get(proto().storeInProfile()).setVisible(false);

        get(proto().id()).setVisible(true);
        get(proto().paymentStatus()).setVisible(true);
        get(proto().rejectedWithNSF()).setVisible(false);
        get(proto().lastStatusChangeDate()).setVisible(true);
        get(proto().finalizeDate()).setVisible(true);
        get(proto().createdBy()).setVisible(true);
        get(proto().convenienceFee()).setVisible(true);
        get(proto().notes()).setVisible(true);

        updateVisibility(null);

        noticeViewer.updateVisibility();
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);

        boolean isNew = getValue().id().isNull();

        // hide some non-relevant fields:
        get(proto().id()).setVisible(!isNew);
        get(proto().paymentStatus()).setVisible(!isNew);
        get(proto().rejectedWithNSF()).setVisible(!isNew && !getValue().rejectedWithNSF().isNull());
        get(proto().lastStatusChangeDate()).setVisible(!isNew);
        get(proto().finalizeDate()).setVisible(!isEditable());
        get(proto().updated()).setVisible(!getValue().updated().isNull());
        get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
        get(proto().convenienceFee()).setVisible(!getValue().convenienceFee().isNull());
        get(proto().notes()).setVisible(isEditable() || !getValue().notes().isNull());
        if (!getValue().paymentMethod().isNull()) {
            updateVisibility(getValue().paymentMethod().type().getValue());
        }

        noticeViewer.updateVisibility();

        if (isEditable()) {
            paymentMethodEditor.setPaymentTypes(getValue().allowedPaymentsSetup().allowedPaymentTypes());
            paymentMethodEditor.setElectronicPaymentsEnabled(getValue().allowedPaymentsSetup().electronicPaymentsAllowed().getValue(Boolean.FALSE));

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

                if (getValue().paymentMethod().isProfiledMethod().getValue(false)) {
                    profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), false, populate);
                    paymentMethodEditor.setEditable(false);
                } else {
                    paymentMethodEditor.setEditable(true);
                    get(proto().storeInProfile()).setVisible(true);
                    setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
                }
            }

            get(proto().transactionAuthorizationNumber()).setVisible(false);
            get(proto().convenienceFeeTransactionAuthorizationNumber()).setVisible(false);
            get(proto().transactionErrorMessage()).setVisible(false);

        } else { // view mode:

            if (getValue().paymentMethod().isProfiledMethod().getValue(false)) {
                profiledPaymentMethodsCombo.setVisible(true);
                profiledPaymentMethodsCombo.setValue(getValue().paymentMethod(), false, populate);
            }

            // Need to show Direct Debit details when in status Queued
            boolean transactionResult = (getValue().paymentMethod().isNull() ? false : getValue().paymentMethod().type().getValue().isTransactable());

            get(proto().transactionAuthorizationNumber()).setVisible(transactionResult && !getValue().transactionAuthorizationNumber().isNull());
            get(proto().convenienceFeeTransactionAuthorizationNumber()).setVisible(
                    transactionResult && !getValue().convenienceFeeTransactionAuthorizationNumber().isNull());
            get(proto().transactionErrorMessage()).setVisible(transactionResult && !getValue().transactionErrorMessage().isNull());

            get(proto().preauthorizedPayment()).setVisible(!getValue().preauthorizedPayment().isNull());
            preauthorizedPaymentMethodViewerHeader.setVisible(!getValue().preauthorizedPayment().isNull());
        }

        paymentMethodEditor.setVisible(!getValue().paymentMethod().isEmpty());
        paymentMethodEditorHeader.setVisible(!getValue().paymentMethod().isEmpty());
//        paymentMethodEditor.setValue(getValue().paymentMethod(), false, populate);
    }

    @Override
    public void addValidations() {
        get(proto().amount()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    return (getComponent().getValue().compareTo(BigDecimal.ZERO) > 0 ? null : new FieldValidationError(getComponent(), i18n
                            .tr("Payment amount should be greater than zero!")));
                }
                return null;
            }
        });

        get(proto().targetDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
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
        paymentMethodEditor.setPaymentTypes(getValue().allowedPaymentsSetup().allowedPaymentTypes());
        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().allowedPaymentsSetup().electronicPaymentsAllowed().getValue(Boolean.FALSE));
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
        get(proto().storeInProfile()).setVisible(!visible && !isViewable() && !getValue().paymentMethod().type().isNull());
        setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
    }

    private void setupAddThisPaymentMethodToProfile(PaymentType paymentType) {
        if (paymentType != null) {
            switch (paymentType) {
            case CreditCard:
                get(proto().storeInProfile()).setValue(true);
                get(proto().storeInProfile()).setEnabled(true);
                break;

            case Echeck:
                get(proto().storeInProfile()).setValue(true);
                get(proto().storeInProfile()).setEnabled(false);
                break;

            default:
                get(proto().storeInProfile()).setValue(false);
                get(proto().storeInProfile()).setEnabled(false);
                break;
            }
        }
    }

    private void updateVisibility(PaymentType paymentType) {
        if (paymentType == null) {
            get(proto().receivedDate()).setVisible(true);
            get(proto().targetDate()).setVisible(true);
        } else {
            get(proto().receivedDate()).setVisible(!isEditable() || paymentType.isReceiveDateEditable());
            get(proto().targetDate()).setVisible(paymentType != PaymentType.Cash);
        }
    }

    private class PreauthorizedPaymentViewer extends CForm<AutopayAgreement> {

        public PreauthorizedPaymentViewer() {
            super(AutopayAgreement.class);
            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);

            CNumberLabel comp;
            formPanel.append(Location.Left, proto().id(), comp = new CNumberLabel()).decorate().componentWidth(120);
            comp.setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    if (!getValue().id().isNull()) {
                        AppSite.getPlaceController().goTo(new CrmSiteMap.Finance.AutoPay().formViewerPlace(getValue().getPrimaryKey()));
                    }
                }
            });

            formPanel.append(Location.Left, proto().creationDate()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate();

            formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()).decorate();

            formPanel.br();

            formPanel.append(Location.Dual, inject(proto().coveredItems(), new PapCoveredItemFolder()));

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
        }
    }

    private class NoticeViewer {

        private final BasicFlexFormPanel noticePanel = new BasicFlexFormPanel();

        public NoticeViewer() {
            noticePanel.setWidget(0, 0, 1, inject(proto().notice()));
            noticePanel.getWidget(0, 0).setStyleName(VistaTheme.StyleName.WarningMessage.name());
            noticePanel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

            noticePanel.setHR(1, 0, 1);

            noticePanel.setWidth("100%");
        }

        public BasicFlexFormPanel getNoticePanel() {
            return noticePanel;
        }

        public void updateVisibility() {
            noticePanel.setVisible(getValue() != null && !getValue().notice().isNull());
        }
    }
}
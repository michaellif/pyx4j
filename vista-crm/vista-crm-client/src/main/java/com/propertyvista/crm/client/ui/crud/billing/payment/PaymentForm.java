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

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;

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
        public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured, ?> comp) {
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
    };

    public PaymentForm(IFormView<PaymentRecordDTO> view) {
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

        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().propertyCode()), 15).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitNumber()), 15).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseStatus()), 10).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().accountNumber())).build());
        get(proto().billingAccount().accountNumber()).setViewable(true);

        left.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().leaseTermParticipant(),
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
                                        CComponent<?, ?> comp = get(PaymentForm.this.proto().leaseTermParticipant());
                                        ((CComponent<LeaseTermParticipant<? extends LeaseParticipant<?>>, ?>) comp).setValue(getSelectedItems().get(0));
                                        return true;
                                    }
                                };
                            }
                        }), 25).build());

        left.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().selectPaymentMethod(),
                        new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), 20).build());

        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 25).build());

        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().addThisPaymentMethodToProfile()), 5).labelWidth(20).build());

        FormFlexPanel right = new FormFlexPanel();
        row = -1;

        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().amount()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().createdDate()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().receivedDate()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().targetDate()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().finalizeDate()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().paymentStatus()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lastStatusChangeDate()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionAuthorizationNumber()), 10).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionErrorMessage()), 20).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().notes()), 25).build());

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

        CComponent<?, ?> comp = get(proto().leaseTermParticipant());
        ((CComponent<LeaseTermParticipant<? extends LeaseParticipant<?>>, ?>) comp)
                .addValueChangeHandler(new ValueChangeHandler<LeaseTermParticipant<? extends LeaseParticipant<?>>>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LeaseTermParticipant<? extends LeaseParticipant<?>>> event) {
                        chageLeaseParticipant();
                    }
                });

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
                            } else if (getValue().allowedPaymentTypes().contains(PaymentType.Cash)) {
                                paymentMethodEditor.initNew(PaymentType.Cash);
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
                    paymentMethodEditor.setViewable(true);
                    paymentMethodEditor.populate(event.getValue());
                }
            }
        });

        FormFlexPanel panel = new FormFlexPanel();

        panel.setWidget(0, 0, left);
        panel.setWidget(0, 1, right);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        panel.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);
        left.setWidth(VistaTheme.columnWidth); // necessary for inner table columns to maintain fixed column width! 

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        boolean isNew = getValue().id().isNull();
        // hide some non-relevant fields:
        get(proto().id()).setVisible(!isNew);
        get(proto().receivedDate()).setVisible(!isEditable());
        get(proto().finalizeDate()).setVisible(!isEditable());
        get(proto().paymentStatus()).setVisible(!isNew);
        get(proto().lastStatusChangeDate()).setVisible(!isNew);

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
                        get(proto().selectPaymentMethod()).reset();
                        get(proto().selectPaymentMethod()).setValue(
                                (profiledPaymentMethodsCombo.getOptions().contains(getValue().paymentMethod()) ? PaymentSelect.Profiled : PaymentSelect.New),
                                false);
                        get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                        get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
                    }
                });

                if (getValue().paymentMethod().isOneTimePayment().isBooleanTrue()) {
                    paymentMethodEditor.setVisible(true);
                    paymentMethodEditor.setViewable(false);
                    paymentMethodEditorSeparator.setVisible(true);
                    get(proto().addThisPaymentMethodToProfile()).setVisible(true);
                    setupAddThisPaymentMethodToProfile(getValue().paymentMethod().type().getValue());
                } else {
                    profiledPaymentMethodsCombo.setVisible(true);
                    profiledPaymentMethodsCombo.setValue(getValue().paymentMethod());
                }

                // TODO : this is the HACK - check CComponent.setVisible implementation!!!
                paymentMethodEditor.setBillingAddressVisible(getValue().paymentMethod().type().getValue() != PaymentType.Cash);
            }

            get(proto().transactionAuthorizationNumber()).setVisible(false);
            get(proto().transactionErrorMessage()).setVisible(false);

        } else { // view mode:

            if (getValue().paymentMethod().isOneTimePayment().isBooleanTrue()) {
                paymentMethodEditor.setVisible(true);
                paymentMethodEditorSeparator.setVisible(true);
            } else {
                profiledPaymentMethodsCombo.setVisible(true);
                profiledPaymentMethodsCombo.setValue(getValue().paymentMethod());
            }

            boolean transactionResult = getValue().paymentMethod().isNull() ? false
                    : (getValue().paymentMethod().type().getValue().isTransactable() && getValue().paymentStatus().getValue().isProcessed());

            get(proto().transactionAuthorizationNumber()).setVisible(transactionResult);
            get(proto().transactionErrorMessage()).setVisible(transactionResult && !getValue().transactionErrorMessage().isNull());
        }
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

    private void chageLeaseParticipant() {
        paymentMethodEditor.reset();
        paymentMethodEditor.setBillingAddressAsCurrentEnabled(true);
        paymentMethodEditor.setPaymentTypes(getValue().allowedPaymentTypes());
        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));
        loadProfiledPaymentMethods(new DefaultAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                boolean hasProfiledMethods = !profiledPaymentMethodsCombo.getOptions().isEmpty();
                get(proto().selectPaymentMethod()).reset();
                get(proto().selectPaymentMethod()).setValue(hasProfiledMethods ? PaymentSelect.Profiled : PaymentSelect.New);
                get(proto().selectPaymentMethod()).setEnabled(hasProfiledMethods);
                get(proto().selectPaymentMethod()).setVisible(hasProfiledMethods);
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
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

import java.util.ArrayList;
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
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;

public class PaymentForm extends CrmEntityForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentForm.class);

    private Widget paymentMethodEditorSeparator;

    private final CComboBox<PaymentMethod> profiledPaymentMethodsCombo = new CSimpleEntityComboBox<PaymentMethod>();

    private final PaymentMethodEditor paymentMethodEditor = new PaymentMethodEditor() {
        @Override
        public List<PaymentType> getPaymentOptions() {
            return new ArrayList<PaymentType>(PaymentType.avalableInCrm());
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

    public PaymentForm() {
        this(false);
    }

    public PaymentForm(boolean viewMode) {
        super(PaymentRecordDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
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

        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTermParticipant(), new CEntitySelectorHyperlink<LeaseTermParticipant<?>>() {
            @Override
            protected AppPlace getTargetPlace() {
                if (getValue().isInstanceOf(LeaseTermTenant.class)) {
                    return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().getPrimaryKey());
                } else if (getValue().isInstanceOf(LeaseTermGuarantor.class)) {
                    return AppPlaceEntityMapper.resolvePlace(Guarantor.class, getValue().getPrimaryKey());
                } else {
                    throw new IllegalArgumentException("Incorrect LeaseParticipant value!");
                }
            }

            @Override
            protected AbstractEntitySelectorDialog<LeaseTermParticipant<?>> getSelectorDialog() {
                return new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenant To Pay"), false, PaymentForm.this.getValue().participants()) {

                    @Override
                    public boolean onClickOk() {
                        get(PaymentForm.this.proto().leaseTermParticipant()).setValue(getSelectedItems().get(0));
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

        get(proto().addThisPaymentMethodToProfile()).setVisible(false);

        get(proto().leaseTermParticipant()).addValueChangeHandler(new ValueChangeHandler<LeaseTermParticipant>() {
            @Override
            public void onValueChange(ValueChangeEvent<LeaseTermParticipant> event) {
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
                        if (getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE)) {
                            paymentMethodEditor.initNew(PaymentType.Echeck);
                        } else {
                            paymentMethodEditor.initNew(PaymentType.Cash);
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
                        profiledPaymentMethodsCombo.setMandatory(true);
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

        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getColumnFormatter().setWidth(1, "50%");

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().selectPaymentMethod()).setVisible(false);

        boolean isNew = getValue().id().isNull();
        get(proto().id()).setVisible(!isNew);
        if (isNew) {
            // Allow edit all values
            get(proto().leaseTermParticipant()).setEditable(true);
        } else {
            // Disable most of modifications
            get(proto().leaseTermParticipant()).setEditable(false);

            // Allow to change profiled Method, but do not edit its values.
            boolean allowtoChangeProfiledMethod = !isViewable() && (!getValue().paymentMethod().isOneTimePayment().isBooleanTrue());
            profiledPaymentMethodsCombo.setVisible(allowtoChangeProfiledMethod);
            if (allowtoChangeProfiledMethod) {
                profiledPaymentMethodsCombo.setMandatory(true);
                profiledPaymentMethodsCombo.setValue(getValue().paymentMethod());
                loadProfiledPaymentMethods(null);
            }
            get(proto().addThisPaymentMethodToProfile()).setVisible(false);

            paymentMethodEditor.setVisible(true);
            // Allow to Edit one time payment method
            paymentMethodEditor.setViewable(!getValue().paymentMethod().isOneTimePayment().isBooleanTrue());
            paymentMethodEditor.setTypeSelectionEnabled(false);
            paymentMethodEditorSeparator.setVisible(true);

            // TODO : this is the HACK - check CComponent.setVisible implementation!!!
            paymentMethodEditor.setBillingAddressVisible(getValue().paymentMethod().type().getValue() != PaymentType.Cash);

        }

        paymentMethodEditor.setElectronicPaymentsEnabled(getValue().electronicPaymentsAllowed().getValue(Boolean.FALSE));

        if (isEditable()) {
            get(proto().transactionAuthorizationNumber()).setVisible(false);
            get(proto().transactionErrorMessage()).setVisible(false);
        } else {
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
        profiledPaymentMethodsCombo.setMandatory(false);
        profiledPaymentMethodsCombo.reset();
        profiledPaymentMethodsCombo.setOptions(null);

        paymentMethodEditor.reset();
        paymentMethodEditor.setVisible(false);
        paymentMethodEditor.setViewable(true);
        paymentMethodEditor.setTypeSelectionEnabled(true);
        paymentMethodEditorSeparator.setVisible(false);

        get(proto().addThisPaymentMethodToProfile()).setVisible(false);
    }

    private void loadProfiledPaymentMethods(final AsyncCallback<Void> callback) {
        profiledPaymentMethodsCombo.setOptions(null);
        ((PaymentEditorView.Presenter) ((PaymentEditorView) getParentView()).getPresenter()).getProfiledPaymentMethods(
                new DefaultAsyncCallback<List<PaymentMethod>>() {
                    @Override
                    public void onSuccess(List<PaymentMethod> result) {
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
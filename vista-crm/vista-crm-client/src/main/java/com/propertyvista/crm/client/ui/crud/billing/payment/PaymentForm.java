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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
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
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;

public class PaymentForm extends CrmEntityForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentForm.class);

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

    private final PaymentMethodEditor paymentMethodEditor = new PaymentMethodEditor() {
        @Override
        public Collection<PaymentType> getPaymentOptions() {
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
                        }, PaymentForm.this.getValue().leaseParticipant());
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
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, createDetailsPanel());
        main.setHR(1, 0, 1);
        main.setWidget(2, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        return new ScrollPanel(main);
    }

    private IsWidget createDetailsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().propertyCode()), 15).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitNumber()), 15).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseStatus()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().accountNumber())).build());
        get(proto().billingAccount().accountNumber()).setViewable(true);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant(), new CEntitySelectorHyperlink<LeaseParticipant>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(getValue().getInstanceValueClass(), getValue().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<LeaseParticipant> getSelectorDialog() {
                return new EntitySelectorListDialog<LeaseParticipant>(i18n.tr("Select Tenant/Guarantor To Pay"), false, PaymentForm.this.getValue()
                        .participants()) {

                    @Override
                    public boolean onClickOk() {
                        get(PaymentForm.this.proto().leaseParticipant()).setValue(getSelectedItems().get(0));
                        return true;
                    }
                };
            }
        }), 25).build());

        panel.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().paymentSelect(), new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), 20)
                        .build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().profiledPaymentMethod(), profiledPaymentMethodsCombo), 25).build());
        profiledPaymentMethodsCombo.setMandatory(true);

        row = -1;
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().amount()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().createdDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().receivedDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().targetDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().finalizeDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().paymentStatus()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lastStatusChangeDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionAuthorizationNumber()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionErrorMessage()), 20).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().notes()), 25).build());

        // tweak UI:
        get(proto().id()).setViewable(true);
        get(proto().propertyCode()).setViewable(true);
        get(proto().unitNumber()).setViewable(true);
        get(proto().leaseId()).setViewable(true);
        get(proto().leaseStatus()).setViewable(true);
        get(proto().paymentStatus()).setViewable(true);
        get(proto().createdDate()).setViewable(true);
        get(proto().receivedDate()).setViewable(true);
        get(proto().lastStatusChangeDate()).setViewable(true);

        get(proto().leaseParticipant()).addValueChangeHandler(new ValueChangeHandler<LeaseParticipant>() {
            @Override
            public void onValueChange(ValueChangeEvent<LeaseParticipant> event) {
                paymentMethodEditor.reset();
                paymentMethodEditor.setBillingAddressAsCurrentEnabled(!event.getValue().isNull());
                checkProfiledPaymentMethods(new DefaultAsyncCallback<List<PaymentMethod>>() {
                    @Override
                    public void onSuccess(List<PaymentMethod> result) {
                        get(proto().paymentSelect()).populate(null);
                        get(proto().paymentSelect()).setValue(result.isEmpty() ? PaymentSelect.New : PaymentSelect.Profiled);
                        get(proto().paymentSelect()).setVisible(!result.isEmpty());
                    }
                });
            }
        });

        get(proto().paymentSelect()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.reset();

                if (event.getValue() != null) {
                    switch (event.getValue()) {
                    case New:
                        paymentMethodEditor.setViewable(false);
                        paymentMethodEditor.setTypeSelectionVisible(true);
                        paymentMethodEditor.selectPaymentDetailsEditor(PaymentType.Echeck);
                        paymentMethodEditor.setVisible(!getValue().leaseParticipant().isNull());

                        paymentMethodEditor.getValue().isOneTimePayment().setValue(Boolean.TRUE);

                        profiledPaymentMethodsCombo.setVisible(false);
                        break;
                    case Profiled:
                        paymentMethodEditor.setViewable(true);
                        paymentMethodEditor.setTypeSelectionVisible(false);
                        paymentMethodEditor.setVisible(false);

                        profiledPaymentMethodsCombo.populate(null);
                        profiledPaymentMethodsCombo.setVisible(true);
                        break;
                    }
                }
            }
        });

        profiledPaymentMethodsCombo.addValueChangeHandler(new ValueChangeHandler<PaymentMethod>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentMethod> event) {
                paymentMethodEditor.setVisible(event.getValue() != null);
                if (event.getValue() != null) {
                    paymentMethodEditor.setViewable(true);
                    paymentMethodEditor.populate(event.getValue());
                }
            }
        });

        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getColumnFormatter().setWidth(1, "50%");
        return panel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().id()).setVisible(!getValue().id().isNull());
        get(proto().paymentSelect()).setVisible(!isViewable() && !getValue().leaseParticipant().isNull());
        profiledPaymentMethodsCombo.setVisible(false);

        checkProfiledPaymentMethods(null);

        paymentMethodEditor.setVisible(!getValue().leaseParticipant().isNull());
        paymentMethodEditor.setBillingAddressAsCurrentEnabled(!getValue().leaseParticipant().isNull());

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

    private void checkProfiledPaymentMethods(final AsyncCallback<List<PaymentMethod>> callback) {
        if (getParentView() instanceof PaymentEditorView) {
            get(proto().paymentSelect()).setEnabled(false);

            profiledPaymentMethodsCombo.populate(null);
            profiledPaymentMethodsCombo.setOptions(null);

            if (!getValue().leaseParticipant().isNull()) {
                ((PaymentEditorView.Presenter) ((PaymentEditorView) getParentView()).getPresenter()).getProfiledPaymentMethods(
                        new DefaultAsyncCallback<List<PaymentMethod>>() {
                            @Override
                            public void onSuccess(List<PaymentMethod> result) {
                                get(proto().paymentSelect()).setEnabled(!result.isEmpty());
                                get(proto().paymentSelect()).setVisible(!result.isEmpty());

                                profiledPaymentMethodsCombo.setOptions(result);
                                profiledPaymentMethodsCombo.setMandatory(true);

                                if (callback != null) {
                                    callback.onSuccess(result);
                                }
                            }
                        }, getValue().leaseParticipant());
            }
        }
    }
}
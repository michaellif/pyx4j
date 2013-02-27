/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.paymentmethod;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.portal.client.ui.residents.payment.PortalPaymentTypesUtil;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsView.Presenter;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;

public class PaymentMethodsForm extends CEntityForm<PaymentMethodListDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodsForm.class);

    private PaymentMethodsView.Presenter presenter;

    public PaymentMethodsForm() {
        super(PaymentMethodListDTO.class, new VistaViewersComponentFactory());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().paymentMethods(), new PaymentMethodFolder()));
        return container;
    }

    public void populate(List<LeasePaymentMethod> paymentMethods) {
        PaymentMethodListDTO dto = EntityFactory.create(PaymentMethodListDTO.class);
        dto.paymentMethods().addAll(paymentMethods);
        super.populate(dto);
    }

    private class PaymentMethodFolder extends VistaTableFolder<LeasePaymentMethod> {

        public PaymentMethodFolder() {
            super(LeasePaymentMethod.class, true);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off                    
                    new EntityFolderColumnDescriptor(proto().type(), "15em"), 
                    new EntityFolderColumnDescriptor(proto().details(), "20em"),
                    new EntityFolderColumnDescriptor(proto().isPreauthorized(), "5em")
            ); //@formatter:on
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof LeasePaymentMethod) {
                return new LeasePaymentMethodEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPaymentMethod();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<LeasePaymentMethod> item) {

            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Payment Method?"), new Command() {
                @Override
                public void execute() {
                    presenter.removePaymentMethod(item.getValue());
                    PaymentMethodFolder.super.removeItem(item);
                }
            });
        }

        @Override
        public void addValidations() {
            super.addValidations();

            this.addValueValidator(new EditableValueValidator<List<LeasePaymentMethod>>() {
                @Override
                public ValidationError isValid(CComponent<List<LeasePaymentMethod>, ?> component, List<LeasePaymentMethod> value) {
                    if (value != null && !value.isEmpty()) {
                        boolean primaryFound = false;
                        for (LeasePaymentMethod item : value) {
                            if (item.isPreauthorized().isBooleanTrue()) {
                                primaryFound = true;
                                break;
                            }
                        }
                        if (!primaryFound) {
                            return new ValidationError(component, i18n.tr("Default payment should be selected"));
                        }
                    }
                    return null;
                }
            });
        }

        private class LeasePaymentMethodEditor extends CEntityFolderRowEditor<LeasePaymentMethod> {

            public LeasePaymentMethodEditor() {
                super(LeasePaymentMethod.class, columns());
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = null;
                if (member.equals(proto().details())) {
                    comp = new CEntityHyperlink<PaymentDetails>(null, new Command() {
                        @Override
                        public void execute() {
                            presenter.editPaymentMethod(getValue());
                        }
                    });
                    comp.setViewable(true);
                } else if (member.equals(proto().isPreauthorized())) {
                    comp = new CCheckBox();
                    comp.inheritViewable(false);
                    comp.setViewable(false);
                } else {
                    comp = super.create(member);
                }
                return comp;
            }

            @Override
            public void addValidations() {
                super.addValidations();

                get(proto().isPreauthorized()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        if (event.getValue().booleanValue()) {
                            MessageDialog.confirm(i18n.tr("Please Confirm"), i18n.tr("Mark the payment as pre-authorized?"), new Command() {
                                @Override
                                public void execute() { // Confirmation:
                                    presenter.savePaymentMethod(LeasePaymentMethodEditor.this.getValue());
                                    for (int i = 0; i < PaymentMethodFolder.this.getItemCount(); ++i) {
                                        for (CComponent<?, ?> comp : PaymentMethodFolder.this.getItem(i).getComponents()) {
                                            if (comp instanceof LeasePaymentMethodEditor && !comp.equals(LeasePaymentMethodEditor.this)) {
                                                ((LeasePaymentMethodEditor) comp).get(proto().isPreauthorized()).setValue(false, false);
                                                presenter.savePaymentMethod(((LeasePaymentMethodEditor) comp).getValue());
                                            }
                                        }
                                    }
                                }
                            }, new Command() {
                                @Override
                                public void execute() { // Declining:
                                    get(proto().isPreauthorized()).setValue(false, false);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                // disable non-allowed items:
                if (!PortalPaymentTypesUtil.getAllowedPaymentTypes().contains(getValue().type().getValue())) {
                    get(proto().details()).setEnabled(false);
                    get(proto().isPreauthorized()).setEnabled(false);
                }
            }
        }
    }
}

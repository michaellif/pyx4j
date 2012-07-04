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

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.payment.PaymentMethod;
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

    public void populate(List<PaymentMethod> paymentMethods) {
        PaymentMethodListDTO dto = EntityFactory.create(PaymentMethodListDTO.class);
        dto.paymentMethods().addAll(paymentMethods);
        super.populate(dto);
    }

    private class PaymentMethodFolder extends VistaTableFolder<PaymentMethod> {

        public PaymentMethodFolder() {
            super(PaymentMethod.class, true);
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
            if (member instanceof PaymentMethod) {
                return new PaymentMethodEditorEx();
            } else if (member instanceof PaymentDetails) {
                return new CEntityLabel<PaymentDetails>();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPaymentMethod();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PaymentMethod> item) {

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

            this.addValueValidator(new EditableValueValidator<List<PaymentMethod>>() {
                @Override
                public ValidationError isValid(CComponent<List<PaymentMethod>, ?> component, List<PaymentMethod> value) {
                    if (value != null && !value.isEmpty()) {
                        boolean primaryFound = false;
                        for (PaymentMethod item : value) {
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

        private class PaymentMethodEditorEx extends CEntityFolderRowEditor<PaymentMethod> {

            public PaymentMethodEditorEx() {
                super(PaymentMethod.class, columns());
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = null;
                if (member.equals(proto().type())) {
                    comp = new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            presenter.editPaymentMethod(getValue());
                        }
                    });
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
                            presenter.savePaymentMethod(PaymentMethodEditorEx.this.getValue());
                            for (int i = 0; i < PaymentMethodFolder.this.getItemCount(); ++i) {
                                for (CComponent<?, ?> comp : PaymentMethodFolder.this.getItem(i).getComponents()) {
                                    if (comp instanceof PaymentMethodEditorEx && !comp.equals(PaymentMethodEditorEx.this)) {
                                        ((PaymentMethodEditorEx) comp).get(proto().isPreauthorized()).setValue(false, false);
                                        presenter.savePaymentMethod(((PaymentMethodEditorEx) comp).getValue());
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}

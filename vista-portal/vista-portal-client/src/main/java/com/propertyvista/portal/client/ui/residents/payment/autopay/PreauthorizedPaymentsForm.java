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
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView.Presenter;
import com.propertyvista.portal.domain.dto.PreauthorizedPaymentListDTO;

public class PreauthorizedPaymentsForm extends CEntityForm<PreauthorizedPaymentListDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private PreauthorizedPaymentsView.Presenter presenter;

    public PreauthorizedPaymentsForm() {
        super(PreauthorizedPaymentListDTO.class, new VistaViewersComponentFactory());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));
        return container;
    }

    public void populate(List<PreauthorizedPayment> preauthorizedPayment) {
        PreauthorizedPaymentListDTO dto = EntityFactory.create(PreauthorizedPaymentListDTO.class);
        dto.preauthorizedPayments().addAll(preauthorizedPayment);
        super.populate(dto);
    }

    private class PreauthorizedPaymentFolder extends VistaTableFolder<PreauthorizedPayment> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPayment.class, true);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off                    
                    new EntityFolderColumnDescriptor(proto().paymentMethod(), "30em"), 
                    new EntityFolderColumnDescriptor(proto().value(), "8em"), 
                    new EntityFolderColumnDescriptor(proto().percent(), "7em")
            ); //@formatter:on
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPayment) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPreauthorizedPayment();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPayment> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
                @Override
                public void execute() {
                    presenter.deletePreauthorizedPayment(item.getValue());
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityFolderRowEditor<PreauthorizedPayment> {

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPayment.class, columns());
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = null;
                if (member.equals(proto().paymentMethod())) {
                    comp = new CEntityHyperlink<PaymentDetails>(null, new Command() {
                        @Override
                        public void execute() {
                            presenter.viewPaymentMethod(getValue().paymentMethod());
                        }
                    });
                    comp.setViewable(true);
                } else {
                    comp = super.create(member);
                }
                return comp;
            }
        }
    }
}

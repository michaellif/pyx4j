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
package com.propertyvista.portal.client.ui.residents;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;

public class PaymentMethodsForm extends CEntityEditor<PaymentMethodListDTO> implements PaymentMethodsView {

    private static I18n i18n = I18n.get(PaymentMethodsForm.class);

    private PaymentMethodsView.Presenter presenter;

    public PaymentMethodsForm() {
        super(PaymentMethodListDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().paymentMethods(), createPaymentMethodsViewer()));
        return container;
    }

    @Override
    public void populate(List<PaymentMethod> paymentMethods) {
        PaymentMethodListDTO dto = EntityFactory.create(PaymentMethodListDTO.class);
        dto.paymentMethods().addAll(paymentMethods);
        super.populate(dto);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    private CEntityFolder<PaymentMethod> createPaymentMethodsViewer() {

        return new VistaTableFolder<PaymentMethod>(PaymentMethod.class, i18n.tr("Payment Method"), true) {

            {
                setOrderable(false);
            }

            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().creditCard().number(), "12em"));
                columns.add(new EntityFolderColumnDescriptor(proto().creditCard().expiryDate(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto().creditCard().name(), "16em"));
                columns.add(new EntityFolderColumnDescriptor(proto().primary(), "4em"));
                return columns;
            }

            @Override
            protected void addItem() {
                presenter.addPaymentMethod();
            }
        };
    }

}

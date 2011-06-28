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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;
import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.common.domain.IAddressFull;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;

public class PaymentMethodsForm extends CEntityForm<PaymentMethodListDTO> {

    private static I18n i18n = I18nFactory.getI18n(PaymentMethodsForm.class);

    public PaymentMethodsForm() {
        super(PaymentMethodListDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();

        container.add(inject(proto().paymentMethods(), createPaymentMethodsViewer()));

        return container;
    }

    @Override
    public void populate(PaymentMethodListDTO paymentMethods) {
        super.populate(paymentMethods);
    }

    //TODO finish implementation
    private CEntityFolderViewer<PaymentMethodDTO> createPaymentMethodsViewer() {

        return new CEntityFolderViewer<PaymentMethodDTO>(PaymentMethodDTO.class) {

            @Override
            protected IFolderViewerDecorator<PaymentMethodDTO> createFolderDecorator() {
                return new BaseFolderViewerDecorator<PaymentMethodDTO>();
            }

            @Override
            protected CEntityFolderItemViewer<PaymentMethodDTO> createItem() {
                return createPaymenLineViewer();
            }

        };
    }

    private CEntityFolderItemViewer<PaymentMethodDTO> createPaymenLineViewer() {

        return new CEntityFolderItemViewer<PaymentMethodDTO>() {

            @Override
            public IFolderItemViewerDecorator<PaymentMethodDTO> createFolderItemDecorator() {
                return new BaseFolderItemViewerDecorator<PaymentMethodDTO>();
            }

            @Override
            public IsWidget createContent(PaymentMethodDTO value) {
                return createPaymentMethodLine(value);
            }

        };
    }

    private IsWidget createPaymentMethodLine(PaymentMethodDTO paymentMethod) {
        HorizontalPanel container = new HorizontalPanel();
        container.setSpacing(10);
        container.setWidth("100%");
        Label item = new Label(paymentMethod.type().getStringView());
        container.add(item);
        container.setCellWidth(item, "10%");
        item = new Label(formatAddress(paymentMethod.billingAddress()));
        container.add(item);
        container.setCellWidth(item, "50%");

        item = new Label(paymentMethod.primary().getStringView());
        container.add(item);
        container.setCellWidth(item, "10%");

        //Edit link
        CHyperlink link = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //TODO implement
            }
        });
        link.setValue(i18n.tr("Edit"));
        container.add(link);
        container.setCellWidth(link, "10%");

        link = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //TODO implement
            }
        });
        link.setValue(i18n.tr("Remove"));
        container.add(link);
        container.setCellWidth(link, "10%");

        return container;
    }

    private String formatAddress(IAddressFull address) {
        if (address.isNull())
            return "";

        StringBuffer addrString = new StringBuffer();

        addrString.append(address.streetNumber().getStringView());
        addrString.append(" ");
        addrString.append(address.streetName().getStringView());

        if (!address.city().isNull()) {
            addrString.append(", ");
            addrString.append(address.city().getStringView());
        }

        if (!address.province().isNull()) {
            addrString.append(" ");
            addrString.append(address.province().getStringView());
        }

        if (!address.postalCode().isNull()) {
            addrString.append(" ");
            addrString.append(address.postalCode().getStringView());
        }

        return addrString.toString();
    }

}

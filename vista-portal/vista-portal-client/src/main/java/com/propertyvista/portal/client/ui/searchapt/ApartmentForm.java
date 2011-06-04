/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 3, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.portal.client.ui.decorations.ApartmentCardDecorator;
import com.propertyvista.portal.domain.dto.PropertyDTO;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityViewer;

public class ApartmentForm extends CEntityForm<PropertyDTO> {

    private PropertyMapView.Presenter presenter;

    private final ApartmentCardDecorator cardDecor;

    private PropertyDTO property;

    private HandlerRegistration handler;

    public ApartmentForm() {
        super(PropertyDTO.class);
        cardDecor = new ApartmentCardDecorator();
        handler = null;
    }

    public void setPresenter(PropertyMapView.Presenter _presenter) {
        this.presenter = _presenter;
        if (handler != null) {
            handler.removeHandler();
        }
        handler = cardDecor.addViewDetailsClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.showPropertyDetails(property);

            }
        });

    }

    @Override
    public void populate(PropertyDTO property) {
        this.property = property;
        super.populate(this.property);

    }

    @Override
    public IsWidget createContent() {
        cardDecor.setCardHeader(inject(proto().address(), formatAddress()));
        return cardDecor;
    }

    private CEntityViewer<IAddress> formatAddress() {

        return new CEntityViewer<IAddress>() {
            @Override
            public IsWidget createContent(IAddress value) {
                StringBuffer address = new StringBuffer();
                address.append(value.street1().getValue());
                if (!value.street2().isNull()) {
                    address.append(" ");
                    address.append(value.street2().getValue());
                }

                address.append(", ");
                address.append(value.city().getValue());
                address.append(" ");
                address.append(value.province().getValue());
                address.append(" ");
                address.append(value.postalCode().getValue());

                return new Label(address.toString());
            }
        };
    }

}

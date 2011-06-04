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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.portal.client.ui.decorations.ApartmentCardDecorator;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;

public class PropertyListForm extends CEntityForm<PropertyListDTO> {

    private PropertyMapView.Presenter presenter;

    public PropertyListForm() {
        super(PropertyListDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();

        container.add(inject(proto().properties(), createAppartmentFolderViewer()));

        return container;
    }

    public void setPresenter(final PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(PropertyListDTO propertyList) {
        super.populate(propertyList);
    }

    private CEntityFolderViewer<PropertyDTO> createAppartmentFolderViewer() {

        return new CEntityFolderViewer<PropertyDTO>(PropertyDTO.class) {

            @Override
            protected IFolderViewerDecorator<PropertyDTO> createFolderDecorator() {
                return new BaseFolderViewerDecorator<PropertyDTO>();
            }

            @Override
            protected CEntityFolderItemViewer<PropertyDTO> createItem() {
                return createAppartmentViewer();
            }

        };
    }

    private CEntityFolderItemViewer<PropertyDTO> createAppartmentViewer() {

        return new CEntityFolderItemViewer<PropertyDTO>() {

            @Override
            public IFolderItemViewerDecorator createFolderItemDecorator() {
                return new ApartmentCardDecorator();
            }

            @Override
            public IsWidget createContent(PropertyDTO value) {
                return fillAppartmentCard(value);
            }

        };
    }

    private FlowPanel fillAppartmentCard(PropertyDTO value) {

        Card card = new Card();
        card.setCardImage(new HTML("Image"));

        StringBuffer address = new StringBuffer();
        address.append(value.address().street1().getValue());
        if (!value.address().street2().isNull()) {
            address.append(" ");
            address.append(value.address().street2().getValue());
        }

        address.append(", ");
        address.append(value.address().city().getValue());
        address.append(" ");
        address.append(value.address().province().getValue());
        address.append(" ");
        address.append(value.address().postalCode().getValue());

        card.setCardHeader(new Label(address.toString()));

        return card;

    }

}

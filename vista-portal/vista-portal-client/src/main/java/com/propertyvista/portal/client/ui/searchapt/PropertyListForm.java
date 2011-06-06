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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitiveSet;

import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.decorations.ApartmentCardDecorator;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class PropertyListForm extends CEntityForm<PropertyListDTO> {

    private PropertyMapView.Presenter presenter;

    private final DecorationData decor;

    public PropertyListForm() {
        super(PropertyListDTO.class);
        decor = new DecorationData(10, Unit.PCT, 90, Unit.PCT);
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
            public IFolderItemViewerDecorator<PropertyDTO> createFolderItemDecorator() {
                return new ApartmentCardDecorator(presenter);
            }

            @Override
            public IsWidget createContent(PropertyDTO value) {
                return fillAppartmentCard(value);
            }

        };
    }

    private FlowPanel fillAppartmentCard(PropertyDTO value) {

        CardPanel card = new CardPanel();
        if (value.mainMedia().isNull()) {
            card.setCardImage(new Image(PortalImages.INSTANCE.noImage()));
        } else {
            card.setCardImage(new Image("media/" + value.mainMedia().getValue().toString() + "/" + ThumbnailSize.medium.name() + ".jpg"));
        }

        card.setCardHeader(new Label(formatAddress(value.address())));

        FlowPanel content = new FlowPanel();
        Label lbl = new Label(formatFloorplans(value.floorplanNames()));
        content.add(lbl);

        lbl = new Label(formatAmenities(value.amenities()));
        content.add(lbl);
        card.setCardContent(content);

        return card;

    }

    private String formatAddress(IAddress address) {
        if (address.isNull())
            return "";

        StringBuffer addrString = new StringBuffer();

        addrString.append(address.street1().getStringView());
        if (!address.street2().isNull()) {
            addrString.append(" ");
            addrString.append(address.street2().getStringView());
        }

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

    private String formatFloorplans(IPrimitiveSet<String> floorplans) {
        final String delimiter = "/ ";

        if (floorplans.isNull())
            return "";

        StringBuffer planString = new StringBuffer();

        for (String planName : floorplans.getValue()) {
            if (planName != null && !planName.isEmpty()) {
                planString.append(planName);
                planString.append(delimiter);
            }
        }
        String finalString = planString.toString();
        if (!finalString.isEmpty()) {
            finalString = finalString.substring(0, finalString.lastIndexOf(delimiter));
        }
        return finalString;
    }

    private String formatAmenities(IList<AmenityDTO> amenities) {
        final String delimiter = "/ ";

        if (amenities.isNull())
            return "";

        StringBuffer planString = new StringBuffer();

        for (AmenityDTO amenity : amenities) {
            if (!amenity.isNull() && !amenity.isEmpty()) {
                planString.append(amenity.name().getValue());
                planString.append(delimiter);
            }
        }
        String finalString = planString.toString();
        if (!finalString.isEmpty()) {
            finalString = finalString.substring(0, finalString.lastIndexOf(delimiter));
        }
        return finalString;
    }

}

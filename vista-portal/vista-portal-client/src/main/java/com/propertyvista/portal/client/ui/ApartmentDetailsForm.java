/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 26, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;

public class ApartmentDetailsForm extends CEntityForm<PropertyDetailsDTO> implements ApartmentDetailsView {

    private ApartmentDetailsView.Presenter presenter;

    public ApartmentDetailsForm() {
        super(PropertyDetailsDTO.class);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(PropertyDetailsDTO property) {
        super.populate(property);
    }

    @Override
    public IsWidget createContent() {

        FlowPanel container = new FlowPanel();
        DecorationData readOnlyDecor = new DecorationData(12d, 25);
        readOnlyDecor.editable = false;
        container.add(new VistaWidgetDecorator(inject(proto().address(), new CEntityViewer<IAddress>() {
            @Override
            public IsWidget createContent(IAddress value) {
                return formatAddress(value);
            }
        }), readOnlyDecor));

        //container.add(new VistaWidgetDecorator(inject(proto().price()), readOnlyDecor));
        container.add(new VistaWidgetDecorator(inject(proto().amenities(), new CEntityViewer<IList<AmenityDTO>>() {
            @Override
            public IsWidget createContent(IList<AmenityDTO> value) {
                return listAmenities(value);
            }
        }), readOnlyDecor));

        container.add(inject(proto().floorplans(), createFloorplanFolderViewer()));

        return container;

    }

    public Presenter getPresenter() {
        return presenter;

    }

    private CEntityFolderViewer<FloorplanDTO> createFloorplanFolderViewer() {

        return new CEntityFolderViewer<FloorplanDTO>(FloorplanDTO.class) {

            @Override
            protected IFolderViewerDecorator<FloorplanDTO> createFolderDecorator() {
                return new BaseFolderViewerDecorator<FloorplanDTO>();
            }

            @Override
            protected CEntityFolderItemViewer<FloorplanDTO> createItem() {
                return createFloorplanViewer();
            }

        };
    }

    private CEntityFolderItemViewer<FloorplanDTO> createFloorplanViewer() {

        return new CEntityFolderItemViewer<FloorplanDTO>() {

            @Override
            public IFolderItemViewerDecorator createFolderItemDecorator() {
                return new FloorplanCardDecorator();
            }

            @Override
            public IsWidget createContent(FloorplanDTO value) {
                return fillFloorplanCard(value);
            }

        };
    }

    private SimplePanel formatAddress(IAddress address) {
        SimplePanel container = new SimplePanel();
        container.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        StringBuffer addressString = new StringBuffer();
        addressString.append(address.street1());
        if (!address.street2().isNull() && address.street2().getValue().length() > 0) {
            addressString.append(address.street2());
        }
        addressString.append(address.city());
        addressString.append(address.province().code());

        container.add(new Label(addressString.toString()));
        return container;

    }

    private VerticalPanel listAmenities(IList<AmenityDTO> amenities) {
        VerticalPanel container = new VerticalPanel();
        if (amenities != null)
            for (AmenityDTO amenity : amenities)
                container.add(new Label(amenity.name().getValue()));
        return container;

    }

    private FlowPanel fillFloorplanCard(FloorplanDTO value) {
        FlowPanel card = new FlowPanel();
        card.setSize("80%", "100%");

        FlowPanel imageHolder = new FlowPanel();
        imageHolder.setHeight("100%");
        imageHolder.setWidth("30%");
        imageHolder.getElement().getStyle().setFloat(Float.LEFT);
        imageHolder.add(new HTML("Image"));
        card.add(imageHolder);

        FlowPanel content = new FlowPanel();
        content.setHeight("100%");
        content.setWidth("70%");
        content.getElement().getStyle().setFloat(Float.RIGHT);
        card.add(content);

        Label lbl = null;
        if (!value.name().isNull()) {
            lbl = new Label(value.name().getValue());
            lbl.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(lbl);
        }

        if (!value.area().isNull()) {
            lbl = new Label(value.area().getValue().toString());
            content.add(lbl);
        }

        if (!value.description().isNull()) {
            lbl = new Label(value.description().getValue());
            content.add(lbl);
        }
        return card;

    }
}

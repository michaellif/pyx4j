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
package com.propertyvista.portal.client.ui.searchapt;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.portal.client.ui.decorations.FloorplanCardDecorator;
import com.propertyvista.portal.client.ui.decorations.PortalListDecorator;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.domain.dto.RangeDTO;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;
import com.pyx4j.entity.shared.IList;

public class ApartmentDetailsForm extends CEntityForm<PropertyDetailsDTO> implements ApartmentDetailsView {

    private ApartmentDetailsView.Presenter presenter;

    private final DecorationData decor;

    private final DecorationData listDecor;

    public ApartmentDetailsForm() {
        super(PropertyDetailsDTO.class);
        //    decor = new DecorationData(10, Unit.PCT, 90, Unit.PCT);
        decor = new DecorationData(10d, 35);
        decor.editable = false;
        listDecor = new DecorationData(0, Unit.PCT, 100, Unit.PCT);
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

        container.add(new VistaWidgetDecorator(inject(proto().address(), new CEntityViewer<IAddress>() {
            @Override
            public IsWidget createContent(IAddress value) {
                return formatAddress(value);
            }
        }), decor));

        container.add(new VistaWidgetDecorator(inject(proto().price(), new CEntityViewer<RangeDTO>() {
            @Override
            public IsWidget createContent(RangeDTO value) {
                return formatPrice(value);
            }
        }), decor));

        container.add(new VistaWidgetDecorator(inject(proto().amenities(), new CEntityViewer<IList<AmenityDTO>>() {
            @Override
            public IsWidget createContent(IList<AmenityDTO> value) {
                return new PortalListDecorator(value, "name", listDecor);
            }
        }), decor));

        container.add(inject(proto().floorplans(), createFloorplanFolderViewer()));

        return container;

    }

    public Presenter getPresenter() {
        return presenter;

    }

    private Label formatPrice(RangeDTO priceRange) {
        if (priceRange.isNull())
            return new Label("");

        StringBuffer priceString = new StringBuffer("$");
        if (!priceRange.from().isNull()) {
            priceString.append(priceRange.from().getValue());
        }

        if (!priceRange.to().isNull()) {
            priceString.append(" - ");
            priceString.append(priceRange.to().getValue());
        }

        return new Label(priceString.toString());

    }

    private Label formatAddress(IAddress address) {

        if (address.isNull())
            return new Label("");

        StringBuffer addrString = new StringBuffer();

        addrString.append(address.street1().getValue());
        if (!address.street2().isNull()) {
            addrString.append(" ");
            addrString.append(address.street2().getValue());
        }

        if (!address.city().isNull()) {
            addrString.append(", ");
            addrString.append(address.city().getValue());
        }

        if (!address.province().isNull()) {
            addrString.append(" ");
            addrString.append(address.province().getValue());
        }

        if (!address.postalCode().isNull()) {
            addrString.append(" ");
            addrString.append(address.postalCode().getValue());
        }

        return new Label(addrString.toString());
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

    private FlowPanel fillFloorplanCard(FloorplanDTO value) {

        CardPanel card = new CardPanel();
        card.setCardImage(new HTML("Unit Image"));

        Label lbl;
        if (!value.name().isNull()) {
            lbl = new Label(value.name().getValue());
            card.setCardHeader(lbl);
        }

        if (!value.area().isNull()) {
            lbl = new Label(value.area().getValue().toString());
            card.setCardContent(lbl);
        }

        return card;

    }
}

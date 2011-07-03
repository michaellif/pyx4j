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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

import com.propertyvista.common.domain.IAddress;
import com.propertyvista.portal.client.MediaUtils;
import com.propertyvista.portal.client.ui.decorations.ApartmentCardDecorator;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class PropertyListForm extends CEntityForm<PropertyListDTO> {

    private PropertyMapView.Presenter presenter;

    protected static I18n i18n = I18nFactory.getI18n(PropertyListForm.class);

    private final String POSTFIX = " \u2022 ";

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
            public IFolderItemViewerDecorator<PropertyDTO> createFolderItemDecorator() {
                return new ApartmentCardDecorator(presenter);
            }

            @Override
            public IsWidget createContent(PropertyDTO value) {
                return createAppartmentCard(value);
            }

        };
    }

    private CardPanel createAppartmentCard(PropertyDTO value) {

        CardPanel card = new CardPanel();
        card.setCardImage(MediaUtils.createPublicMediaImage(value.mainMedia(), ThumbnailSize.medium));

        FlowPanel content = new FlowPanel();

        content.add(formatCardLine(i18n.tr("Address"), formatAddress(value.address())));
        content.add(formatCardLine(i18n.tr("Type"), formatFloorplans(value.floorplanNames())));
        content.add(formatCardLine(i18n.tr("Amenities"), formatAmenities(value.amenities())));
        content.add(formatCardLine(i18n.tr("Notes"), value.description().getStringView()));
        card.setMajorContent(content);

        content = new FlowPanel();
        //from date
        Label item = new Label(i18n.tr("Starting from"));
        content.add(item);
        item = new Label(value.avalableForRent().getStringView());
        item.getElement().getStyle().setMarginBottom(5d, Unit.PX);
        content.add(item);

        //from price
        item = new Label(i18n.tr("from"));
        content.add(item);
        //TODO think of a better way
        item = new Label("$" + value.price().min().getStringView());
        content.add(item);
        card.setMinorContent(content);

        return card;

    }

    private String formatListItem(String item) {
        if (item == null || item.isEmpty()) {
            return "";
        }
        return item.toUpperCase() + POSTFIX;

    }

    private HorizontalPanel formatCardLine(String label, String value) {
        HorizontalPanel item = new HorizontalPanel();
        item.setWidth("100%");
        Label lbl = new Label(label + ":");
        item.add(lbl);
        item.setCellWidth(lbl, "15%");
        lbl = new Label(value);
        item.add(new Label(value));
        item.setCellWidth(lbl, "85%");
        return item;

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
            addrString.append(", ");
            addrString.append(address.province().getStringView());
        }

        if (!address.postalCode().isNull()) {
            addrString.append(" ");
            addrString.append(address.postalCode().getStringView());
        }

        return addrString.toString();
    }

    private String formatAmenities(IList<AmenityDTO> amenities) {
        if (amenities.isNull() || amenities.isEmpty()) {
            return "";
        }
        StringBuffer strbuffer = new StringBuffer();
        for (AmenityDTO amenity : amenities) {
            if (!amenity.isNull() && !amenity.isEmpty()) {
                strbuffer.append(formatListItem(amenity.getStringView()));
            }
        }
        String finalString = strbuffer.toString();
        int idx = finalString.lastIndexOf(POSTFIX);
        if (idx > -1) {
            finalString = finalString.substring(0, idx);
        }

        return finalString;

    }

    private String formatFloorplans(IPrimitiveSet<String> floorplans) {
        if (floorplans.isNull() || floorplans.isEmpty())
            return "";

        StringBuffer strbuffer = new StringBuffer();

        for (String planName : floorplans.getValue()) {
            if (planName != null && !planName.isEmpty()) {
                strbuffer.append(formatListItem(planName));
            }
        }
        String finalString = strbuffer.toString();
        int idx = finalString.lastIndexOf(POSTFIX);
        if (idx > -1) {
            finalString = finalString.substring(0, idx);
        }
        return finalString;
    }

}

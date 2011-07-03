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
import com.pyx4j.entity.client.ui.flex.viewer.CEntityViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.common.domain.RangeGroup;
import com.propertyvista.portal.client.MediaUtils;
import com.propertyvista.portal.client.ui.decorations.FloorplanCardDecorator;
import com.propertyvista.portal.client.ui.decorations.PortalListDecorator;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class ApartmentDetailsForm extends CEntityForm<PropertyDetailsDTO> implements ApartmentDetailsView {

    private ApartmentDetailsView.Presenter presenter;

    private final DecorationData decor;

    private final DecorationData listDecor;

    private final String POSTFIX = " \u2022 ";

    protected static I18n i18n = I18nFactory.getI18n(ApartmentDetailsForm.class);

    public ApartmentDetailsForm() {
        super(PropertyDetailsDTO.class);
        //    decor = new DecorationData(10, Unit.PCT, 90, Unit.PCT);
        decor = new DecorationData(7d, 40);
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

        container.add(new VistaWidgetDecorator(inject(proto().price(), new CEntityViewer<RangeGroup>() {
            @Override
            public IsWidget createContent(RangeGroup value) {
                return new Label(formatRange(value, "$"));
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

    private String formatRange(RangeGroup range, String prefix) {
        if (range.isNull())
            return "";

        //TODO remove $ for production
        StringBuffer rangeString = new StringBuffer((prefix != null ? prefix : ""));
        if (!range.min().isNull()) {
            rangeString.append(range.min().getStringView());
        }

        if (!range.max().isNull()) {
            rangeString.append(" - ");
            rangeString.append(range.max().getStringView());
        }

        return rangeString.toString();

    }

    private Label formatAddress(IAddress address) {

        if (address.isNull())
            return new Label("");

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
                return new FloorplanCardDecorator(presenter);
            }

            @Override
            public IsWidget createContent(FloorplanDTO value) {
                return createFloorplanCard(value);
            }

        };
    }

    private CardPanel createFloorplanCard(FloorplanDTO value) {

        CardPanel card = new CardPanel();
        card.setCardImage(MediaUtils.createPublicMediaImage(value.mainMedia(), ThumbnailSize.medium));

        FlowPanel content = new FlowPanel();

        content.add(formatCardLine(i18n.tr("Type"), value.name().getStringView()));
        content.add(formatCardLine(i18n.tr("Amenities"), formatAmenities(value.amenities())));
        content.add(formatCardLine(i18n.tr("Area"), formatRange(value.area(), "")));
        content.add(formatCardLine(i18n.tr("Notes"), value.description().getStringView()));
        card.setMajorContent(content);

        content = new FlowPanel();
        //from date
        Label item = new Label(i18n.tr("Starting from"));
        content.add(item);
        item = new Label(value.avalableForRent().getStringView());
        item.getElement().getStyle().setMarginBottom(5d, Unit.PX);
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
        item.setCellWidth(lbl, "18%");
        lbl = new Label(value);
        item.add(new Label(value));
        item.setCellWidth(lbl, "82%");
        return item;

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

}

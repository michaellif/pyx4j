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
import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.contact.IAddress;
import com.propertyvista.portal.client.MediaUtils;
import com.propertyvista.portal.client.ui.decorations.FloorplanCardDecorator;
import com.propertyvista.portal.client.ui.decorations.PortalListDecorator;
import com.propertyvista.portal.client.ui.util.Formatter;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class ApartmentDetailsForm extends CEntityForm<PropertyDetailsDTO> implements ApartmentDetailsView {

    private ApartmentDetailsView.Presenter presenter;

    private final DecorationData decor;

    private final DecorationData listDecor;

    protected static I18n i18n = I18nFactory.getI18n(ApartmentDetailsForm.class);

    public ApartmentDetailsForm() {
        super(PropertyDetailsDTO.class);
        //    decor = new DecorationData(10, Unit.PCT, 90, Unit.PCT);
        decor = new DecorationData(7d, 40);
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
                return new Label(Formatter.formatAddress(value));
            }
        }), decor));

        container.add(new VistaWidgetDecorator(inject(proto().price(), new CEntityViewer<RangeGroup>() {
            @Override
            public IsWidget createContent(RangeGroup value) {
                return new Label(Formatter.formatRange(value, "$"));
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

        content.add(Formatter.formatCardLine(i18n.tr("Type"), value.name().getStringView()));
        content.add(Formatter.formatCardLine(i18n.tr("Amenities"), Formatter.formatAmenities(value.amenities())));
        content.add(Formatter.formatCardLine(i18n.tr("Area"), Formatter.formatRange(value.area(), "")));
        content.add(Formatter.formatCardLine(i18n.tr("Notes"), value.description().getStringView()));
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
}

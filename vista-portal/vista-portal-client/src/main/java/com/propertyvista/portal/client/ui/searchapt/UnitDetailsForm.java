/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.domain.RangeGroup;
import com.propertyvista.portal.client.ui.decorations.PortalListDecorator;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityViewer;
import com.pyx4j.entity.shared.IList;

public class UnitDetailsForm extends CEntityForm<FloorplanDetailsDTO> implements UnitDetailsView {

    private final DecorationData decor;

    private UnitDetailsView.Presenter presenter;

    private final DecorationData listDecor;

    public UnitDetailsForm() {
        super(FloorplanDetailsDTO.class);
        decor = new DecorationData(7d, 40);
        decor.editable = false;
        listDecor = new DecorationData(0, Unit.PCT, 100, Unit.PCT);
    }

    @Override
    public void populate(FloorplanDetailsDTO property) {
        super.populate(property);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();

        container.add(new VistaWidgetDecorator(inject(proto().area(), new CEntityViewer<RangeGroup>() {

            @Override
            public IsWidget createContent(RangeGroup value) {
                return formatArea(value);
            }
        }), decor));

        container.add(new VistaWidgetDecorator(inject(proto().buildingAmenities(), new CEntityViewer<IList<AmenityDTO>>() {
            @Override
            public IsWidget createContent(IList<AmenityDTO> value) {
                return new PortalListDecorator(value, "name", listDecor);
            }
        }), decor));

        return container;
    }

    private Label formatArea(RangeGroup area) {
        if (area.isNull())
            return new Label("");

        StringBuffer areaString = new StringBuffer();
        if (!area.min().isNull()) {
            areaString.append(area.min().getStringView());
        }

        if (!area.max().isNull()) {
            areaString.append(" - ");
            areaString.append(area.max().getStringView());
        }

        return new Label(areaString.toString());

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}

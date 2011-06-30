/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.components;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;

import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.ChargeLine.ChargeType;

public class ChargeLineFolder extends CEntityFolderViewer<ChargeLine> {

    public ChargeLineFolder() {
        super(ChargeLine.class);
    }

    @Override
    protected CEntityFolderItemViewer<ChargeLine> createItem() {
        // TODO Auto-generated method stub
        return createChargeLineViewer();
    }

    @Override
    protected IFolderViewerDecorator<ChargeLine> createFolderDecorator() {
        // TODO Auto-generated method stub
        return new BaseFolderViewerDecorator<ChargeLine>();
    }

    private CEntityFolderItemViewer<ChargeLine> createChargeLineViewer() {

        return new CEntityFolderItemViewer<ChargeLine>() {

            @Override
            public IFolderItemViewerDecorator<ChargeLine> createFolderItemDecorator() {
                return new BaseFolderItemViewerDecorator<ChargeLine>() {

                };
            }

            @Override
            public IsWidget createContent(ChargeLine value) {
                return createChargeLine(value);
            }

        };
    }

    private IsWidget createChargeLine(final ChargeLine chargeLine) {
        HorizontalPanel container = new HorizontalPanel();
        container.setWidth("100%");
        Label lbl = new Label(chargeLine.type().getStringView());
        container.add(lbl);
        container.setCellWidth(lbl, "50%");
        container.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_LEFT);
        lbl = new Label(chargeLine.charge().getStringView());
        if (chargeLine.type().getValue() == ChargeType.monthlyRent) {
            container.getElement().getStyle().setFontSize(2d, Unit.EM);
            container.getElement().getStyle().setPaddingBottom(10d, Unit.PX);
            container.getElement().getStyle().setProperty("borderBottom", "1px dotted");
        } else {
            container.getElement().getStyle().setPaddingBottom(5d, Unit.PX);
            container.getElement().getStyle().setFontSize(1.2d, Unit.EM);
        }

        container.add(lbl);
        container.setCellWidth(lbl, "50%");
        container.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_RIGHT);
        container.setWidth("100%");

        return container;
    }
}

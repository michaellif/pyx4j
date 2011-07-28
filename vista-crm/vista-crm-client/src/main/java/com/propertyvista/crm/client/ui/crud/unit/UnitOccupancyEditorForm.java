/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;

public class UnitOccupancyEditorForm extends CrmEntityForm<AptUnitOccupancy> {

    public UnitOccupancyEditorForm() {
        super(AptUnitOccupancy.class, new CrmEditorsComponentFactory());
    }

    public UnitOccupancyEditorForm(IEditableComponentFactory factory) {
        super(AptUnitOccupancy.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().dateFrom()), 8.2);
        main.add(inject(proto().dateTo()), 8.2);

        main.add(inject(proto().offMarket()), 10);
        main.add(inject(proto().description()), 25);

        main.add(inject(proto().lease()), 15);

        return new CrmScrollPanel(main);
    }
}
/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.portal.domain.AptUnit;

public class UnitEditorForm extends CEntityForm<AptUnit> {

    private static I18n i18n = I18nFactory.getI18n(UnitEditorForm.class);

    public UnitEditorForm() {
        super(AptUnit.class);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeaderDecorator(i18n.tr("Details")));

        DecorationData decorData = new DecorationData(14d, 12);
        main.add(new VistaWidgetDecorator(inject(proto().name()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketingName()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().unitType()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().unitEcomomicStatus()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().unitEcomomicStatusDescr()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().floor()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().suiteNumber()), decorData));
// TODO: arrange available building in drop-down box? 
//        main.add(new VistaWidgetDecorator(inject(proto().building()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().area()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().areaMeasurementType()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().bedrooms()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().bathrooms()), decorData));

// TODO: complex data editing here: 
//        main.add(new VistaWidgetDecorator(inject(proto().currentOccupancies()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().avalableForRent()), decorData));

// TODO: market rent list goes here: 
//        main.add(new VistaWidgetDecorator(inject(proto().marketRent()), decorData));
// TODO: arrange available Lease Terms in drop-down box? 
//      main.add(new VistaWidgetDecorator(inject(proto().newLeaseTerms()), decorData));
// TODO: arrange available floorplans in drop-down box? 
//      main.add(new VistaWidgetDecorator(inject(proto().floorplan()), decorData));

//        main.add(new VistaWidgetDecorator(inject(proto().requiredDeposit()), decorData));

// TODO: amenities list goes here: 
//      main.add(new VistaWidgetDecorator(inject(proto().amenities()), decorData));
// TODO: utilities list goes here: 
//      main.add(new VistaWidgetDecorator(inject(proto().utilities()), decorData));
// TODO: infoDetails list goes here: 
//      main.add(new VistaWidgetDecorator(inject(proto().infoDetails()), decorData));
// TODO: concessions list goes here: 
//      main.add(new VistaWidgetDecorator(inject(proto().concessions()), decorData));
// TODO: addOns list goes here: 
//      main.add(new VistaWidgetDecorator(inject(proto().addOns()), decorData));

        main.setWidth("100%");
        return main;
    }

}

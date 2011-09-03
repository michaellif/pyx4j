/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.rpc.ptapp.dto.UnitInfoDTO;

public class ApartmentViewForm extends CEntityForm<UnitInfoDTO> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    public ApartmentViewForm() {
        super(UnitInfoDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new VistaHeaderBar(i18n.tr("General Info")));
        main.add(inject(proto().name()), 20);

        main.add(new VistaLineSeparator(100, Unit.PCT));

        VistaDecoratorsSplitFlowPanel split;
        main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 12, 30));

        split.getLeftPanel().add(inject(proto().number()), 15);
        split.getLeftPanel().add(inject(proto().area()), 15);

        split.getRightPanel().add(inject(proto().beds()), 15);
        split.getRightPanel().add(inject(proto().baths()), 15);

        main.add(new VistaHeaderBar(i18n.tr("Utilities")));

        main.add(new VistaHeaderBar(i18n.tr("Available Add-ons")));

        main.add(new VistaHeaderBar(i18n.tr("Concession")));

        main.add(new VistaHeaderBar(i18n.tr("Lease Terms")));
        main.add(inject(proto().leaseFrom()), 8);
        main.add(inject(proto().leaseTo()), 8);

        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        content.add(main);
        content.add(new BuildingPicture());
        return content;
    }
}

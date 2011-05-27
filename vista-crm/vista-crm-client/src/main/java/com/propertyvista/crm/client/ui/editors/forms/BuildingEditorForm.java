/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors.forms;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.Address;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;

public class BuildingEditorForm extends CrmEntityForm<BuildingDTO> {

    public BuildingEditorForm() {
        super(BuildingDTO.class, new CrmEditorsComponentFactory());
    }

    public BuildingEditorForm(IEditableComponentFactory factory) {
        super(BuildingDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeaderDecorator(i18n.tr("Information")));
        main.add(inject(proto().info().name()), 15);
        injectAddress(main, proto().info().address());
        main.add(inject(proto().info().propertyCode()), 15);
        main.add(inject(proto().info().type()), 15);
        main.add(inject(proto().info().shape()), 15);
        main.add(inject(proto().info().structureType()), 15);
        main.add(inject(proto().info().totalStories()), 15);
        main.add(inject(proto().info().residentialStories()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr("Details")));
        main.add(inject(proto().elevators(), createElevatorsListEditor()));
        //main.add(inject(proto().boilers()), 15);
        //main.add(inject(proto().roof()), 15);
        //main.add(inject(proto().parkings()), 15);
        //main.add(inject(proto().lockers()), 15);
        //main.add(inject(proto().amenities()), 15);
        main.add(inject(proto().info().constructionType()), 15);
        main.add(inject(proto().info().foundationType()), 15);
        main.add(inject(proto().info().centralAir()), 15);
        main.add(inject(proto().info().centralHeat()), 15);
        main.add(inject(proto().info().floorType()), 15);
        main.add(inject(proto().info().landArea()), 15);
        main.add(inject(proto().info().waterSupply()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr("Marketing")));
        main.add(inject(proto().marketing().name()), 15);
        main.add(inject(proto().marketing().description()), 15);
//        main.add(inject(proto().media()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr("Financials")));
        main.add(inject(proto().financial().dateAquired()), 15);
        main.add(inject(proto().financial().purchasePrice()), 15);
        main.add(inject(proto().financial().marketPrice()), 15);
        main.add(inject(proto().financial().lastAppraisalDate()), 15);
        main.add(inject(proto().financial().lastAppraisalValue()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr("Contact Information")));
        main.add(inject(proto().contacts().website()), 15);
//        main.add(inject(proto().email()), 15);
        SubtypeInjectors.injectPhones(main, proto().contacts().phones(), this);
        // TODO - add this complex data processing later! :
//        main.add(inject(proto().contactsList()), 15);

// TODO - add this complex data processing later! :
//        main.add(inject(proto().complex()), 15);

        main.setWidth("100%");
        return main;
    }

    private void injectAddress(final VistaDecoratorsFlowPanel main, final Address address) {
        AddressUtils.injectIAddress(main, address, this);
        main.add(inject(address.addressType()), 12);
    }

    private CEntityFolder<ElevatorDTO> createElevatorsListEditor() {
        AppPlace placeToGo = (isEditable() ? new CrmSiteMap.Editors.Elevator() : new CrmSiteMap.Viewers.Elevator());
        return new CrmEntityFolder<ElevatorDTO>(ElevatorDTO.class, "Elevator", isEditable(), placeToGo, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().isForMoveInOut(), "5em"));
                return columns;
            }
        };
    }
}

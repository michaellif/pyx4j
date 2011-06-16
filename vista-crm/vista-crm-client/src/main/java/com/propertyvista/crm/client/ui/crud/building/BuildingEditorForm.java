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
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.widgets.client.TabLayoutPanel;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingEditorForm extends CrmEntityForm<BuildingDTO> {

    public BuildingEditorForm() {
        super(BuildingDTO.class, new CrmEditorsComponentFactory());
    }

    public BuildingEditorForm(IEditableComponentFactory factory) {
        super(BuildingDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        // TODO - add this data processing later! :
        //  main.add(inject(proto().contactsList()), 15);
        // TODO - add this data processing later! :
        //  main.add(inject(proto().media()), 15);

        TabLayoutPanel tabPanel = new TabLayoutPanel(2.7, Unit.EM);

        tabPanel.add(new ScrollPanel(createGeneralTab()), "General");
        tabPanel.add(new ScrollPanel(createDetailsTab()), "Details");

        asWidget().setSize("100%", "100%");
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();
        main.add(split);
        split.getLeftPanel().add(inject(proto().info().name()), 15);
        split.getLeftPanel().add(inject(proto().info().propertyCode()), 12);
        split.getLeftPanel().add(inject(proto().info().type()), 12);
        split.getRightPanel().add(inject(proto().info().shape()), 7);
        split.getRightPanel().add(inject(proto().info().totalStories()), 5);
        split.getRightPanel().add(inject(proto().info().residentialStories()), 5);
        injectAddress(split, proto().info().address());
        split.getRightPanel().add(inject(proto().complex()), 15);

        main.add(new CrmHeader2Decorator(proto().amenities().getMeta().getCaption()));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmHeader2Decorator(proto().elevators().getMeta().getCaption()));
        main.add(inject(proto().elevators(), createElevatorsListEditor()));

        main.add(new CrmHeader2Decorator(proto().boilers().getMeta().getCaption()));
        main.add(inject(proto().boilers(), createBoilersListEditor()));

        main.add(new CrmHeader2Decorator(proto().roofs().getMeta().getCaption()));
        main.add(inject(proto().roofs(), createRoofsListEditor()));

        main.add(new CrmHeader2Decorator(proto().parkings().getMeta().getCaption()));
        main.add(inject(proto().parkings(), createParkingsListEditor()));

        main.add(new CrmHeader2Decorator(proto().lockers().getMeta().getCaption()));
        main.add(inject(proto().lockers(), createLockerAreasListEditor()));

        main.add(new CrmHeader1Decorator(i18n.tr("Financials")));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        split.getLeftPanel().add(inject(proto().financial().dateAquired()), 8.2);
        split.getLeftPanel().add(inject(proto().financial().purchasePrice()), 5);
        split.getLeftPanel().add(inject(proto().financial().marketPrice()), 5);
        split.getRightPanel().add(inject(proto().financial().lastAppraisalDate()), 8.2);
        split.getRightPanel().add(inject(proto().financial().lastAppraisalValue()), 5);
        split.getRightPanel().add(inject(proto().financial().currency().name()), split.getRightPanel().getDefaultLabelWidth(), 10, i18n.tr("Currency Name"));

        main.add(new CrmHeader1Decorator(i18n.tr("Marketing")));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        SubtypeInjectors.injectMarketing(main, split, proto().marketing(), this);

        main.add(new CrmHeader1Decorator(i18n.tr("Contact Information")));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        split.getLeftPanel().add(inject(proto().contacts().website()), 23);
        split.getRightPanel().add(inject(proto().contacts().email().address()), split.getRightPanel().getDefaultLabelWidth(), 23, i18n.tr("Email Address"));
        SubtypeInjectors.injectPhones(main, proto().contacts().phones(), this);
        return main;
    }

    private Widget createDetailsTab() {
        VistaDecoratorsFlowPanel details = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();
        details.add(split);
        split.getLeftPanel().add(inject(proto().info().structureType()), 15);
        split.getLeftPanel().add(inject(proto().info().structureBuildYear()), 15);
        split.getLeftPanel().add(inject(proto().info().constructionType()), 15);
        split.getLeftPanel().add(inject(proto().info().foundationType()), 15);
        split.getLeftPanel().add(inject(proto().info().floorType()), 15);
        split.getRightPanel().add(inject(proto().info().landArea()), 15);
        split.getRightPanel().add(inject(proto().info().waterSupply()), 15);
        split.getRightPanel().add(inject(proto().info().centralAir()), 15);
        split.getRightPanel().add(inject(proto().info().centralHeat()), 15);
        return details;
    }

    private void injectAddress(final VistaDecoratorsSplitFlowPanel split, final Address address) {
        AddressUtils.injectIAddress(split, address, this);
        split.getLeftPanel().add(inject(address.addressType()), 12);
    }

    private CEntityFolderEditor<BuildingAmenity> createAmenitiesListEditor() {
        return new CrmEntityFolder<BuildingAmenity>(BuildingAmenity.class, i18n.tr("Amenity"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().subType(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().rank(), "3em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().rent(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().deposit(), "5em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<ElevatorDTO> createElevatorsListEditor() {
        return new CrmEntityFolder<ElevatorDTO>(ElevatorDTO.class, i18n.tr("Elevator"), isEditable(), CrmSiteMap.Properties.Elevator.class, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().isForMoveInOut(), "5em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<BoilerDTO> createBoilersListEditor() {
        return new CrmEntityFolder<BoilerDTO>(BoilerDTO.class, i18n.tr("Boiler"), isEditable(), CrmSiteMap.Properties.Boiler.class, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "15em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<RoofDTO> createRoofsListEditor() {
        return new CrmEntityFolder<RoofDTO>(RoofDTO.class, i18n.tr("Roof"), isEditable(), CrmSiteMap.Properties.Roof.class, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "8.2em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<ParkingDTO> createParkingsListEditor() {
        return new CrmEntityFolder<ParkingDTO>(ParkingDTO.class, i18n.tr("Parking"), isEditable(), CrmSiteMap.Properties.Parking.class, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<LockerAreaDTO> createLockerAreasListEditor() {
        return new CrmEntityFolder<LockerAreaDTO>(LockerAreaDTO.class, i18n.tr("Locker Area"), isEditable(), CrmSiteMap.Properties.LockerArea.class, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().totalLockers(), "5em"));
                return columns;
            }
        };
    }

}

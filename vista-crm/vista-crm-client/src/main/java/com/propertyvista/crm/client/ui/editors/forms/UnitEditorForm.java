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
package com.propertyvista.crm.client.ui.editors.forms;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorForm extends CrmEntityForm<AptUnitDTO> {

    public UnitEditorForm() {
        super(AptUnitDTO.class, new CrmEditorsComponentFactory());
    }

    public UnitEditorForm(IEditableComponentFactory factory) {
        super(AptUnitDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        Widget header = new CrmHeaderDecorator(i18n.tr("Details"));
        header.getElement().getStyle().setMarginTop(0, Unit.EM); // remove default for header top margin...
        main.add(header);

        main.add(inject(proto().info().name()), 15);
        main.add(inject(proto().marketing().name()), 15);

        main.add(inject(proto().info().type()), 15);

        main.add(inject(proto().info().economicStatus()), 15);
        main.add(inject(proto().info().economicStatusDescription()), 15);

        main.add(inject(proto().info().floor()), 15);
        main.add(inject(proto().info().number()), 15);
        main.add(inject(proto().belongsTo()), 15);

        main.add(inject(proto().info().area()), 15);
        main.add(inject(proto().info().areaUnits()), 15);

        main.add(inject(proto().info().bedrooms()), 15);
        main.add(inject(proto().info().bathrooms()), 15);

// TODO: complex data editing here: 
//        main.add(inject(proto().currentOccupancies()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr("Rents")));
        main.add(inject(proto().financial().unitRent()), 15);
        main.add(inject(proto().financial().marketRent()), 15);

// TODO: arrange available Lease Terms in drop-down box? 
//      main.add(inject(proto().newLeaseTerms()), 15);
// TODO: arrange available floorplans in drop-down box? 
//      main.add(inject(proto().floorplan()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr(proto().amenities().getMeta().getCaption())));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().info().utilities().getMeta().getCaption())));
        main.add(inject(proto().info().utilities(), createUtilitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Info Details")));
        main.add(inject(proto().details(), createDetailsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().occupancies().getMeta().getCaption())));
        main.add(inject(proto().occupancies(), createOccupanciesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().concessions().getMeta().getCaption())));
        main.add(inject(proto().concessions(), createConcessionsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().addOns().getMeta().getCaption())));
        main.add(inject(proto().addOns(), createAddOnsListEditor()));

        main.setWidth("100%");
        return main;
    }

    private CEntityFolder<AptUnitAmenity> createAmenitiesListEditor() {
        return new CrmEntityFolder<AptUnitAmenity>(AptUnitAmenity.class, "Amenity", isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().subType(), "12em"));
                columns.add(new EntityFolderColumnDescriptor(proto().rank(), "3em"));
                columns.add(new EntityFolderColumnDescriptor(proto().rent(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().deposit(), "5em"));
                return columns;
            }
        };
    }

    private CEntityFolder<Utility> createUtilitiesListEditor() {
        return new CrmEntityFolder<Utility>(Utility.class, "Utility", isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "35em"));
                return columns;
            }
        };
    }

    private CEntityFolder<AptUnitItem> createDetailsListEditor() {
        AppPlace placeToGo = (isEditable() ? new CrmSiteMap.Editors.UnitItem() : new CrmSiteMap.Viewers.UnitItem());
        return new CrmEntityFolder<AptUnitItem>(AptUnitItem.class, "Unit Item", isEditable(), placeToGo, this) {

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().conditionNotes(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().wallColour(), "8em"));
                return columns;
            }
        };
    }

    private CEntityFolder<AptUnitOccupancy> createOccupanciesListEditor() {
        AppPlace placeToGo = (isEditable() ? new CrmSiteMap.Editors.UnitOccupancy() : new CrmSiteMap.Viewers.UnitOccupancy());
        return new CrmEntityFolder<AptUnitOccupancy>(AptUnitOccupancy.class, "Unit Occupancy", isEditable(), placeToGo, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().dateFrom(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().dateTo(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().offMarket(), "8em"));
                return columns;
            }
        };
    }

    private CEntityFolder<Concession> createConcessionsListEditor() {
        AppPlace placeToGo = (isEditable() ? new CrmSiteMap.Editors.Concession() : new CrmSiteMap.Viewers.Concession());
        return new CrmEntityFolder<Concession>(Concession.class, "Concession", isEditable(), placeToGo, this) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().appliedTo(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().percentage(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().start(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().end(), "8.2em"));
                return columns;
            }
        };
    }

    private CEntityFolder<AddOn> createAddOnsListEditor() {
        return new CrmEntityFolder<AddOn>(AddOn.class, "Add-on", isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().term(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                return columns;
            }
        };
    }
}

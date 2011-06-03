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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
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
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().info().name()), 15);
        split.getLeftPanel().add(inject(proto().marketing().name()), 15);

        split.getLeftPanel().add(inject(proto().info().type()), 15);

        split.getLeftPanel().add(inject(proto().info().economicStatus()), 15);
        split.getLeftPanel().add(inject(proto().info().economicStatusDescription()), 15);

        split.getLeftPanel().add(inject(proto().info().floor()), 15);
        split.getRightPanel().add(inject(proto().info().number()), 15);
        split.getRightPanel().add(inject(proto().belongsTo()), 15);

        split.getRightPanel().add(inject(proto().info().area()), 15);
        split.getRightPanel().add(inject(proto().info().areaUnits()), 15);

        split.getRightPanel().add(inject(proto().info().bedrooms()), 15);
        split.getRightPanel().add(inject(proto().info().bathrooms()), 15);

        main.add(new CrmHeader1Decorator(i18n.tr("Details")));
        main.add(inject(proto().details(), createDetailsListEditor()));

        main.add(new CrmHeader1Decorator(i18n.tr(proto().info().utilities().getMeta().getCaption())));
        main.add(inject(proto().info().utilities(), createUtilitiesListEditor()));

        main.add(new CrmHeader1Decorator(i18n.tr(proto().addOns().getMeta().getCaption())));
        main.add(inject(proto().addOns(), createAddOnsListEditor()));

        main.add(new CrmHeader1Decorator(i18n.tr(proto().amenities().getMeta().getCaption())));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmHeader1Decorator(i18n.tr(proto().occupancies().getMeta().getCaption())));
        main.add(inject(proto().occupancies(), createOccupanciesListEditor()));

        main.add(new CrmHeader1Decorator(i18n.tr("Financials")));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        split.getLeftPanel().add(inject(proto().financial().unitRent()), 15);
        split.getRightPanel().add(inject(proto().financial().marketRent()), 15);

        main.add(new CrmHeader1Decorator(i18n.tr(proto().financial().concessions().getMeta().getCaption())));
        main.add(inject(proto().financial().concessions(), createConcessionsListEditor()));
// just select from predefines ones:        
//        main.add(inject(proto().financial().concessions()), 15);

        main.add(new CrmHeader1Decorator(i18n.tr("Marketing")));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        SubtypeInjectors.injectMarketing(main, split, proto().marketing(), this);
        split.getLeftPanel().add(inject(proto().marketing().floorplan()), 15);

        main.setWidth("100%");
        return main;
    }

    private CEntityFolderEditor<AptUnitAmenity> createAmenitiesListEditor() {
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

    private CEntityFolderEditor<Utility> createUtilitiesListEditor() {
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

    private CEntityFolderEditor<AptUnitItem> createDetailsListEditor() {
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

    private CEntityFolderEditor<AptUnitOccupancy> createOccupanciesListEditor() {
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

    private CEntityFolderEditor<Concession> createConcessionsListEditor() {
        AppPlace placeToGo = (isEditable() ? new CrmSiteMap.Editors.Concession() : new CrmSiteMap.Viewers.Concession());
        return SubtypeInjectors.injectConcessions(isEditable(), placeToGo, this);
    }

    private CEntityFolderEditor<AddOn> createAddOnsListEditor() {
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

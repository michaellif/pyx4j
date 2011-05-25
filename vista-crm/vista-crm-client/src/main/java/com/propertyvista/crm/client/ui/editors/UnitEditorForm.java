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

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorForm extends CrmEntityForm<AptUnitDTO> {

    private static I18n i18n = I18nFactory.getI18n(UnitEditorForm.class);

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

        DecorationData decorData = new DecorationData(14d, 12);
        main.add(new VistaWidgetDecorator(inject(proto().info().name()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketing().name()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().info().type()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().info().economicStatus()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().economicStatusDescription()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().info().floor()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().number()), decorData));
// TODO: arrange available building in drop-down box? 
//        main.add(new VistaWidgetDecorator(inject(proto().building()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().info().area()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().areaUnits()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().info().bedrooms()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().bathrooms()), decorData));

// TODO: complex data editing here: 
//        main.add(new VistaWidgetDecorator(inject(proto().currentOccupancies()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Rents")));
        main.add(new VistaWidgetDecorator(inject(proto().financial().unitRent()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().financial().marketRent()), decorData));

// TODO: arrange available Lease Terms in drop-down box? 
//      main.add(new VistaWidgetDecorator(inject(proto().newLeaseTerms()), decorData));
// TODO: arrange available floorplans in drop-down box? 
//      main.add(new VistaWidgetDecorator(inject(proto().floorplan()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().amenities().getFieldName())));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().info().utilities().getFieldName())));
        main.add(inject(proto().info().utilities(), createUtilitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Info Details")));
        main.add(inject(proto().info().details(), createDetailsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().concessions().getFieldName())));
        main.add(inject(proto().concessions(), createConcessionsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr(proto().addOns().getFieldName())));
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
        return new CrmEntityFolder<AptUnitItem>(AptUnitItem.class, "Unit Item", isEditable(), placeToGo) {

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

    private CEntityFolder<Concession> createConcessionsListEditor() {
        return new CrmEntityFolder<Concession>(Concession.class, "Concession", isEditable()) {
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

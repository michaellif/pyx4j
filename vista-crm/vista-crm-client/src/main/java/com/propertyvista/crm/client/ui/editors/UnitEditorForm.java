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
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitEditorForm extends CEntityForm<AptUnit> {

    private static I18n i18n = I18nFactory.getI18n(UnitEditorForm.class);

    public UnitEditorForm() {
        super(AptUnit.class, new CrmEditorsComponentFactory());
    }

    public UnitEditorForm(IEditableComponentFactory factory) {
        super(AptUnit.class, factory);
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

        main.add(new CrmHeaderDecorator(i18n.tr("Amenities")));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Utilities")));
        main.add(inject(proto().info().utilities(), createUtilitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Info Details")));
        main.add(inject(proto().info().details(), createDetailsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Concessions")));
        main.add(inject(proto().concessions(), createConcessionsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Add-ons")));
        main.add(inject(proto().addOns(), createAddOnsListEditor()));

        main.setWidth("100%");
        return main;
    }

    private CEntityFolder<AptUnitAmenity> createAmenitiesListEditor() {
        return new CEntityFolder<AptUnitAmenity>(AptUnitAmenity.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "45em"));
            }

            @Override
            protected FolderDecorator<AptUnitAmenity> createFolderDecorator() {
                return new TableFolderDecorator<AptUnitAmenity>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add AptUnitAmenity"));
            }

            @Override
            protected CEntityFolderItem<AptUnitAmenity> createItem() {
                return new CEntityFolderRow<AptUnitAmenity>(AptUnitAmenity.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove AptUnitAmenity"));
                    }
                };
            }
        };
    }

    private CEntityFolder<Utility> createUtilitiesListEditor() {
        return new CEntityFolder<Utility>(Utility.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "45em"));
            }

            @Override
            protected FolderDecorator<Utility> createFolderDecorator() {
                return new TableFolderDecorator<Utility>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add Utility"));
            }

            @Override
            protected CEntityFolderItem<Utility> createItem() {
                return new CEntityFolderRow<Utility>(Utility.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove Utility"));
                    }
                };
            }
        };
    }

    private CEntityFolder<AptUnitItem> createDetailsListEditor() {
        return new CEntityFolder<AptUnitItem>(AptUnitItem.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "45em"));
            }

            @Override
            protected FolderDecorator<AptUnitItem> createFolderDecorator() {
                return new TableFolderDecorator<AptUnitItem>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add Unit Info"));
            }

            @Override
            protected CEntityFolderItem<AptUnitItem> createItem() {
                return new CEntityFolderRow<AptUnitItem>(AptUnitItem.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove Unit Info"));
                    }
                };
            }
        };
    }

    private CEntityFolder<Concession> createConcessionsListEditor() {
        return new CEntityFolder<Concession>(Concession.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "11em"));
                columns.add(new EntityFolderColumnDescriptor(proto().percentage(), "5em"));
            }

            @Override
            protected FolderDecorator<Concession> createFolderDecorator() {
                return new TableFolderDecorator<Concession>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add Concession"));
            }

            @Override
            protected CEntityFolderItem<Concession> createItem() {
                return new CEntityFolderRow<Concession>(Concession.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove Concession"));
                    }
                };
            }
        };
    }

    private CEntityFolder<AddOn> createAddOnsListEditor() {
        return new CEntityFolder<AddOn>(AddOn.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "40em"));
                columns.add(new EntityFolderColumnDescriptor(proto().term(), "5em"));
            }

            @Override
            protected FolderDecorator<AddOn> createFolderDecorator() {
                return new TableFolderDecorator<AddOn>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add add-on"));
            }

            @Override
            protected CEntityFolderItem<AddOn> createItem() {
                return new CEntityFolderRow<AddOn>(AddOn.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove add-on"));
                    }
                };
            }
        };
    }
}

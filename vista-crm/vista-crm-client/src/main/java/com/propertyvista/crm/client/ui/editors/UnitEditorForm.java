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

import com.google.gwt.user.client.ui.IsWidget;

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
import com.propertyvista.portal.domain.AddOn;
import com.propertyvista.portal.domain.Amenity;
import com.propertyvista.portal.domain.AptUnit;
import com.propertyvista.portal.domain.Concession;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.UnitInfoItem;
import com.propertyvista.portal.domain.Utility;

public class UnitEditorForm extends CEntityForm<AptUnit> {

    private static I18n i18n = I18nFactory.getI18n(UnitEditorForm.class);

    public UnitEditorForm() {
        super(AptUnit.class, new CrmEditorsComponentFactory());
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
        main.add(new VistaWidgetDecorator(inject(proto().requiredDeposit()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Market Rents")));
        main.add(inject(proto().marketRent(), createMarketRentListEditor()));

// TODO: arrange available Lease Terms in drop-down box? 
//      main.add(new VistaWidgetDecorator(inject(proto().newLeaseTerms()), decorData));
// TODO: arrange available floorplans in drop-down box? 
//      main.add(new VistaWidgetDecorator(inject(proto().floorplan()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Amenities")));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Utilities")));
        main.add(inject(proto().utilities(), createUtilitiesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Info Details")));
        main.add(inject(proto().infoDetails(), createInfoDetailsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Concessions")));
        main.add(inject(proto().concessions(), createConcessionsListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Add-ons")));
        main.add(inject(proto().addOns(), createAddOnsListEditor()));

        main.setWidth("100%");
        return main;
    }

    private CEntityFolder<MarketRent> createMarketRentListEditor() {
        return new CEntityFolder<MarketRent>(MarketRent.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().leaseTerm(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().rent(), "10em"));
            }

            @Override
            protected FolderDecorator<MarketRent> createFolderDecorator() {
                return new TableFolderDecorator<MarketRent>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add Rent"));
            }

            @Override
            protected CEntityFolderItem<MarketRent> createItem() {
                return new CEntityFolderRow<MarketRent>(MarketRent.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove Rent"));
                    }
                };
            }
        };
    }

    private CEntityFolder<Amenity> createAmenitiesListEditor() {
        return new CEntityFolder<Amenity>(Amenity.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "45em"));
            }

            @Override
            protected FolderDecorator<Amenity> createFolderDecorator() {
                return new TableFolderDecorator<Amenity>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add Amenity"));
            }

            @Override
            protected CEntityFolderItem<Amenity> createItem() {
                return new CEntityFolderRow<Amenity>(Amenity.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove Amenity"));
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
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "45em"));
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

    private CEntityFolder<UnitInfoItem> createInfoDetailsListEditor() {
        return new CEntityFolder<UnitInfoItem>(UnitInfoItem.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "45em"));
            }

            @Override
            protected FolderDecorator<UnitInfoItem> createFolderDecorator() {
                return new TableFolderDecorator<UnitInfoItem>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add Unit Info"));
            }

            @Override
            protected CEntityFolderItem<UnitInfoItem> createItem() {
                return new CEntityFolderRow<UnitInfoItem>(UnitInfoItem.class, columns) {

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
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "35em"));
                columns.add(new EntityFolderColumnDescriptor(proto().freeMonths(), "5em"));
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
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "40em"));
                columns.add(new EntityFolderColumnDescriptor(proto().monthlyCost(), "5em"));
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

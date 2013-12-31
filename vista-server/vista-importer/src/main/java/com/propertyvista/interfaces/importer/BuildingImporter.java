/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanAmenityConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.AptUnitOccupancyIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanAmenityIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingImporter extends ImportPersister {

    private final static Logger log = LoggerFactory.getLogger(BuildingImporter.class);

    private static final I18n i18n = I18n.get(BuildingImporter.class);

    public List<String> verify(BuildingIO buildingIO, MediaConfig mediaConfig) {
        List<String> messages = new Vector<String>();
        // Set defaults
        if (buildingIO.type().isNull()) {
            buildingIO.type().setValue(BuildingInfo.Type.residential);
        }

        if (buildingIO.propertyCode().isNull() && buildingIO.externalId().isNull()) {
            messages.add("Both propertyCode and externalId are empty");
        }

        // Media
        if (!mediaConfig.ignoreMissingMedia) {
            for (MediaIO iIO : buildingIO.medias()) {
                String m = new MediaConverter(mediaConfig, ImageTarget.Building).verify(iIO);
                if (m != null) {
                    messages.add("Building '" + buildingIO.propertyCode().getValue() + "' " + m);
                }
            }
        }

        Set<String> duplicates = new HashSet<String>();

        //    Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                if (floorplanIO.name().isNull()) {
                    messages.add("Floorplan name in building '" + buildingIO.propertyCode().getValue() + "' can't be empty");
                }

                // Media
                if (!mediaConfig.ignoreMissingMedia) {
                    for (MediaIO iIO : floorplanIO.medias()) {
                        String m = new MediaConverter(mediaConfig, ImageTarget.Floorplan).verify(iIO);
                        if (m != null) {
                            messages.add("Floorplan '" + floorplanIO.name().getValue() + "' in building '" + buildingIO.propertyCode().getValue() + "' " + m);
                        }
                    }
                }
                //Units
                {
                    for (AptUnitIO iIO : floorplanIO.units()) {
                        if (iIO.number().isNull()) {
                            messages.add("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '" + buildingIO.propertyCode().getValue()
                                    + "' can't be empty");
                        } else {
                            String number = AptUnitConverter.trimUnitNumber(iIO.number().getValue());
                            if (duplicates.contains(number)) {
                                messages.add("AptUnit number " + number + " in '" + floorplanIO.name().getValue() + "' in building '"
                                        + buildingIO.propertyCode().getValue() + "' alredy exists");
                            } else {
                                duplicates.add(number);
                            }
                        }
                    }
                }

            }
        }
        return messages;
    }

    public List<String> verifyExists(BuildingIO buildingIO, MediaConfig mediaConfig) { //used for Update Data, units have to exist as no new ones are created

        List<String> messages = new Vector<String>();
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        if (buildingIO.propertyCode().isNull()) {
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().externalId(), buildingIO.externalId().getValue()));
        } else {
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
        }

        List<Building> buildings = Persistence.service().query(buildingCriteria);
        if (buildings.size() == 0) {
            messages.add("Building '" + buildingIO.propertyCode().getValue() + "' with externalId '" + buildingIO.externalId().getValue() + "' not found");
        }

        IList<AptUnitIO> u = buildingIO.units();
        for (AptUnitIO unitIO : u) {
            String number = AptUnitConverter.trimUnitNumber(unitIO.number().getValue());
            EntityQueryCriteria<AptUnit> unitCriteria = EntityQueryCriteria.create(AptUnit.class);
            unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().info().number(), number));
            List<AptUnit> units = Persistence.service().query(unitCriteria);

            if (units.size() == 0) {
                messages.add("Unit '" + unitIO.number() + "' not found.");
            }
        }

        return messages;
    }

    public ImportCounters persist(BuildingIO buildingIO, MediaConfig mediaConfig) {

        ImportCounters counters = new ImportCounters();
        counters.buildings++;

        if (buildingIO.propertyCode().isNull() && buildingIO.externalId().isNull()) {
            throw new UserRuntimeException("Both propertyCode and externalId are empty");
        }
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            if (buildingIO.propertyCode().isNull()) {
                criteria.add(PropertyCriterion.eq(criteria.proto().externalId(), buildingIO.externalId().getValue()));
            } else {
                criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            }
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() != 0) {
                throw new UserRuntimeException("Building '" + buildingIO.propertyCode().getValue() + "' with externalId '" + buildingIO.externalId().getValue()
                        + "' already exists");
            }
        }

        // Save building
        Building building = createBuilding(buildingIO, mediaConfig);

        List<ProductItem> products = new ArrayList<ProductItem>();

        ARCode arCode = null;

        if (VistaFeatures.instance().productCatalog()) {
            EntityQueryCriteria<ARCode> serviceCriteria = EntityQueryCriteria.create(ARCode.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ARCode.Type.Residential));
            arCode = Persistence.service().retrieve(serviceCriteria);
        }

        //Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                Floorplan floorplan = createFloorplan(floorplanIO, building, mediaConfig);

                counters.floorplans += 1;

                //FloorplanAmenity
                {
                    List<FloorplanAmenity> items = new Vector<FloorplanAmenity>();
                    for (FloorplanAmenityIO iIO : floorplanIO.amenities()) {
                        FloorplanAmenity i = new FloorplanAmenityConverter().createBO(iIO);
                        i.floorplan().set(floorplan);
                        if (i.type().isNull()) {
                            i.type().setValue(FloorplanAmenity.Type.other);
                        }
                        i.orderInParent().setValue(items.size());
                        items.add(i);
                    }
                    Persistence.service().persist(items);
                }

                //Units
                {
                    List<AptUnit> items = new Vector<AptUnit>();
                    for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                        if (aptUnitIO.number().isNull()) {
                            throw new UserRuntimeException("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '"
                                    + buildingIO.propertyCode().getValue() + "' can't be empty");
                        }
                        aptUnitIO.number().setValue(AptUnitConverter.trimUnitNumber(aptUnitIO.number().getValue()));
                        {
                            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                            criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                            List<AptUnit> units = Persistence.service().query(criteria);
                            if (units.size() != 0) {
                                throw new UserRuntimeException("AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue()
                                        + "' in '" + buildingIO.propertyCode().getValue() + "' already exists");
                            }
                        }

                        AptUnit i = new AptUnitConverter().createBO(aptUnitIO);
                        i.building().set(building);
                        i.floorplan().set(floorplan);
                        i.info()._bathrooms().set(floorplan.bathrooms());
                        i.info()._bedrooms().set(floorplan.bedrooms());
                        i.info().floor().set(aptUnitIO.floor());
                        items.add(i);

                        if (VistaFeatures.instance().occupancyModel()) {
                            for (AptUnitOccupancyIO occupancyIO : aptUnitIO.AptUnitOccupancySegment()) {
                                AptUnitOccupancySegment occupancySegment = EntityFactory.create(AptUnitOccupancySegment.class);
                                occupancySegment.dateFrom().setValue(occupancyIO.dateFrom().getValue());
                                occupancySegment.dateTo().setValue(occupancyIO.dateTo().getValue());
                                occupancySegment.status().setValue(occupancyIO.status().getValue());
                                occupancySegment.offMarket().setValue(occupancyIO.offMarket().getValue());
                                i.unitOccupancySegments().add(occupancySegment);
                                occupancySegment.unit().set(i);
                            }
                        }
                        try {
                            Persistence.service().merge(i);
                        } catch (UniqueConstraintUserRuntimeException e) {
                            throw new UniqueConstraintUserRuntimeException(i18n.tr("{0}, Unit# {1} in {2}", e.getMessage(), aptUnitIO.number(), building),
                                    e.getEntityPrototype());
                        }
                    }

                    counters.units += items.size();

                    if (VistaFeatures.instance().productCatalog()) {
                        for (AptUnit unit : items) {
                            ProductItem product = EntityFactory.create(ProductItem.class);
                            BigDecimal price = unit.financial()._marketRent().getValue();
                            product.name().setValue(arCode.name().getStringView());
                            product.price().setValue(price);
                            product.description().setValue(arCode.type().getStringView() + " description");
                            product.element().set(unit);

                            products.add(product);
                        }
                    }
                }
            }
        }

        if (VistaFeatures.instance().productCatalog()) {
            List<Service> services = new ArrayList<Service>();
            Service service = EntityFactory.create(Service.class);
            service.code().set(arCode);
            service.version().items().addAll(products);
            service.catalog().set(building.productCatalog());
            services.add(service);

            Persistence.service().persist(services);
        }

        return counters;
    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.Media;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.server.geo.GeoLocator;
import com.propertyvista.portal.server.geo.GeoLocator.Mode;
import com.propertyvista.portal.server.portal.PublicDataUpdater;
import com.propertyvista.portal.server.preloader.MeidaGenerator;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.common.generator.Model;

public class Importer {

    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    private Reader reader;

    private Mapper mapper;

    private Generator generator;

    private final Model model = new Model();

    public Importer() {
    }

    public void read() throws IOException, JAXBException, ParseException {
        reader = new Reader();
        reader.readCsv();
        reader.readXml();
    }

    public void map() {
        // map
        mapper = new Mapper(model);
        mapper.load(reader.getResidential(), reader.getUnits());
    }

    public void geo() throws JAXBException, IOException {
        GeoLocator geoCache = new GeoLocator(Mode.useCache);
        geoCache.populateGeo(model.getBuildings());
    }

    /**
     * Generate any missing data to conform
     */
    public void generate() {
        generator = new Generator(model);
        generator.generateMissingData();
    }

    public void save() {
        // save

        for (Building building : model.getBuildings()) {
            loadBuildingMedia(building);
            persist(building);
            PublicDataUpdater.updateIndexData(building);
        }

        for (BuildingAmenity amenity : model.getBuildingAmenities()) {
            persist(amenity);
        }

        for (FloorplanDTO floorplanDTO : model.getFloorplans()) {
            loadFloorplanMedia(floorplanDTO);

            // persist plain internal lists:
            for (Concession concession : floorplanDTO.concessions()) {
                persist(concession);
            }

            Floorplan floorplan = down(floorplanDTO, Floorplan.class);
            persist(floorplan); // persist real unit here, not DTO!..

            // persist internal lists and with belongness: 
            for (FloorplanAmenity amenity : floorplanDTO.amenities()) {
                amenity.belongsTo().set(floorplan);
                persist(amenity);
            }
        }

        for (AptUnitDTO unitDTO : model.getUnits()) {
            // persist plain internal lists:
            for (Utility utility : unitDTO.info().utilities()) {
                persist(utility);
            }
            for (AddOn addOn : unitDTO.addOns()) {
                persist(addOn);
            }

            AptUnit unit = down(unitDTO, AptUnit.class);
            persist(unit); // persist real unit here, not DTO!..

            // persist internal lists and with belongness: 
            for (AptUnitOccupancy occupancy : unitDTO.occupancies()) {
                occupancy.unit().set(unit);
                persist(occupancy);
            }
            for (AptUnitItem detail : unitDTO.details()) {
                detail.belongsTo().set(unit);
                persist(detail);
            }
        }
    }

    private void loadBuildingMedia(Building building) {
        if (building.info().propertyCode().isNull()) {
            return;
        }
        Map<Media, byte[]> data = PictureUtil.loadbuildingMedia(building.info().propertyCode().getValue());
        for (Map.Entry<Media, byte[]> me : data.entrySet()) {
            Media m = me.getKey();
            m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().filename().getValue(), m.file().contentType().getValue()));

            ThumbnailService.persist(m.file().blobKey().getValue(), me.getValue(), ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM,
                    ImageConsts.BUILDING_LARGE);

            persist(m);
            building.media().add(m);
        }
    }

    private void loadFloorplanMedia(Floorplan floorplan) {
        MeidaGenerator.attachGeneratedFloorplanMedia(floorplan);
    }

    public void start() throws Exception {
        read();
        map();
        geo();
        generate();
        save();
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }

    public Model getModel() {
        return model;
    }

    // Genric DTO -> O convertion:
    public static <S extends IEntity, D extends S> S down(D src, Class<S> dstClass) {
        S dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }
}

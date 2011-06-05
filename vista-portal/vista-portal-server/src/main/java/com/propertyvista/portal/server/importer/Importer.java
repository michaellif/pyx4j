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

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.server.geo.GeoLocator;
import com.propertyvista.portal.server.geo.GeoLocator.Mode;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

public class Importer {

    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    private Reader reader;

    private Mapper mapper;

    public Importer() {
    }

    public void read() throws IOException, JAXBException, ParseException {
        reader = new Reader();
        reader.readCsv();
        reader.readXml();
    }

    public void map() {
        // map
        mapper = new Mapper();
        mapper.load(reader.getResidential(), reader.getUnits());
    }

    public void geo() throws JAXBException, IOException {
        GeoLocator geoCache = new GeoLocator(Mode.useCache);
        geoCache.populateGeo(mapper.getBuildings());
    }

    public void save() {
        // save
        for (Building building : mapper.getBuildings()) {
            loadMemdia(building);
            persist(building);
        }

        for (Floorplan floorplan : mapper.getFloorplans()) {
            persist(floorplan);
        }

        for (AptUnitDTO unitDTO : mapper.getUnits()) {
            AptUnit unit = down(unitDTO, AptUnit.class);

            for (Utility utility : unitDTO.info().utilities()) {
                persist(utility);
            }
            for (AptUnitAmenity amenity : unitDTO.amenities()) {
                persist(amenity);
            }
            for (AddOn addOn : unitDTO.addOns()) {
                persist(addOn);
            }
            for (Concession concession : unitDTO.financial().concessions()) {
                persist(concession);
            }

            persist(unit); // persist real unit here, not DTO!..

            // persist internal lists and set correct belongness: 
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

    private void loadMemdia(Building building) {
        if (building.info().propertyCode().isNull()) {
            return;
        }
        Map<Medium, byte[]> data = PictureUtil.loadbuildingMedia(building.info().propertyCode().getValue());
        for (Map.Entry<Medium, byte[]> me : data.entrySet()) {
            Medium m = me.getKey();
            m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().filename().getValue(), m.file().contentType().getValue()));

            ThumbnailService.persist(m.file().blobKey().getValue(), me.getValue(), ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDUM,
                    ImageConsts.BUILDING_LARGE);

            persist(m);
            building.media().add(m);
        }

    }

    public void start() throws Exception {
        read();
        map();
        geo();
        save();
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }

    public Mapper getMapper() {
        return mapper;
    }

    // Genric DTO -> O convertion:
    public static <S extends IEntity, D extends S> S down(D src, Class<S> dstClass) {
        S dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }
}

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

import com.propertvista.generator.MediaGenerator;
import com.propertvista.generator.util.PictureUtil;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.common.generator.Model;
import com.propertyvista.server.common.generator.UnitRelatedData;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.GeoLocator;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;

public class Importer {

    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    private Reader reader;

    private Mapper mapper;

    private Generator generator;

    private final Model model = new Model();

    private boolean attachMedia = true;

    public Importer() {
    }

    public boolean isAttachMedia() {
        return attachMedia;
    }

    public void setAttachMedia(boolean attachMedia) {
        this.attachMedia = attachMedia;
    }

    public void read() throws IOException, JAXBException, ParseException {
        reader = new Reader();
        reader.readCsv();
        reader.readXml();
    }

    public void map(int maxResidentialBuildings) {
        // map
        mapper = new Mapper(model);
        mapper.load(reader.getResidential(), reader.getUnits(), maxResidentialBuildings);
    }

    public void geo() throws JAXBException, IOException {
        GeoLocator geoCache = new GeoLocator(Mode.useCacheOnly);
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
            Persistence.service().persist(building.media());
            if (isAttachMedia()) {
                loadBuildingMedia(building);
            }
            Persistence.service().persist(building);
            PublicDataUpdater.updateIndexData(building);
        }

        Persistence.service().persist(model.getBuildingAmenities());

        for (FloorplanDTO floorplanDTO : model.getFloorplans()) {

            if (isAttachMedia()) {
                MediaGenerator.attachGeneratedFloorplanMedia(floorplanDTO);
            }

            // persist plain internal lists:
//            for (Feature feature : floorplanDTO.features()) {
//                for (Concession concession : feature.concessions()) {
//                    persist(concession);
//                }
//                persist(feature);
//            }

            Floorplan floorplan = floorplanDTO.clone(Floorplan.class);
            Persistence.service().persist(floorplan); // persist real unit here, not DTO!..
            floorplanDTO.setPrimaryKey(floorplan.getPrimaryKey());

            // persist internal lists and with belongness: 
            for (FloorplanAmenity amenity : floorplanDTO.amenities()) {
                amenity.belongsTo().set(floorplan);
            }
            Persistence.service().persist(floorplanDTO.amenities());
        }

        for (UnitRelatedData unitData : model.getUnits()) {
            // persist plain internal lists:
            AptUnit unit = unitData.clone(AptUnit.class);
            Persistence.service().persist(unit); // persist real unit here, not DTO!..

            // persist internal lists and with belongness: 
            for (AptUnitOccupancy occupancy : unitData.occupancies()) {
                occupancy.unit().set(unit);
            }
            Persistence.service().persist(unitData.occupancies());

            for (AptUnitItem detail : unitData.details()) {
                detail.belongsTo().set(unit);
            }
            Persistence.service().persist(unitData.details());
        }
    }

    private void loadBuildingMedia(Building building) {
        if (building.propertyCode().isNull()) {
            return;
        }
        Map<Media, byte[]> data = PictureUtil.loadbuildingMedia(building.propertyCode().getValue());
        for (Map.Entry<Media, byte[]> me : data.entrySet()) {
            Media m = me.getKey();
            m.visibility().setValue(PublicVisibilityType.global);
            m.type().setValue(Media.Type.file);
            m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().filename().getValue(), m.file().contentMimeType().getValue()));
            m.file().timestamp().setValue(System.currentTimeMillis());

            ThumbnailService.persist(m.file().blobKey().getValue(), m.file().filename().getValue(), me.getValue(), ImageTarget.Building);

            Persistence.service().persist(m);
            building.media().add(m);
        }
    }

    public void start(int maxResidentialBuildings) throws Exception {
        read();
        map(maxResidentialBuildings);
        geo();
        generate();
        save();
    }

    public Model getModel() {
        return model;
    }
}
/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.FileBlob;

public class ImportProcessorFlatFloorplanAndUnits implements ImportProcessor {

    private static final I18n i18n = I18n.get(ImportProcessorFlatFloorplanAndUnits.class);

    private final static Logger log = LoggerFactory.getLogger(ImportProcessorFlatFloorplanAndUnits.class);

    @Override
    public boolean validate(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo, DownloadableUploadResponseDTO response) {
        status.setProgressMaximum(data.buildings().size() * 2);
        int count = 0;
        boolean result = true;
        buildings: for (BuildingIO buildingIO : data.buildings()) {
            count++;
            status.setProgress(count);
            if (status.isCanceled()) {
                break;
            }

            if (buildingIO.propertyCode().isNull()) {
                buildingIO._import().invalid().setValue(true);
                buildingIO._import().message().setValue(i18n.tr("Building Property Code is empty"));
                result = false;
                continue buildings;
            }

            if (!buildingIO.propertyCode().isNull() && buildingIO.propertyCode().getValue().length() > 10) {
                buildingIO._import().invalid().setValue(true);
                buildingIO._import().message().setValue("Property code must be 10 characters or less");
                result = false;
                continue buildings;
            }

            // Check if building is already created
            EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(buildingCriteria);
            if (buildings.size() == 0) {
                buildingIO._import().invalid().setValue(true);
                buildingIO._import().message().setValue(i18n.tr("Building ''{0}'' is not found in the database.", buildingIO.propertyCode().getValue()));
                result = false;
                continue buildings;
            } else {
                for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                    if (floorplanIO.name().isNull()) {
                        floorplanIO._import().invalid().setValue(true);
                        floorplanIO._import().message().setValue(i18n.tr("Floorplan Name is empty"));
                        result = false;
                    }
                    for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                        if (aptUnitIO.number().isNull()) {
                            aptUnitIO._import().invalid().setValue(true);
                            aptUnitIO._import().message().setValue(i18n.tr("Unit Number is empty"));
                            result = false;
                        }
                    }
                }
            }
        }
        status.setProgress(0);
        return result;
    }

    @Override
    public void persist(ImportIO data, DeferredProcessProgressResponse status, ImportUploadDTO uploadRequestInfo, DownloadableUploadResponseDTO response) {
        SharedGeoLocator.setMode(Mode.updateCache);
        status.setProgressMaximum(data.buildings().size() * 2);
        ImportCounters counters = new ImportCounters();
        int count = 0;
        try {
            for (BuildingIO building : data.buildings()) {
                if (building.type().isNull()) {
                    building.type().setValue(BuildingInfo.Type.residential);
                }
                log.debug("processing building {} {}", count + "/" + data.buildings().size(), building.getStringView());
                counters.add(saveInDB(building));
                count++;
                status.setProgress(data.buildings().size() + count);
                log.info("building {} updated", building.getStringView());
                if (status.isCanceled()) {
                    break;
                }
            }
            response.message().setValue(
                    SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", count, counters.floorplans, counters.units));
        } finally {
            SharedGeoLocator.save();
        }
    }

    private ImportCounters saveInDB(BuildingIO buildingIO) {

        ImportCounters counters = new ImportCounters();
        counters.buildings++;
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            building = buildings.get(0);
        }

        //Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                Floorplan floorplan = EntityFactory.create(Floorplan.class);
                {
                    EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                    criteria.add(PropertyCriterion.eq(criteria.proto().name(), floorplanIO.name().getValue()));
                    List<Floorplan> floorplans = Persistence.service().query(criteria);
                    if (floorplans.size() == 1) {
                        floorplan = (floorplans.get(0));
                    } else {
                        floorplan = new FloorplanConverter().createBO(floorplanIO);
                        floorplan.building().set(building);
                    }
                    Persistence.service().persist(floorplan);

                }

                counters.floorplans++;

                FloorplanMediaInfo mediaInfor = new FloorplanMediaInfo(floorplan);

                //Units
                {
                    for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                        aptUnitIO.number().setValue(AptUnitConverter.trimUnitNumber(aptUnitIO.number().getValue()));
                        {
                            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                            criteria.add(PropertyCriterion.eq(criteria.proto().building().propertyCode(), buildingIO.propertyCode()));
                            criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                            List<AptUnit> units = Persistence.service().query(criteria);
                            if (units.size() == 1) {
                                AptUnit unit = units.get(0);
                                Floorplan oldFloorplan = unit.floorplan();
                                floorplan = mergeMedia(oldFloorplan, floorplan, mediaInfor);
                                unit.floorplan().set(floorplan);
                                ServerSideFactory.create(BuildingFacade.class).persist(unit);
                                continue;
                            }
                        }

                        AptUnit i = new AptUnitConverter().createBO(aptUnitIO);
                        i.building().set(building);
                        i.floorplan().set(floorplan);
                        i.info()._bathrooms().set(floorplan.bathrooms());
                        i.info()._bedrooms().set(floorplan.bedrooms());
                        i.info().floor().set(aptUnitIO.floor());
                        ServerSideFactory.create(BuildingFacade.class).persist(i);
                        counters.units++;

                    }
                }
            }
        }

        return counters;
    }

    private static class FloorplanMediaInfo {

        private final Floorplan floorplan;

        private Map<Long, MediaFile> mediaCrc;

        public FloorplanMediaInfo(Floorplan floorplan) {
            this.floorplan = floorplan;
        }

        void loadMediaCrc() {
            if (mediaCrc != null) {
                return;
            }
            Persistence.ensureRetrieve(floorplan.media(), AttachLevel.Attached);
            mediaCrc = new HashMap<Long, MediaFile>();
            for (MediaFile media : floorplan.media()) {
                if (!media.blobKey().isNull()) {
                    FileBlob blob = Persistence.service().retrieve(FileBlob.class, media.blobKey().getValue());
                    mediaCrc.put(getCrc(blob), media);
                }
            }
        }

        private Long getCrc(FileBlob blob) {
            CRC32 crc = new CRC32();
            crc.update(blob.content().getValue(), 0, blob.content().getValue().length);
            return crc.getValue();
        }
    }

    private Floorplan mergeMedia(Floorplan oldFloorplan, Floorplan newFloorplan, FloorplanMediaInfo newMediaInfo) {
        Persistence.ensureRetrieve(oldFloorplan, AttachLevel.Attached);
        FloorplanMediaInfo oldMediaInfo = new FloorplanMediaInfo(oldFloorplan);
        newMediaInfo.loadMediaCrc();
        oldMediaInfo.loadMediaCrc();

        for (Map.Entry<Long, MediaFile> me : oldMediaInfo.mediaCrc.entrySet()) {
            if (!newMediaInfo.mediaCrc.containsKey(me.getKey())) {
                newMediaInfo.mediaCrc.put(me.getKey(), copyMedia(me.getValue(), newFloorplan));
            }
        }

        return newFloorplan;
    }

    private MediaFile copyMedia(MediaFile origValue, Floorplan newFloorplan) {
        MediaFile newValue = EntityGraph.businessDuplicate(origValue);

        FileBlob origBlob = Persistence.service().retrieve(FileBlob.class, origValue.blobKey().getValue());
        newValue.blobKey().setValue(BlobService.persist(origBlob.content().getValue(), origBlob.name().getValue(), origBlob.contentType().getValue()));

        ThumbnailService.persist(newValue.blobKey().getValue(), origBlob.name().getValue(), origBlob.content().getValue(), ImageTarget.Floorplan);

        newFloorplan.media().add(newValue);
        Persistence.service().persist(newFloorplan);
        return newValue;
    }

}
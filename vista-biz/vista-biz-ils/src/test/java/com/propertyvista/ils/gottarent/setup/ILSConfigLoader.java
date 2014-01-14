/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 29, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.setup;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsDBConfigurationPostgreSQL;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan.Priority;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.ILSConfig;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.domain.FileImageThumbnailBlobDTO;

public class ILSConfigLoader {
    static {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.PostgreSQL) {
            @Override
            public IPersistenceConfiguration getPersistenceConfiguration() {
                return new VistaTestsDBConfigurationPostgreSQL() {
                    @Override
                    public String dbName() {
                        return "vista_prod";
                    }
                };
            }
        });
        NamespaceManager.setNamespace("star");
    }

    private final static Logger log = LoggerFactory.getLogger(ILSConfigLoader.class);

    /*
     * - Ensure gottarent is selected in ILSConfig
     * - Add gottarent vendor for all buildings and floorplans
     * - Update unit availability and market price from csv file
     */
    public static void main(String[] args) {
        // retrieve unit availability and market price
        List<UnitAvailData> unitAvailList = EntityCSVReciver.create(UnitAvailData.class).loadResourceFile(
                IOUtils.resourceFileName("greenwin/units.csv", ILSConfigLoader.class));
        Map<String, UnitAvailData> availDataMap = new HashMap<String, UnitAvailData>();
        for (UnitAvailData data : unitAvailList) {
            availDataMap.put(data.propertyCode().getValue() + "_" + data.unitNo().getValue(), data);
        }

        // load floorplan media
        int mediaSize = 5;
        MediaFile[] media = new MediaFile[mediaSize * 2];
        for (int i = 0; i < mediaSize; i++) {
            for (int k = 0; k < 2; k++) {
                String fileName = "apartment" + (i + 1) + "-" + (k + 1) + ".jpg";
                MediaFile m = loadMediaFile(fileName);
                if (m == null) {
                    log.error("Failed to load media file: {}", fileName);
                    return;
                }
                media[2 * i + k] = m;
            }
        }

        Persistence.service().startTransaction();

        // check ILSConfig
        ILSConfig ilsConfig = null;
        {
            ilsConfig = Persistence.service().retrieve(EntityQueryCriteria.create(ILSConfig.class));
            if (ilsConfig == null) {
                ilsConfig = EntityFactory.create(ILSConfig.class);
                Persistence.service().persist(ilsConfig);
            }
        }

        {
            EntityQueryCriteria<ILSVendorConfig> crit = EntityQueryCriteria.create(ILSVendorConfig.class);
            crit.eq(crit.proto().vendor(), ILSVendor.gottarent);
            if (Persistence.service().count(crit) < 1) {
                ILSVendorConfig grConfig = EntityFactory.create(ILSVendorConfig.class);
                grConfig.vendor().setValue(ILSVendor.gottarent);
                grConfig.maxDailyAds().setValue(100);
                ilsConfig.vendors().add(grConfig);
                Persistence.service().persist(ilsConfig);
            }
        }

        // update ils data
        List<Building> buildings = Persistence.service().query(EntityQueryCriteria.create(Building.class));
        for (Building b : buildings) {
            System.out.println(b.propertyCode().getValue());
            // check ils profile
            EntityQueryCriteria<ILSProfileBuilding> crit = EntityQueryCriteria.create(ILSProfileBuilding.class);
            crit.eq(crit.proto().vendor(), ILSVendor.gottarent);
            crit.eq(crit.proto().building(), b);
            if (Persistence.service().count(crit) < 1) {
                ILSProfileBuilding ilsProfile = EntityFactory.create(ILSProfileBuilding.class);
                ilsProfile.vendor().setValue(ILSVendor.gottarent);
                ilsProfile.building().set(b);
                // required for GR feed
                ilsProfile.preferredContacts().email().value().setValue("gottarent-test-" + b.propertyCode().getValue() + "@propertyvista.com");
                Persistence.service().persist(ilsProfile);
            }

            Persistence.service().retrieveMember(b.floorplans(), AttachLevel.Attached);
            for (Floorplan f : b.floorplans()) {
                // check ils profile
                EntityQueryCriteria<ILSProfileFloorplan> critFp = EntityQueryCriteria.create(ILSProfileFloorplan.class);
                critFp.eq(critFp.proto().vendor(), ILSVendor.gottarent);
                critFp.eq(critFp.proto().floorplan(), f);
                if (Persistence.service().count(critFp) < 1) {
                    ILSProfileFloorplan ilsProfile = EntityFactory.create(ILSProfileFloorplan.class);
                    ilsProfile.vendor().setValue(ILSVendor.gottarent);
                    ilsProfile.floorplan().set(f);
                    ilsProfile.priority().setValue(DataGenerator.randomEnum(Priority.class));
                    Persistence.service().persist(ilsProfile);
                }
                // add media
                if (f.media().isEmpty()) {
                    int id = DataGenerator.randomInt(mediaSize);
                    f.media().add((MediaFile) media[2 * id].duplicate());
                    f.media().add((MediaFile) media[2 * id + 1].duplicate());
                    Persistence.service().persist(f);
                }
            }

            Persistence.service().retrieveMember(b.units(), AttachLevel.Attached);
            for (AptUnit u : b.units()) {
                UnitAvailData data = availDataMap.get(b.propertyCode().getValue() + "_" + u.info().number().getValue());
                if (data != null && u.financial()._marketRent().isNull()) {
                    u._availableForRent().setValue(toLogicalDate(data.available().getValue()));
                    u.financial()._marketRent().setValue(new BigDecimal(data.marketRent().getValue()));
                    Persistence.service().persist(u);
                }
            }
        }
        Persistence.service().commit();
    }

    static LogicalDate toLogicalDate(String yyyyMMdd) {
        LogicalDate date = null;
        try {
            date = new LogicalDate(new SimpleDateFormat("yyyy-MM-dd").parse(yyyyMMdd));
        } catch (Exception ignore) {
            // ignore
        }
        return date;
    }

    static MediaFile loadMediaFile(String filename) {
        try {
            byte raw[] = IOUtils.getBinaryResource(IOUtils.resourceFileName("greenwin/" + filename, ILSConfigLoader.class));
            if (raw == null) {
                return null;
            }
            MediaFile m = EntityFactory.create(MediaFile.class);
            m.visibility().setValue(PublicVisibilityType.global);
            m.caption().setValue(FilenameUtils.getBaseName(filename));
            m.file().fileName().setValue(filename);
            m.file().contentMimeType().setValue(MimeMap.getContentType(FilenameUtils.getExtension(filename)));
            m.file().fileSize().setValue(raw.length);
            m.file().blobKey().setValue(BlobService.persist(raw, m.file().fileName().getValue(), m.file().contentMimeType().getValue()));
            FileImageThumbnailBlobDTO thumbnailBlob = ThumbnailService.createThumbnailBlob(m.file().fileName().getStringView(), raw, ImageTarget.Floorplan);
            thumbnailBlob.setPrimaryKey(m.file().blobKey().getValue());
            ThumbnailService.persist(thumbnailBlob);

            return m;
        } catch (IOException e) {
            log.error("Failed to read the file: {}", filename, e);
            return null;
        }
    }

}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.Map;

import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.PictureUtil;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.File;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

public class MediaGenerator {

    // Minimize PreloadData Size and speed, Share common images statically.  (25 sec instead of 2 min on fast computer) 
    static final boolean blob_mimize_Preload_Data_Size = true;

    static String[] youtubeIds;

    public static String randomYoutubeId() {
        if (youtubeIds == null) {
            youtubeIds = CSVLoad.loadFile(IOUtils.resourceFileName("youtube.csv", MediaGenerator.class), "id");
        }
        return youtubeIds[DataGenerator.nextInt(youtubeIds.length, "youtubeIds", 4)];
    }

    /**
     * This is a light-weight method that simply creates a file media with a fictitious filename
     * 
     * @return
     */
    public static Media createMedia() {
        Media media = EntityFactory.create(Media.class);
        media.type().setValue(Media.Type.file);

        File file = EntityFactory.create(File.class);

        file.filename().setValue("file102.jpg");
        media.caption().setValue(CommonsGenerator.lipsumShort());
        file.contentMimeType().setValue(MimeMap.getContentType(DownloadFormat.JPEG));
        media.file().set(file);
        media.visibility().setValue(PublicVisibilityType.global);

        return media;
    }

    public static void generatedBuildingMedia(Building building) {
        {
            Media media = EntityFactory.create(Media.class);
            media.type().setValue(Media.Type.youTube);
            media.caption().setValue("A " + building.info().name().getValue() + " video");
            media.youTubeVideoID().setValue(MediaGenerator.randomYoutubeId());
            media.visibility().setValue(PublicVisibilityType.global);
            building.media().add(media);
        }

        int imageIndex = RandomUtil.randomInt(6) + 1;
        String filename = "building" + imageIndex;

        boolean newData = false;
        @SuppressWarnings("unchecked")
        Map<Media, byte[]> data = (Map<Media, byte[]>) CacheService.get(MediaGenerator.class.getName() + filename);
        if (data == null) {
            data = PictureUtil.loadResourceMedia(filename, MediaGenerator.class);
            newData = true;
        }
        for (Map.Entry<Media, byte[]> me : data.entrySet()) {
            Media m = me.getKey();
            if (m.file().blobKey().isNull()) {
                m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().filename().getValue(), m.file().contentMimeType().getValue()));
                ThumbnailService.persist(m.file().blobKey().getValue(), filename, me.getValue(), ImageTarget.Building);
                newData = true;
            }
            if (blob_mimize_Preload_Data_Size) {
                m = (Media) m.cloneEntity();
                m.setPrimaryKey(null);
            }
            building.media().add(m);
        }

        if (newData && blob_mimize_Preload_Data_Size) {
            CacheService.put(MediaGenerator.class.getName() + filename, data);
        }
    }

    public static void attachGeneratedFloorplanMedia(Floorplan floorplan) {
        int imageIndex = RandomUtil.randomInt(5) + 1;
        String filename = "apartment" + imageIndex;
        @SuppressWarnings("unchecked")
        Map<Media, byte[]> data = (Map<Media, byte[]>) CacheService.get(MediaGenerator.class.getName() + filename);
        if (data == null) {
            data = PictureUtil.loadResourceMedia(filename, MediaGenerator.class);
            if (blob_mimize_Preload_Data_Size) {
                CacheService.put(filename, data);
            }
        }
        for (Map.Entry<Media, byte[]> me : data.entrySet()) {
            Media m = me.getKey();
            if (m.file().blobKey().isNull()) {
                m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().filename().getValue(), m.file().contentMimeType().getValue()));
                ThumbnailService.persist(m.file().blobKey().getValue(), filename, me.getValue(), ImageTarget.Floorplan);
            }
            if (blob_mimize_Preload_Data_Size) {
                m = (Media) m.cloneEntity();
                m.setPrimaryKey(null);
            }

            Persistence.service().persist(m);
            floorplan.media().add(m);
        }
    }

}

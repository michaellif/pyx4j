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
package com.propertyvista.generator;

import java.util.HashMap;
import java.util.Map;


import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.File;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.PictureUtil;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.domain.FileImageThumbnailBlobDTO;

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

        file.fileName().setValue("file102.jpg");
        media.caption().setValue(CommonsGenerator.lipsumShort());
        file.contentMimeType().setValue(MimeMap.getContentType(DownloadFormat.JPEG));
        media.file().set(file);
        media.visibility().setValue(PublicVisibilityType.global);

        return media;
    }

    //This is shared between created PMCs
    private static Map<String, FileImageThumbnailBlobDTO> resized = new HashMap<String, FileImageThumbnailBlobDTO>();

    public static void generatedBuildingMedia(Building building) {
        {
            Media media = EntityFactory.create(Media.class);
            media.type().setValue(Media.Type.youTube);
            media.caption().setValue("A " + building.info().name().getValue() + " video");
            media.youTubeVideoID().setValue(MediaGenerator.randomYoutubeId());
            media.visibility().setValue(PublicVisibilityType.global);
            building.media().add(media);
        }

        int imageIndex = RandomUtil.nextInt(7, "bld-img", 4) + 1;
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
                m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().fileName().getValue(), m.file().contentMimeType().getValue()));

                FileImageThumbnailBlobDTO thumbnailBlob = resized.get(m.file().fileName().getValue());
                if (thumbnailBlob == null) {
                    thumbnailBlob = ThumbnailService.createThumbnailBlob(m.file().fileName().getStringView(), me.getValue(), ImageTarget.Building);
                    resized.put(m.file().fileName().getValue(), thumbnailBlob);
                }
                thumbnailBlob = (FileImageThumbnailBlobDTO) thumbnailBlob.duplicate();
                thumbnailBlob.setPrimaryKey(m.file().blobKey().getValue());
                ThumbnailService.persist(thumbnailBlob);
                newData = true;
            }
            if (blob_mimize_Preload_Data_Size) {
                m = (Media) m.duplicate();
                m.setPrimaryKey(null);
            }
            building.media().add(m);
        }

        if (newData && blob_mimize_Preload_Data_Size) {
            CacheService.put(MediaGenerator.class.getName() + filename, data);
        }
    }

    public static void attachGeneratedFloorplanMedia(Floorplan floorplan) {
        int imageIndex = RandomUtil.nextInt(5, "fp-img", 3) + 1;
        String filename = "apartment" + imageIndex;
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
                m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().fileName().getValue(), m.file().contentMimeType().getValue()));

                FileImageThumbnailBlobDTO thumbnailBlob = resized.get(m.file().fileName().getValue());
                if (thumbnailBlob == null) {
                    thumbnailBlob = ThumbnailService.createThumbnailBlob(m.file().fileName().getStringView(), me.getValue(), ImageTarget.Floorplan);
                    resized.put(m.file().fileName().getValue(), thumbnailBlob);
                }
                thumbnailBlob = (FileImageThumbnailBlobDTO) thumbnailBlob.duplicate();
                thumbnailBlob.setPrimaryKey(m.file().blobKey().getValue());
                ThumbnailService.persist(thumbnailBlob);

                newData = true;
            }
            if (blob_mimize_Preload_Data_Size) {
                m = (Media) m.duplicate();
                m.setPrimaryKey(null);
            }

            floorplan.media().add(m);
        }
        if (newData && blob_mimize_Preload_Data_Size) {
            CacheService.put(MediaGenerator.class.getName() + filename, data);
        }
    }

}

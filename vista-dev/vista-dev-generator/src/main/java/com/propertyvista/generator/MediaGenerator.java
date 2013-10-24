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
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
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

    //This is shared between created PMCs
    private static Map<String, FileImageThumbnailBlobDTO> resized = new HashMap<String, FileImageThumbnailBlobDTO>();

    public static void generatedBuildingMedia(Building building) {
        int imageIndex = RandomUtil.nextInt(7, "bld-img", 4) + 1;
        String filename = "building" + imageIndex;

        boolean newData = false;
        @SuppressWarnings("unchecked")
        Map<MediaFile, byte[]> data = (Map<MediaFile, byte[]>) CacheService.get(MediaGenerator.class.getName() + filename);
        if (data == null) {
            data = PictureUtil.loadResourceMedia(filename, MediaGenerator.class);
            newData = true;
        }
        for (Map.Entry<MediaFile, byte[]> me : data.entrySet()) {
            MediaFile m = me.getKey();
            if (m.blobKey().isNull()) {
                m.blobKey().setValue(BlobService.persist(me.getValue(), m.fileName().getValue(), m.contentMimeType().getValue()));

                FileImageThumbnailBlobDTO thumbnailBlob = resized.get(m.fileName().getValue());
                if (thumbnailBlob == null) {
                    thumbnailBlob = ThumbnailService.createThumbnailBlob(m.fileName().getStringView(), me.getValue(), ImageTarget.Building);
                    resized.put(m.fileName().getValue(), thumbnailBlob);
                }
                thumbnailBlob = (FileImageThumbnailBlobDTO) thumbnailBlob.duplicate();
                thumbnailBlob.setPrimaryKey(m.blobKey().getValue());
                ThumbnailService.persist(thumbnailBlob);
                newData = true;
            }
            if (blob_mimize_Preload_Data_Size) {
                m = (MediaFile) m.duplicate();
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
        Map<MediaFile, byte[]> data = (Map<MediaFile, byte[]>) CacheService.get(MediaGenerator.class.getName() + filename);
        if (data == null) {
            data = PictureUtil.loadResourceMedia(filename, MediaGenerator.class);
            newData = true;
        }
        for (Map.Entry<MediaFile, byte[]> me : data.entrySet()) {
            MediaFile m = me.getKey();
            if (m.blobKey().isNull()) {
                m.blobKey().setValue(BlobService.persist(me.getValue(), m.fileName().getValue(), m.contentMimeType().getValue()));

                FileImageThumbnailBlobDTO thumbnailBlob = resized.get(m.fileName().getValue());
                if (thumbnailBlob == null) {
                    thumbnailBlob = ThumbnailService.createThumbnailBlob(m.fileName().getStringView(), me.getValue(), ImageTarget.Floorplan);
                    resized.put(m.fileName().getValue(), thumbnailBlob);
                }
                thumbnailBlob = (FileImageThumbnailBlobDTO) thumbnailBlob.duplicate();
                thumbnailBlob.setPrimaryKey(m.blobKey().getValue());
                ThumbnailService.persist(thumbnailBlob);

                newData = true;
            }
            if (blob_mimize_Preload_Data_Size) {
                m = (MediaFile) m.duplicate();
                m.setPrimaryKey(null);
            }

            floorplan.media().add(m);
        }
        if (newData && blob_mimize_Preload_Data_Size) {
            CacheService.put(MediaGenerator.class.getName() + filename, data);
        }
    }

}

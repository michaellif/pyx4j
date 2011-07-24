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
package com.propertyvista.portal.server.generator;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.server.PersistenceServicesFactory;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.server.importer.PictureUtil;
import com.propertyvista.portal.server.preloader.BuildingPreloader;
import com.propertyvista.portal.server.preloader.RandomUtil;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

public class MediaGenerator {

    public static final String ATTACH_MEDIA_PARAMETER = "vista.media.preload";

    // Minimize PreloadData Size and speed, Share common images statically.  (25 sec instead of 2 min on fast computer) 
    static final boolean blob_mimize_Preload_Data_Size = true;

    private static Map<String, Map<Media, byte[]>> blob_Shared_GenerateMedia = new HashMap<String, Map<Media, byte[]>>();

    public static void attachGeneratedFloorplanMedia(Floorplan floorplan) {
        int imageIndex = RandomUtil.randomInt(5) + 1;
        String filename = "apartment" + imageIndex;
        Map<Media, byte[]> data = blob_Shared_GenerateMedia.get(filename);
        if (data == null) {
            data = loadFloorplanMedia(filename);
            if (blob_mimize_Preload_Data_Size) {
                blob_Shared_GenerateMedia.put(filename, data);
            }
        }
        for (Map.Entry<Media, byte[]> me : data.entrySet()) {
            Media m = me.getKey();
            if (blob_mimize_Preload_Data_Size) {
                m = (Media) m.cloneEntity();
                m.setPrimaryKey(null);
            }
            PersistenceServicesFactory.getPersistenceService().persist(m);
            floorplan.media().add(m);
        }
    }

    private static Map<Media, byte[]> loadFloorplanMedia(String filename) {
        Map<Media, byte[]> data = PictureUtil.loadResourceMedia(filename, BuildingPreloader.class);
        for (Map.Entry<Media, byte[]> me : data.entrySet()) {
            Media m = me.getKey();
            m.file().blobKey().setValue(BlobService.persist(me.getValue(), m.file().filename().getValue(), m.file().contentType().getValue()));

            //TODO what sizes to use for Floorplan images?
            ThumbnailService.persist(m.file().blobKey().getValue(), me.getValue(), ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM,
                    ImageConsts.BUILDING_LARGE);
        }
        return data;
    }
}

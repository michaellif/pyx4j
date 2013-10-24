/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.oapi.model.MediaImageIO;
import com.propertyvista.oapi.xml.StringIO;

public class MediaMarshaller implements Marshaller<MediaFile, MediaImageIO> {

    private static class SingletonHolder {
        public static final MediaMarshaller INSTANCE = new MediaMarshaller();
    }

    private MediaMarshaller() {
    }

    public static MediaMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public MediaImageIO marshal(MediaFile media) {
        if (media == null || media.isNull()) {
            return null;
        }
        MediaImageIO mediaIO = new MediaImageIO();
        mediaIO.caption = MarshallerUtils.createIo(StringIO.class, media.caption());
        return mediaIO;
    }

    public List<MediaImageIO> marshal(List<MediaFile> mediaList) {
        List<MediaImageIO> mediaIOList = new ArrayList<MediaImageIO>();
        for (MediaFile media : mediaList) {
            mediaIOList.add(marshal(media));
        }
        return mediaIOList;
    }

    @Override
    public MediaFile unmarshal(MediaImageIO mediaIO) {
        MediaFile media = EntityFactory.create(MediaFile.class);
        MarshallerUtils.setValue(media.caption(), mediaIO.caption);
        return media;
    }

    public List<MediaFile> unmarshal(List<MediaImageIO> MediaIOList) {
        List<MediaFile> medias = new ArrayList<MediaFile>();
        for (MediaImageIO mediaIO : MediaIOList) {
            MediaFile media = EntityFactory.create(MediaFile.class);
            MarshallerUtils.set(media, mediaIO, MediaMarshaller.getInstance());
            medias.add(media);
        }
        return medias;
    }
}

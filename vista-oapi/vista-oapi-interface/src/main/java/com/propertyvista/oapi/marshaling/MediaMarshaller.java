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

import com.propertyvista.domain.media.Media;
import com.propertyvista.oapi.model.MediaIO;
import com.propertyvista.oapi.model.types.MediaTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class MediaMarshaller implements Marshaller<Media, MediaIO> {

    private static class SingletonHolder {
        public static final MediaMarshaller INSTANCE = new MediaMarshaller();
    }

    private MediaMarshaller() {
    }

    public static MediaMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public MediaIO marshal(Media media) {
        if (media == null || media.isNull()) {
            return null;
        }
        MediaIO mediaIO = new MediaIO();
        mediaIO.caption = MarshallerUtils.createIo(StringIO.class, media.caption());
        mediaIO.mediaType = MarshallerUtils.createIo(MediaTypeIO.class, media.type());
        mediaIO.url = MarshallerUtils.createIo(StringIO.class, media.url());
        mediaIO.youTubeVideoID = MarshallerUtils.createIo(StringIO.class, media.youTubeVideoID());
        return mediaIO;
    }

    public List<MediaIO> marshal(List<Media> mediaList) {
        List<MediaIO> mediaIOList = new ArrayList<MediaIO>();
        for (Media media : mediaList) {
            mediaIOList.add(marshal(media));
        }
        return mediaIOList;
    }

    @Override
    public Media unmarshal(MediaIO mediaIO) {
        Media media = EntityFactory.create(Media.class);
        MarshallerUtils.setValue(media.caption(), mediaIO.caption);
        MarshallerUtils.setValue(media.type(), mediaIO.mediaType);
        MarshallerUtils.setValue(media.url(), mediaIO.url);
        MarshallerUtils.setValue(media.youTubeVideoID(), mediaIO.youTubeVideoID);
        return media;
    }

    public List<Media> unmarshal(List<MediaIO> MediaIOList) {
        List<Media> medias = new ArrayList<Media>();
        for (MediaIO mediaIO : MediaIOList) {
            Media media = EntityFactory.create(Media.class);
            MarshallerUtils.set(media, mediaIO, MediaMarshaller.getInstance());
            medias.add(media);
        }
        return medias;
    }
}

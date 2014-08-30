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
package com.propertyvista.oapi.v1.marshaling;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.MediaImageIO;
import com.propertyvista.oapi.xml.StringIO;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;

public class MediaMarshaller extends AbstractMarshaller<MediaFile, MediaImageIO> {

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
        mediaIO.caption = createIo(StringIO.class, media.caption());
        mediaIO.accessUrl = getMediaImgUrl(media);
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
        setValue(media.caption(), mediaIO.caption);
        return media;
    }

    public List<MediaFile> unmarshal(List<MediaImageIO> MediaIOList) {
        List<MediaFile> medias = new ArrayList<MediaFile>();
        for (MediaImageIO mediaIO : MediaIOList) {
            MediaFile media = EntityFactory.create(MediaFile.class);
            set(media, mediaIO, MediaMarshaller.getInstance());
            medias.add(media);
        }
        return medias;
    }

    private String getMediaImgUrl(MediaFile media) {
        return getSiteHomeUrl() + DeploymentConsts.mediaImagesServletMapping + media.id().getStringView() + "/" + ThumbnailSize.large.name() + "."
                + ImageConsts.THUMBNAIL_TYPE;
    }

    private String getSiteHomeUrl() {
        String homeUrl = VistaDeployment.getBaseApplicationURL(VistaApplication.site, false);
        return homeUrl + (homeUrl.endsWith("/") ? "" : "/");
    }

}

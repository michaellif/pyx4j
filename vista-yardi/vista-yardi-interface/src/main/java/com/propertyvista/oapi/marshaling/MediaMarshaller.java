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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.media.Media;
import com.propertyvista.oapi.model.MediaIO;
import com.propertyvista.oapi.model.MediaTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class MediaMarshaller implements Marshaller<Media, MediaIO> {

    @Override
    public MediaIO unmarshal(Media media) {
        MediaIO mediaIO = new MediaIO();
        mediaIO.caption = new StringIO(media.caption().getValue());
        mediaIO.mediaType = new MediaTypeIO(media.type().getValue());
        mediaIO.url = new StringIO(media.url().getValue());
        mediaIO.youTubeVideoID = new StringIO(media.youTubeVideoID().getValue());
        return mediaIO;
    }

    @Override
    public Media marshal(MediaIO mediaIO) throws Exception {
        Media media = EntityFactory.create(Media.class);
        media.caption().setValue(mediaIO.caption.value);
        media.type().setValue(mediaIO.mediaType.value);
        media.url().setValue(mediaIO.url.value);
        media.youTubeVideoID().setValue(mediaIO.youTubeVideoID.value);
        return media;
    }
}

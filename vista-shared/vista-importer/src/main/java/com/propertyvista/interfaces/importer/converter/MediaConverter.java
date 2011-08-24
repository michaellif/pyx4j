/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.media.Media;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

public class MediaConverter extends EntityDtoBinder<Media, MediaIO> {

    private final String baseFolder;

    public MediaConverter(String baseFolder) {
        super(Media.class, MediaIO.class, false);
        this.baseFolder = baseFolder;
    }

    @Override
    protected void bind() {
        bind(dtoProto.caption(), dboProto.file().caption());
    }

    @Override
    public void copyDBOtoDTO(Media dbo, MediaIO dto) {
        super.copyDBOtoDTO(dbo, dto);
        switch (dbo.type().getValue()) {
        case file:
            dto.uri().setValue(dbo.file().blobKey().getStringView() + "-" + dbo.file().filename().getStringView());
            dto.mediaType().setValue(MediaIO.MediaType.file);
            try {
                BlobService.save(dbo.file().blobKey().getValue(), new File(baseFolder + dto.uri().getValue()));
            } catch (IOException e) {
                throw new Error(e);
            }
            break;
        case externalUrl:
            dto.mediaType().setValue(MediaIO.MediaType.externalUrl);
            dto.uri().setValue(dbo.url().getValue());
            break;
        case youTube:
            dto.mediaType().setValue(MediaIO.MediaType.youTube);
            dto.uri().setValue(dbo.youTubeVideoID().getValue());
            break;
        }
    }

    @Override
    public void copyDTOtoDBO(MediaIO dto, Media dbo) {
        super.copyDTOtoDBO(dto, dbo);
        switch (dto.mediaType().getValue()) {
        case file:
            dbo.type().setValue(Media.Type.file);
            File file = new File(new File(baseFolder), dto.uri().getValue());
            dbo.file().filename().setValue(file.getName());
            dbo.file().contentMimeType().setValue(MimeMap.getContentType(FilenameUtils.getExtension(file.getName())));
            byte raw[] = getBinary(file);
            dbo.file().fileSize().setValue(raw.length);

            dbo.file().blobKey().setValue(BlobService.persist(raw, dbo.file().filename().getValue(), dbo.file().contentMimeType().getValue()));
            ThumbnailService.persist(dbo.file().blobKey().getValue(), raw, ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM, ImageConsts.BUILDING_LARGE);

            break;
        case externalUrl:
            dbo.type().setValue(Media.Type.externalUrl);
            dbo.url().setValue(dto.uri().getValue());
            break;
        case youTube:
            if (!dto.uri().getValue().matches("[a-zA-Z0-9_-]{11}")) {
                throw new UserRuntimeException("Invalid YouTube VideoID" + dto.uri().getValue());
            }
            dbo.type().setValue(Media.Type.youTube);
            dbo.youTubeVideoID().setValue(dto.uri().getValue());
            break;
        }
    }

    private byte[] getBinary(File file) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            IOUtils.copyStream(in, b, 1024);
            return b.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(b);
        }
    }

}

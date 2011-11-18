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
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.essentials.j2se.util.FileIOUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.services.MediaUploadService;
import com.propertyvista.domain.media.Media;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.domain.ThumbnailBlob;

public class MediaConverter extends EntityDtoBinder<Media, MediaIO> {

    private final static Logger log = LoggerFactory.getLogger(MediaConverter.class);

    private static I18n i18n = I18n.get(MediaConverter.class);

    private final MediaConfig mediaConfig;

    private final ImageTarget imageTarget;

    private static Collection<String> extensions = DownloadFormat.getExtensions(MediaUploadService.supportedFormats);

    public MediaConverter(MediaConfig mediaConfig, ImageTarget imageTarget) {
        super(Media.class, MediaIO.class, false);
        this.mediaConfig = mediaConfig;
        this.imageTarget = imageTarget;
    }

    @Override
    protected void bind() {
        bind(dtoProto.caption(), dboProto.caption());
        bind(dtoProto.visibility(), dboProto.visibility());
    }

    @Override
    public void copyDBOtoDTO(Media dbo, MediaIO dto) {
        super.copyDBOtoDTO(dbo, dto);
        switch (dbo.type().getValue()) {
        case file:
            dto.mediaType().setValue(MediaIO.MediaType.file);
            if (mediaConfig.baseFolder != null) {
                dto.uri().setValue(dbo.file().blobKey().getStringView() + "-" + dbo.file().filename().getStringView());
                try {
                    BlobService.save(dbo.file().blobKey().getValue(), new File(mediaConfig.baseFolder + dto.uri().getValue()));
                } catch (IOException e) {
                    throw new Error(e);
                }
            } else {
                dto.uri().setValue(dbo.id().getStringView() + "/" + dbo.file().filename().getStringView());
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

    public String verify(MediaIO dto) {
        if (dto.mediaType().isNull()) {
            return i18n.tr("Media Type Is Empty");
        }
        switch (dto.mediaType().getValue()) {
        case file:
            File file = new File(new File(mediaConfig.baseFolder), dto.uri().getValue());
            if (!file.exists()) {
                file = FileIOUtils.findFileIgnoreCase(file);
                if (!file.exists()) {
                    return i18n.tr("Media file not found ''{0}''", dto.uri().getValue());
                }
            }
            String extension = FilenameUtils.getExtension(file.getName());
            if (extension != null) {
                extension = extension.toLowerCase(Locale.ENGLISH);
            }
            if (!extensions.contains(extension)) {
                return i18n.tr("Unsupported Media file ''{0}'' extension ''{1}''", dto.uri().getValue(), extension);
            }
        }
        return null;
    }

    //This is shared between created PMCs
    private static Map<String, ThumbnailBlob> resized = new HashMap<String, ThumbnailBlob>();

    @Override
    public void copyDTOtoDBO(MediaIO dto, Media dbo) {
        super.copyDTOtoDBO(dto, dbo);
        if (dto.mediaType().isNull()) {
            if (mediaConfig.ignoreMissingMedia) {
                return;
            } else {
                throw new UserRuntimeException(i18n.tr("Media Type Is Empty"));
            }
        }
        switch (dto.mediaType().getValue()) {
        case file:
            dbo.type().setValue(Media.Type.file);
            File file = new File(new File(mediaConfig.baseFolder), dto.uri().getValue());
            if (!file.exists()) {
                file = FileIOUtils.findFileIgnoreCase(file);
                if (!file.exists()) {
                    if (mediaConfig.ignoreMissingMedia) {
                        return;
                    } else {
                        throw new UserRuntimeException(i18n.tr("Media file not found ''{0}''", dto.uri().getValue()));
                    }
                }
            }
            String extension = FilenameUtils.getExtension(file.getName());
            if (extension != null) {
                extension = extension.toLowerCase(Locale.ENGLISH);
            }
            if (!extensions.contains(extension)) {
                if (mediaConfig.ignoreMissingMedia) {
                    return;
                } else {
                    throw new UserRuntimeException(i18n.tr("Unsupported Media file ''{0}'' extension ''{1}''", dto.uri().getValue(), extension));
                }
            }
            dbo.file().filename().setValue(file.getName());
            dbo.file().fileSize().setValue(Long.valueOf(file.length()).intValue());
            dbo.file().contentMimeType().setValue(MimeMap.getContentType(extension));
            dbo.file().timestamp().setValue(System.currentTimeMillis());

            if (!mediaConfig.mimizePreloadDataSize) {
                byte raw[] = getBinary(file);
                dbo.file().blobKey().setValue(BlobService.persist(raw, dbo.file().filename().getValue(), dbo.file().contentMimeType().getValue()));
                ThumbnailService.persist(dbo.file().blobKey().getValue(), file.getName(), raw, imageTarget);
            } else {
                String uniqueName = MediaConverter.class.getName() + imageTarget + file.getAbsolutePath().toLowerCase(Locale.ENGLISH);
                Key blobKey = CacheService.get(uniqueName);
                if (blobKey == null) {
                    byte raw[] = getBinary(file);
                    blobKey = BlobService.persist(raw, dbo.file().filename().getValue(), dbo.file().contentMimeType().getValue());
                    CacheService.put(uniqueName, blobKey);
                    ThumbnailBlob thumbnailBlob = resized.get(uniqueName);
                    if (thumbnailBlob == null) {
                        thumbnailBlob = ThumbnailService.createThumbnailBlob(dbo.file().filename().getValue(), raw, imageTarget);
                        resized.put(uniqueName, thumbnailBlob);
                        log.info("ThumbnailBlob not cashed {}; cash size {}", dbo.file().filename().getValue(), resized.size());
                    }
                    thumbnailBlob = (ThumbnailBlob) thumbnailBlob.cloneEntity();
                    thumbnailBlob.setPrimaryKey(blobKey);
                    Persistence.service().persist(thumbnailBlob);
                }
                dbo.file().blobKey().setValue(blobKey);
            }

            break;
        case externalUrl:
            dbo.type().setValue(Media.Type.externalUrl);
            dbo.url().setValue(dto.uri().getValue());
            break;
        case youTube:
            if (!dto.uri().getValue().matches("[a-zA-Z0-9_-]{11}")) {
                throw new UserRuntimeException(i18n.tr("Invalid YouTube Video ID ''{0}''", dto.uri().getValue()));
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

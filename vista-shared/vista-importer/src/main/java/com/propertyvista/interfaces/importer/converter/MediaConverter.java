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
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.essentials.j2se.util.FileIOUtils;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.domain.FileImageThumbnailBlobDTO;

public class MediaConverter extends EntityBinder<MediaFile, MediaIO> {

    private final static Logger log = LoggerFactory.getLogger(MediaConverter.class);

    private static final I18n i18n = I18n.get(MediaConverter.class);

    private final MediaConfig mediaConfig;

    private final ImageTarget imageTarget;

    private static Collection<String> extensions = DownloadFormat.getExtensions(EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP));

    public MediaConverter(MediaConfig mediaConfig, ImageTarget imageTarget) {
        super(MediaFile.class, MediaIO.class, false);
        this.mediaConfig = mediaConfig;
        this.imageTarget = imageTarget;
    }

    @Override
    protected void bind() {
        bind(toProto.caption(), boProto.caption());
        bind(toProto.visibility(), boProto.visibility());
    }

    @Override
    public void copyBOtoTO(MediaFile dbo, MediaIO dto) {
        super.copyBOtoTO(dbo, dto);
        dto.mediaType().setValue(MediaIO.MediaType.file);
        if (mediaConfig.baseFolder != null) {
            dto.uri().setValue(mediaConfig.directory + dbo.blobKey().getStringView() + "-" + dbo.fileName().getStringView());
            try {
                BlobService.save(dbo.blobKey().getValue(), new File(mediaConfig.baseFolder + dto.uri().getValue()));
            } catch (IOException e) {
                throw new Error(e);
            }
        } else {
            dto.uri().setValue(dbo.id().getStringView() + dbo.fileName().getStringView());
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
                return i18n.tr("Unsupported Media File Type ''{0}'' Extension ''{1}''", dto.uri().getValue(), extension);
            }
        default:
            break;
        }
        return null;
    }

    //This is shared between created PMCs
    private static Map<String, FileImageThumbnailBlobDTO> resized = new Hashtable<String, FileImageThumbnailBlobDTO>();

    @Override
    public void copyTOtoBO(MediaIO dto, MediaFile dbo) {
        super.copyTOtoBO(dto, dbo);
        if (dto.mediaType().isNull()) {
            if (mediaConfig.ignoreMissingMedia) {
                return;
            } else {
                throw new UserRuntimeException(i18n.tr("Media Type Is Empty"));
            }
        }
        switch (dto.mediaType().getValue()) {
        case file:
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
                    throw new UserRuntimeException(i18n.tr("Unsupported Media File Type ''{0}'' Extension ''{1}''", dto.uri().getValue(), extension));
                }
            }
            dbo.fileName().setValue(file.getName());
            dbo.fileSize().setValue(Long.valueOf(file.length()).intValue());
            dbo.contentMimeType().setValue(MimeMap.getContentType(extension));
            dbo.timestamp().setValue(System.currentTimeMillis());

            if (!mediaConfig.mimizePreloadDataSize) {
                byte raw[] = getBinary(file);
                dbo.blobKey().setValue(BlobService.persist(raw, dbo.fileName().getValue(), dbo.contentMimeType().getValue()));
                ThumbnailService.persist(dbo.blobKey().getValue(), file.getName(), raw, imageTarget);
            } else {
                String uniqueName = MediaConverter.class.getName() + imageTarget + file.getAbsolutePath().toLowerCase(Locale.ENGLISH);
                Key blobKey = CacheService.get(uniqueName);
                if (blobKey == null) {
                    byte raw[] = getBinary(file);
                    blobKey = BlobService.persist(raw, dbo.fileName().getValue(), dbo.contentMimeType().getValue());
                    CacheService.put(uniqueName, blobKey);
                    FileImageThumbnailBlobDTO thumbnailBlob = resized.get(uniqueName);
                    if (thumbnailBlob == null) {
                        thumbnailBlob = ThumbnailService.createThumbnailBlob(dbo.fileName().getValue(), raw, imageTarget);
                        if (ApplicationMode.isDevelopment()) {
                            resized.put(uniqueName, thumbnailBlob);
                            log.info("ThumbnailBlob not cashed {}; cash size {}", dbo.fileName().getValue(), resized.size());
                        }
                    }
                    thumbnailBlob = (FileImageThumbnailBlobDTO) thumbnailBlob.duplicate();
                    thumbnailBlob.setPrimaryKey(blobKey);
                    ThumbnailService.persist(thumbnailBlob);
                }
                dbo.blobKey().setValue(blobKey);
            }

            break;
        default:
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

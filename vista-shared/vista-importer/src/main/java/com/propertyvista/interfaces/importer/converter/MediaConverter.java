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
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.xnap.commons.i18n.I18n;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.essentials.j2se.util.FileIOUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.services.MediaUploadService;
import com.propertyvista.domain.media.Media;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

public class MediaConverter extends EntityDtoBinder<Media, MediaIO> {

    private static I18n i18n = I18nFactory.getI18n();

    private final String baseFolder;

    private static Collection<String> extensions = DownloadFormat.getExtensions(MediaUploadService.supportedFormats);

    private final boolean ignoreMissingMedia;

    private final ImageTarget imageTarget;

    public MediaConverter(String baseFolder, boolean ignoreMissingMedia, ImageTarget imageTarget) {
        super(Media.class, MediaIO.class, false);
        this.baseFolder = baseFolder;
        this.ignoreMissingMedia = ignoreMissingMedia;
        this.imageTarget = imageTarget;
    }

    public MediaConverter(String baseFolder) {
        this(baseFolder, false, ImageTarget.Building);
    }

    @Override
    protected void bind() {
        bind(dtoProto.caption(), dboProto.caption());
        bind(dtoProto.visibleToPublic(), dboProto.visibleToPublic());
    }

    @Override
    public void copyDBOtoDTO(Media dbo, MediaIO dto) {
        super.copyDBOtoDTO(dbo, dto);
        switch (dbo.type().getValue()) {
        case file:
            dto.mediaType().setValue(MediaIO.MediaType.file);
            if (baseFolder != null) {
                dto.uri().setValue(dbo.file().blobKey().getStringView() + "-" + dbo.file().filename().getStringView());
                try {
                    BlobService.save(dbo.file().blobKey().getValue(), new File(baseFolder + dto.uri().getValue()));
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
            return i18n.tr("Media type is empty");
        }
        switch (dto.mediaType().getValue()) {
        case file:
            File file = new File(new File(baseFolder), dto.uri().getValue());
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

    @Override
    public void copyDTOtoDBO(MediaIO dto, Media dbo) {
        super.copyDTOtoDBO(dto, dbo);
        if (dto.mediaType().isNull()) {
            if (ignoreMissingMedia) {
                return;
            } else {
                throw new UserRuntimeException(i18n.tr("Media type is empty"));
            }
        }
        switch (dto.mediaType().getValue()) {
        case file:
            dbo.type().setValue(Media.Type.file);
            File file = new File(new File(baseFolder), dto.uri().getValue());
            if (!file.exists()) {
                file = FileIOUtils.findFileIgnoreCase(file);
                if (!file.exists()) {
                    if (ignoreMissingMedia) {
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
                if (ignoreMissingMedia) {
                    return;
                } else {
                    throw new UserRuntimeException(i18n.tr("Unsupported Media file ''{0}'' extension ''{1}''", dto.uri().getValue(), extension));
                }
            }
            dbo.file().filename().setValue(file.getName());
            dbo.file().contentMimeType().setValue(MimeMap.getContentType(extension));
            byte raw[] = getBinary(file);
            dbo.file().fileSize().setValue(raw.length);

            dbo.file().blobKey().setValue(BlobService.persist(raw, dbo.file().filename().getValue(), dbo.file().contentMimeType().getValue()));
            ThumbnailService.persist(dbo.file().blobKey().getValue(), file.getName(), raw, imageTarget);

            break;
        case externalUrl:
            dbo.type().setValue(Media.Type.externalUrl);
            dbo.url().setValue(dto.uri().getValue());
            break;
        case youTube:
            if (!dto.uri().getValue().matches("[a-zA-Z0-9_-]{11}")) {
                throw new UserRuntimeException(i18n.tr("Invalid YouTube VideoID ''{0}''", dto.uri().getValue()));
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

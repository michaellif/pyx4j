/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.blob;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.ThumpnailRescaleOp;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.domain.FileImageThumbnailBlob;
import com.propertyvista.server.domain.FileImageThumbnailBlobDTO;

/**
 * Simple DB base Blob storage abstraction for future refactoring.
 */
public class ThumbnailService {

    private final static Logger log = LoggerFactory.getLogger(ThumbnailService.class);

    private static final I18n i18n = I18n.get(ThumbnailService.class);

    public static ResampleParams getDefaultResampleParams(ImageTarget imageTarget) {
        ResampleParams params = new ResampleParams();
        params.clipThumpnailBorders = 2;
        switch (imageTarget) {
        case Building:
            params.crop = true;
            break;
        case Floorplan:
            break;
        default:
        }
        return params;
    }

    public static void persist(Key key, String fileName, byte[] originalContent, ImageTarget imageTarget) {
        switch (imageTarget) {
        case Building:
            ThumbnailService.persist(key, fileName, originalContent, getDefaultResampleParams(imageTarget), ImageConsts.BUILDING_XSMALL,
                    ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM, ImageConsts.BUILDING_LARGE, null);
            break;
        case Floorplan:
            ThumbnailService.persist(key, fileName, originalContent, getDefaultResampleParams(imageTarget), null, ImageConsts.FLOORPLAN_SMALL,
                    ImageConsts.FLOORPLAN_MEDIUM, ImageConsts.FLOORPLAN_LARGE, ImageConsts.FLOORPLAN_XLARGE);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static FileImageThumbnailBlobDTO createThumbnailBlob(String fileName, byte[] originalContent, ImageTarget imageTarget) {
        switch (imageTarget) {
        case Building:
            return createThumbnailBlob(fileName, originalContent, getDefaultResampleParams(imageTarget), ImageConsts.BUILDING_XSMALL,
                    ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM, ImageConsts.BUILDING_LARGE, null);
        case Floorplan:
            return createThumbnailBlob(fileName, originalContent, getDefaultResampleParams(imageTarget), null, ImageConsts.FLOORPLAN_SMALL,
                    ImageConsts.FLOORPLAN_MEDIUM, ImageConsts.FLOORPLAN_LARGE, ImageConsts.FLOORPLAN_XLARGE);
        default:
            throw new IllegalArgumentException();
        }
    }

    public static FileImageThumbnailBlobDTO createThumbnailBlob(String fileName, byte[] originalContent, ResampleParams params, Dimension xsmall,
            Dimension small, Dimension medum, Dimension large, Dimension xlarge) {
        FileImageThumbnailBlobDTO blob = EntityFactory.create(FileImageThumbnailBlobDTO.class);
        InputStream stream = null;
        try {
            BufferedImage inputImage = ImageIO.read(stream = new ByteArrayInputStream(originalContent));
            if (inputImage == null) {
                throw new UserRuntimeException(i18n.tr("Unable To Read The Image ''{0}''", fileName));
            }
            blob.xsmall().setValue(resample(fileName, inputImage, params, xsmall));
            blob.small().setValue(resample(fileName, inputImage, params, small));
            blob.medium().setValue(resample(fileName, inputImage, params, medum));
            blob.large().setValue(resample(fileName, inputImage, params, large));
            blob.xlarge().setValue(resample(fileName, inputImage, params, xlarge));
        } catch (IOException e) {
            log.error("Error", e);
            throw new UserRuntimeException(i18n.tr("Unable To Resample The Image ''{0}''", fileName));
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return blob;
    }

    public static void persist(Key key, String fileName, byte[] originalContent, ResampleParams params, Dimension xsmall, Dimension small, Dimension medum,
            Dimension large, Dimension xlarge) {
        FileImageThumbnailBlobDTO blob = createThumbnailBlob(fileName, originalContent, params, xsmall, small, medum, large, xlarge);
        blob.setPrimaryKey(key);
        ThumbnailService.persist(blob);
    }

    private static byte[] resample(String fileName, BufferedImage image, ResampleParams params, Dimension dimension) {
        if (dimension == null) {
            return null;
        }
        //Crop to match destination proportions
        int x;
        int y;
        int width;
        int height;

        int clipBorders = params.clipBorders;
        int imageWidth = image.getWidth() - clipBorders * 2;
        int imageHeight = image.getHeight() - clipBorders * 2;

        boolean resize = true;

        Dimension rescaleTarget;
        if (params.crop) {
            double p = 1.0 * imageWidth / dimension.width;
            if (p * dimension.height < imageHeight) {
                x = clipBorders;
                width = imageWidth;

                height = Double.valueOf(Math.ceil(p * dimension.height)).intValue();
                y = (imageHeight - height) / 2;
            } else {
                y = clipBorders;
                height = imageHeight;

                width = Double.valueOf(Math.ceil((1.0 * imageHeight / dimension.height) * dimension.width)).intValue();
                x = (imageWidth - width) / 2;
                if ((x + width) > imageWidth) {
                    width = imageWidth - x;
                }
            }
            image = image.getSubimage(x, y, width, height);
            rescaleTarget = new Dimension(dimension.width + 2 * params.clipThumpnailBorders, dimension.height + 2 * params.clipThumpnailBorders);
        } else {
            // TODO impl clipBorders
            rescaleTarget = new Dimension();
            if (image.getWidth() * dimension.height / dimension.width < image.getHeight()) {
                rescaleTarget.height = dimension.height;
                rescaleTarget.width = rescaleTarget.height * image.getWidth() / image.getHeight();

                if ((!params.enlarge) && (image.getHeight() < dimension.height)) {
                    resize = false;
                }
            } else {
                rescaleTarget.width = dimension.width;
                rescaleTarget.height = rescaleTarget.width * image.getHeight() / image.getWidth();

                if ((!params.enlarge) && (image.getWidth() < dimension.width)) {
                    resize = false;
                }
            }

            rescaleTarget = new Dimension(rescaleTarget.width + 2 * params.clipThumpnailBorders, rescaleTarget.height + 2 * params.clipThumpnailBorders);
        }

        UnsharpenMask unsharpenMask = UnsharpenMask.None;
        if (params.unsharpenMask != null) {
            switch (params.unsharpenMask) {
            case None:
                unsharpenMask = AdvancedResizeOp.UnsharpenMask.None;
                break;
            case Normal:
                unsharpenMask = AdvancedResizeOp.UnsharpenMask.Normal;
                break;
            case Soft:
                unsharpenMask = AdvancedResizeOp.UnsharpenMask.Soft;
                break;
            }
        }

        double scale = 1.0 * imageWidth / rescaleTarget.width;
        if (scale < 1.2) {
            unsharpenMask = AdvancedResizeOp.UnsharpenMask.None;
            log.debug("not mask set for {}", fileName);
        }

        BufferedImage rescaled;
        if (resize) {
            //Resize
            ThumpnailRescaleOp resampleOp = new ThumpnailRescaleOp(rescaleTarget.width, rescaleTarget.height);
            resampleOp.setUnsharpenMask(unsharpenMask);
            rescaled = resampleOp.filter(image, null);
        } else {
            rescaled = image;
        }
        if (params.clipThumpnailBorders != 0) {
            rescaled = rescaled.getSubimage(params.clipThumpnailBorders, params.clipThumpnailBorders, rescaled.getWidth() - 2 * params.clipThumpnailBorders,
                    rescaled.getHeight() - 2 * params.clipThumpnailBorders);
        }

        if (!params.crop) {
            // Create a buffered image that supports transparency
            BufferedImage fillIn = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fillIn.createGraphics();

            // place small image in a center
            int px = (dimension.width - rescaled.getWidth()) / 2;
            int py = (dimension.height - rescaled.getHeight()) / 2;

            g2.drawImage(rescaled, px, py, null);
            g2.dispose();
            rescaled = fillIn;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            switch (ImageConsts.THUMBNAIL_TYPE) {
            case jpg:
                ImageIO.write(rescaled, "jpg", out);
                break;
            case png:
                ImageIO.write(rescaled, "png", out);
                break;
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    public static void delete(Key key) {
        Persistence.service().delete(FileImageThumbnailBlobDTO.class, key);
    }

    public static void persist(FileImageThumbnailBlobDTO blob) {
        for (ThumbnailSize size : ThumbnailSize.values()) {
            persistImageThumbnail(blob, size);
        }
    }

    private static void persistImageThumbnail(FileImageThumbnailBlobDTO blob, ThumbnailSize size) {
        FileImageThumbnailBlob imageThumbnailBlob = EntityFactory.create(FileImageThumbnailBlob.class);
        byte[] data = null;
        switch (size) {
        case xsmall:
            data = blob.xsmall().getValue();
            break;
        case small:
            data = blob.small().getValue();
            break;
        case medium:
            data = blob.medium().getValue();
            break;
        case large:
            data = blob.large().getValue();
            break;
        case xlarge:
            data = blob.xlarge().getValue();
            break;
        default:
            data = blob.large().getValue();
            break;
        }
        if (data == null) {
            return;
        }
        imageThumbnailBlob.content().setValue(data);
        imageThumbnailBlob.thumbnailSize().setValue(size);
        imageThumbnailBlob.blobKey().setValue(blob.getPrimaryKey());
        Persistence.service().persist(imageThumbnailBlob);
    }

    public static boolean serve(Key key, ThumbnailSize size, HttpServletResponse response) throws IOException {
        EntityQueryCriteria<FileImageThumbnailBlob> criteria = EntityQueryCriteria.create(FileImageThumbnailBlob.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().blobKey(), key));
        criteria.add(PropertyCriterion.eq(criteria.proto().thumbnailSize(), size));
        FileImageThumbnailBlob blob = Persistence.service().retrieve(criteria);
        if (blob == null) {
            return false;
        } else {
            serve(blob.content().getValue(), response);
            return true;
        }
    }

    public static boolean serve(FileImageThumbnailBlobDTO blob, ThumbnailSize size, HttpServletResponse response) throws IOException {
        if (blob != null) {
            byte[] data = null;
            switch (size) {
            case xsmall:
                data = blob.xsmall().getValue();
                break;
            case small:
                data = blob.small().getValue();
                break;
            case medium:
                data = blob.medium().getValue();
                break;
            case large:
                data = blob.large().getValue();
                break;
            case xlarge:
                data = blob.xlarge().getValue();
                break;
            default:
                data = blob.large().getValue();
                break;
            }
            if (data == null) {
                return false;
            } else {
                serve(data, response);
                return true;
            }
        } else {
            return false;
        }
    }

    private static void serve(byte[] data, HttpServletResponse response) throws IOException {
        switch (ImageConsts.THUMBNAIL_TYPE) {
        case jpg:
            response.setContentType("image/jpeg");
            break;
        case png:
            response.setContentType("image/png");
            break;
        }
        response.setContentLength(data.length);
        ServletOutputStream out = response.getOutputStream();
        try {
            out.write(data);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}

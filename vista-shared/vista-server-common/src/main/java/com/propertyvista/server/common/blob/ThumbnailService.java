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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ThumpnailRescaleOp;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.shared.Dimension;

import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.server.domain.ThumbnailBlob;

/**
 * Simple DB base Blob storage abstraction for future refactoring.
 */
@SuppressWarnings("deprecation")
public class ThumbnailService {

    private final static Logger log = LoggerFactory.getLogger(ThumbnailService.class);

    private static I18n i18n = I18nFactory.getI18n();

    public static void persist(Key key, String fileName, byte[] originalContent, ImageTarget imageTarget) {
        switch (imageTarget) {
        case Building:
            ThumbnailService.persist(key, fileName, originalContent, ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM, ImageConsts.BUILDING_LARGE);
            break;
        case Floorplan:
            ThumbnailService.persist(key, fileName, originalContent, ImageConsts.FLOORPLAN_SMALL, ImageConsts.FLOORPLAN_MEDIUM, ImageConsts.FLOORPLAN_LARGE);
            break;
        }
    }

    public static void persist(Key key, String fileName, byte[] originalContent, Dimension small, Dimension medum, Dimension large) {
        ThumbnailBlob blob = EntityFactory.create(ThumbnailBlob.class);
        blob.setPrimaryKey(key);

        try {
            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(originalContent));
            if (inputImage == null) {
                throw new UserRuntimeException(i18n.tr("Unable to read the image ''{0}''", fileName));
            }
            blob.small().setValue(resample(inputImage, small));
            blob.medium().setValue(resample(inputImage, medum));
            blob.large().setValue(resample(inputImage, large));
        } catch (IOException e) {
            log.error("Error", e);
            throw new UserRuntimeException(i18n.tr("Unable to resample the image ''{0}''", fileName));
        }

        PersistenceServicesFactory.getPersistenceService().persist(blob);
    }

    private static byte[] resample(BufferedImage image, Dimension dimension) {
        //Crop to match destination proportions
        int x;
        int y;
        int width;
        int height;

        int clipBorders = 2;
        int imageWidth = image.getWidth() - clipBorders * 2;
        int imageHeight = image.getHeight() - clipBorders * 2;

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
        }
        image = image.getSubimage(x, y, width, height);

        int clipThumpnailBorders = 1;
        //Resize
        ThumpnailRescaleOp resampleOp = new ThumpnailRescaleOp(dimension.width + 2 * clipThumpnailBorders, dimension.height + 2 * clipThumpnailBorders);
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
        BufferedImage rescaled = resampleOp.filter(image, null);
        if (clipThumpnailBorders != 0) {
            rescaled = rescaled.getSubimage(clipThumpnailBorders, clipThumpnailBorders, rescaled.getWidth() - clipThumpnailBorders, rescaled.getHeight()
                    - clipThumpnailBorders);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(rescaled, "jpg", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    public static void delete(Key key) {
        PersistenceServicesFactory.getPersistenceService().delete(ThumbnailBlob.class, key);
    }

    public static void serve(Key key, ThumbnailSize size, HttpServletResponse response) throws IOException {
        ThumbnailBlob blob = PersistenceServicesFactory.getPersistenceService().retrieve(ThumbnailBlob.class, key);
        if (blob != null) {
            byte[] data = null;
            switch (size) {
            case small:
                data = blob.small().getValue();
                break;
            case medium:
                data = blob.medium().getValue();
                break;
            case large:
                data = blob.large().getValue();
                break;
            }
            response.setContentType("image/jpeg");
            response.setContentLength(data.length);
            ServletOutputStream out = response.getOutputStream();
            try {
                out.write(data);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }
}

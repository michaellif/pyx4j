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

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ThumpnailRescaleOp;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.site.shared.Dimension;

import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.server.domain.ThumbnailBlob;

/**
 * Simple DB base Blob storage abstraction for future refactoring.
 */
@SuppressWarnings("deprecation")
public class ThumbnailService {

    private final static Logger log = LoggerFactory.getLogger(ThumbnailService.class);

    public static void persist(Key key, byte[] originalContent, Dimension small, Dimension medum, Dimension large) {
        ThumbnailBlob blob = EntityFactory.create(ThumbnailBlob.class);
        blob.setPrimaryKey(key);

        try {
            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(originalContent));
            blob.small().setValue(resample(inputImage, small));
            blob.medum().setValue(resample(inputImage, medum));
            blob.large().setValue(resample(inputImage, large));
        } catch (IOException e) {
            log.error("Error", e);
            return;
        }

//        entity.name().setValue(name);
//        entity.content().setValue(content);
//        entity.contentType().setValue(contentType);
        PersistenceServicesFactory.getPersistenceService().persist(blob);
    }

    private static byte[] resample(BufferedImage image, Dimension dimension) {
        //Crop to match destination proportions
        int x;
        int y;
        int width;
        int height;
        double p = 1.0 * image.getWidth() / dimension.width;
        if (p * dimension.height < image.getHeight()) {
            x = 0;
            width = image.getWidth();

            height = Double.valueOf(Math.ceil(p * dimension.height)).intValue();
            y = (image.getHeight() - height) / 2;
        } else {
            y = 0;
            height = image.getHeight();

            width = Double.valueOf(Math.ceil((1.0 * image.getHeight() / dimension.height) * dimension.width)).intValue();
            x = (image.getWidth() - width) / 2;
        }
        image = image.getSubimage(x, y, width, height);

        //Resize
        ThumpnailRescaleOp resampleOp = new ThumpnailRescaleOp(dimension.width, dimension.height);
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
        BufferedImage rescaled = resampleOp.filter(image, null);

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
            case medum:
                data = blob.medum().getValue();
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

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 31, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.blob;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ThumpnailRescaleOp;

import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.shared.Dimension;

import com.propertyvista.crm.rpc.services.MediaUploadService;

public class ImageResizer {

    public static final Collection<String> imageExtensions = DownloadFormat.getExtensions(MediaUploadService.supportedFormats);

    private static I18n i18n = I18nFactory.getI18n();

    private final static Logger log = LoggerFactory.getLogger(ImageResizer.class);

    public static Dimension targetDimension = new Dimension(1280, 1024);

    public final static int clipBorders = 2;

    public final static int clipThumpnailBorders = 1;

    public static boolean verifImageOnly = true;

    public static void main(String[] args) {

        String srcFolderName = "E:\\Vista\\sl\\test";
        String dstFolderName = "E:\\Vista\\sl\\buildings-small";

        processDirectory(new File(srcFolderName), new File(dstFolderName));
    }

    private static class DirFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }

    private static class MediaFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return file.isFile() && FilenameUtils.isExtension(file.getName(), imageExtensions);
        }
    }

    public static void processDirectory(File srcDir, File dstDir) {
        if (!srcDir.isDirectory()) {
            throw new Error("Direcroty " + srcDir.getAbsolutePath() + " not found");
        }
        File[] dirs = srcDir.listFiles(new DirFileFilter());
        for (File d : dirs) {
            processDirectory(d, new File(dstDir, d.getName()));
        }
        File[] files = srcDir.listFiles(new MediaFileFilter());
        if (files.length > 0) {
            log.info("processing {}", srcDir.getAbsolutePath());
        }
        for (File f : files) {
            resizeFile(f, dstDir);
        }
    }

    private static void resizeFile(File file, File dstDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (!dstDir.exists()) {
                FileUtils.forceMkdir(dstDir);
            }

            BufferedImage inputImage = ImageIO.read(in = new FileInputStream(file));
            if (inputImage == null) {
                if (verifImageOnly) {
                    log.error("Unable to read the image ''{}''", file.getAbsolutePath());
                } else {
                    throw new UserRuntimeException(i18n.tr("Unable to read the image ''{0}''", file.getAbsolutePath()));
                }
            }
            if (!verifImageOnly) {
                resample(inputImage, targetDimension, new FileOutputStream(new File(dstDir, file.getName())));
            }
        } catch (IOException e) {
            log.error("Unable to read the image ''{}''", file.getAbsolutePath(), e);
            if (!verifImageOnly) {
                throw new UserRuntimeException(i18n.tr("Unable to resample the image ''{0}''", file.getAbsolutePath()));
            }
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

    }

    private static void resample(BufferedImage image, Dimension dimension, OutputStream out) {
        //Crop to match destination proportions
        int x;
        int y;
        int width;
        int height;

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

        //Resize
        ThumpnailRescaleOp resampleOp = new ThumpnailRescaleOp(dimension.width + 2 * clipThumpnailBorders, dimension.height + 2 * clipThumpnailBorders);
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
        BufferedImage rescaled = resampleOp.filter(image, null);
        if (clipThumpnailBorders != 0) {
            rescaled = rescaled.getSubimage(clipThumpnailBorders, clipThumpnailBorders, rescaled.getWidth() - clipThumpnailBorders, rescaled.getHeight()
                    - clipThumpnailBorders);
        }

        try {
            ImageIO.write(rescaled, "jpg", out);
        } catch (IOException e) {
            throw new Error(e);
        }

    }
}

/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-12-30
 * @author vlads
 */
package com.pyx4j.tester.server.file;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.shared.Dimension;

public class ImageThumbnailCreator {

    public static byte[] resample(byte[] originalContent, Dimension dimension) {
        InputStream stream = null;
        try {
            BufferedImage inputImage = ImageIO.read(stream = new ByteArrayInputStream(originalContent));
            if (inputImage == null) {
                throw new UserRuntimeException("Unable to read the image");
            }
            return toByte(resample(inputImage, dimension));
        } catch (IOException e) {
            throw new UserRuntimeException("Unable to resample the image");
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private static byte[] toByte(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private static BufferedImage resample(BufferedImage inputImage, Dimension dimension) {
        BufferedImage resizedImage = new BufferedImage(dimension.width, dimension.height, inputImage.getType());

        Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(inputImage, 0, 0, dimension.width, dimension.height, null);
        g.dispose();
        return resizedImage;
    }
}

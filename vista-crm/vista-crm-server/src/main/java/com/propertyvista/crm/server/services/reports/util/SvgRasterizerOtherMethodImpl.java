/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.util;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.j2se.SvgRootImpl;

public class SvgRasterizerOtherMethodImpl implements SvgRasterizer {

    private static class BufferedImageTranscoder extends ImageTranscoder {

        private BufferedImage image = null;

        @Override
        public BufferedImage createImage(int width, int height) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            return bi;
        }

        @Override
        public void writeImage(BufferedImage image, TranscoderOutput out) throws TranscoderException {
            this.image = image;
        }

        public BufferedImage getImage() {
            return this.image;
        }
    }

    @Override
    public BufferedImage rasterize(SvgRoot svgRoot, int width, int height) {
        BufferedImage rasterizedSvg = null;
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(width));
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(height));

        try {
            transcoder.transcode(new TranscoderInput(((SvgRootImpl) svgRoot).getDocument()), null);
            rasterizedSvg = transcoder.getImage();
        } catch (TranscoderException exeption) {
            throw new RuntimeException(exeption);
        }

        return rasterizedSvg;
    }

}

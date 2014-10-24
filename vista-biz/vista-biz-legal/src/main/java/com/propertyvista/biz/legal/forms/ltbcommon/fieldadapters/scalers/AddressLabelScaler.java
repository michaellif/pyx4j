/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.scalers;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;

public class AddressLabelScaler extends TextBoxScaler {

    @Override
    public float scaleToFit(String text, final Font font, Rectangle rect) throws DocumentException {
        /*
         * Address Label is expected to have 3 lines: name, street address, city/province/postal code
         * The name line is a comma separated list of the names of all responsible tenants. So, it has
         * a potential to be the longest line among the 3. Accordingly, the scaling algorithm will be:
         * 1. scale font until the longest of line2 and line3 fits the box width
         * 2. scale font until the line1 fits the upper portion of the box (above 2 lower lines)
         */
        String[] lines = text.split("\n");
        if (lines.length != 3) {
            return super.scaleToFit(text, font, rect);
        }

        BaseFont bf = font.getCalculatedBaseFont(false);
        Float fontSize = font.getSize();
        // get with of the longest line in the given text
        Float textWidth = 0F;
        int lineIdx = 0;
        for (int i = 1; i < lines.length; i++) {
            float width = bf.getWidthPoint(lines[i], fontSize);
            if (width > textWidth) {
                textWidth = width;
                lineIdx = i;
            }
        }
        // scale font per longest line
        Float scaledSize = fitLine(lines[lineIdx], rect.getWidth(), bf, fontSize);

        if (bf.getWidthPoint(lines[0], scaledSize) < rect.getWidth()) {
            return scaledSize;
        } else {
            // scale again to fit the first line into remaining upper area
            font.setSize(scaledSize);
            Rectangle topArea = new Rectangle(rect) {
                @Override
                public float getBottom() {
                    // NOTE: Y-coordinate goes bottom-top; assume line height = 1.1 * size
                    return (float) (super.getBottom() + 2 * font.getSize() * 1.1);
                }
            };
            return super.scaleToFit(lines[0], font, topArea);
        }
    }

    protected float fitLine(final String text, float maxWidth, BaseFont font, float maxFontSize) {
        float min = MIN_FONT_SIZE, max = maxFontSize;
        float size = maxFontSize;
        float precision = 0.1f;
        int attempt = 0;
        int maxAttempts = 10; // limit attempts in case it doesn't converge
        while ((max - min > size * precision) && (attempt < maxAttempts)) {
            if (attempt++ > 0) {
                size = (min + max) / 2;
            }
            float width = font.getWidthPoint(text, size);
            if (width < maxWidth) {
                min = size;
            } else {
                max = size;
            }
        }
        return size;
    }
}

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
 */
package com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.scalers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;

import com.propertyvista.biz.legal.forms.framework.mapping.Scaler;

public class TextBoxScaler implements Scaler {

    private static final Logger log = LoggerFactory.getLogger(TextBoxScaler.class);

    public static final float MIN_FONT_SIZE = 6.0f;

    @Override
    public float scaleToFit(String text, Font font, Rectangle rect) throws DocumentException {
        Phrase ph = new Phrase(text, font);
        float size = font.getSize();
        float precision = 0.1f;
        float min = MIN_FONT_SIZE;
        float max = size;
        int attempt = 0;
        int maxAttempts = 10; // limit attempts in case it doesn't converge
        while ((max - min > size * precision) && (attempt < maxAttempts)) {
            if (attempt++ > 0) {
                size = (min + max) / 2;
            }
            font.setSize(size);
            log.debug("-- Iteration: {}, Font: {}, Rect: {} x {}", attempt, size, rect.getRight() - rect.getLeft(), rect.getTop() - rect.getBottom());
            // try to print
            ColumnText ct = new ColumnText(null);
            ct.setSimpleColumn(ph, rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop(), size, Element.ALIGN_LEFT);
            if ((ct.go(true) & ColumnText.NO_MORE_TEXT) != 0) {
                // font is small enough to fit rectangle
                min = size;
                log.debug("---- Total lines: {}", ct.getLinesWritten());
            } else {
                max = size;
            }
        }
        log.debug("Done. Font size: {}", size);
        return size;
    }
}

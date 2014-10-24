/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 22, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.scalers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Rectangle;

public class TextScalingTest {

    @Test
    public void testScalingTextField() throws DocumentException {
        Font font = new Font(FontFamily.HELVETICA, 18, Font.BOLDITALIC, BaseColor.BLACK);
        Float newFontSize = new TextBoxScaler().scaleToFit("Very Long Text".toUpperCase(), font, new Rectangle(40, 40));
        assertEquals(12.75f, newFontSize, 0.5f);
    }

    @Test
    public void testScalingAddressLabel() throws DocumentException {
        String addrLabel = "OneGuy WithVeryLongName, HisRoomMate WithEvenLongerName\n"//
                + "123 TheLongestStreetNameYouCouldImagine St\n"//
                + "Hometown, ON, Canada";
        Rectangle rect = new Rectangle(260, 40);
        Font font = new Font(FontFamily.HELVETICA, 18, Font.BOLDITALIC, BaseColor.BLACK);
        Float newFontSize = new AddressLabelScaler().scaleToFit(addrLabel.toUpperCase(), font, rect);
        assertEquals(9.25f, newFontSize, 0.5f);

        font.setSize(12f); // start with other font size - make sure we get the same result
        newFontSize = new AddressLabelScaler().scaleToFit(addrLabel.toUpperCase(), font, rect);
        assertEquals(9.25f, newFontSize, 0.5f);
    }

}

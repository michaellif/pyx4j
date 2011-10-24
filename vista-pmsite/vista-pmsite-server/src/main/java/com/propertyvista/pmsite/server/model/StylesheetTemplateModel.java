/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.LoadableDetachableModel;

public class StylesheetTemplateModel extends LoadableDetachableModel<Map<String, Object>> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_COLOR = "#666666";

    private static final int VIBRANCE_CNT = 10;

    private final String primeColor;

    private final String accentColor;

    private final String bgColor;

    private final String fgColor;

    private final String hlightColor;

    public StylesheetTemplateModel(String baseColor) {
        switch (Integer.parseInt(baseColor)) {
        case 1:
            this.primeColor = "#269bff"; //-- H1
            this.accentColor = "#ff170f"; //- H2
            this.hlightColor = "#6f5879"; //- H3
            this.bgColor = "#ffffff"; //----- H4
            this.fgColor = "#000000"; //----- H5
            break;
        case 2:
        default:
            this.primeColor = "#072255"; //-- H1
            this.accentColor = "#8BAEDA"; //- H2
            this.hlightColor = "#5177A6"; //- H3
            this.bgColor = "#ffffff"; //----- H4
            this.fgColor = "#444444"; //----- H5
            break;
        }
    }

    @Override
    public Map<String, Object> load() {
        final Map<String, Object> varModel = new HashMap<String, Object>();
        varModel.putAll(generateColorMap("primeColor", primeColor));
        varModel.putAll(generateColorMap("accentColor", accentColor));
        varModel.putAll(generateColorMap("bgColor", bgColor));
        varModel.putAll(generateColorMap("fgColor", fgColor));
        varModel.putAll(generateColorMap("hlightColor", hlightColor));

        return varModel;
    }

    private static Map<String, String> generateColorMap(String keyBase, String color) {
        Color c = stringToColor(color);
        float[] HSBModel = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        final Map<String, String> colorMap = new HashMap<String, String>(VIBRANCE_CNT);
        for (int vib = 0; vib < VIBRANCE_CNT; vib++) {
            float vibrance = (float) (1.0 - (float) vib / VIBRANCE_CNT); // 1.0 -> 0.1
            colorMap.put(keyBase + "_" + (100 - 10 * vib), colorToString(new Color(HSBVtoRGB(HSBModel[0], HSBModel[1], HSBModel[2], vibrance))));
        }
        return colorMap;
    }

    private static int HSBVtoRGB(float hue, float saturation, float brightness, float vibrance) {
        float ns = saturation * vibrance;
        float nb = 1 - (1 - brightness) * vibrance;
        return Color.HSBtoRGB(hue, ns, nb);
    }

    private static Color stringToColor(String value) {
        if (value == null) {
            value = DEFAULT_COLOR;
        }
        try {
            // get color by hex or octal value
            return Color.decode(value);
        } catch (NumberFormatException nfe) {
            // if we can't decode lets try to get it by name
            try {
                // try to get a color by name using reflection
                final Field f = Color.class.getField(value);
                return (Color) f.get(null);
            } catch (Exception ce) {
                // if we can't get any color return black
                return Color.decode(DEFAULT_COLOR);
            }
        }
    }

    public static String colorToString(Color c) {
        String color = Integer.toHexString(c.getRGB() & 0x00ffffff);
        StringBuffer retval = new StringBuffer(7);
        retval.append("#");

        int fillUp = 6 - color.length();
        for (int i = 0; i < fillUp; i++) {
            retval.append("0");
        }

        retval.append(color);
        return retval.toString();
    }
}

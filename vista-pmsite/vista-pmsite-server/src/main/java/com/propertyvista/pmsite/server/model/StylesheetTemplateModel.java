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

    private final String baseColor;

    private final String accentColor;

    public StylesheetTemplateModel(String baseColor) {
        //this.baseColor = baseColor;
        this.baseColor = "#e2e2e2";
        this.accentColor = "red";
    }

    @Override
    public Map<String, Object> load() {
        Color bc = stringToColor(baseColor);
        float[] baseHsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
        Color ac = stringToColor(accentColor);
        float[] accentHsb = Color.RGBtoHSB(ac.getRed(), ac.getGreen(), ac.getBlue(), null);

        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("primeColor1a", colorToString(new Color(HSBVtoRGB(baseHsb[0], baseHsb[1], baseHsb[2], 0.6f))));
        vars.put("primeColor1b", colorToString(new Color(HSBVtoRGB(baseHsb[0], baseHsb[1], baseHsb[2], 0.8f))));
        vars.put("primeColor1c", colorToString(new Color(HSBVtoRGB(baseHsb[0], baseHsb[1], baseHsb[2], 1f))));
        vars.put("primeColor1d", colorToString(new Color(HSBVtoRGB(baseHsb[0], baseHsb[1], baseHsb[2], 1.2f))));
        vars.put("primeColor1e", colorToString(new Color(HSBVtoRGB(baseHsb[0], baseHsb[1], baseHsb[2], 1.4f))));

        vars.put("accentColor1a", colorToString(new Color(HSBVtoRGB(accentHsb[0], accentHsb[1], accentHsb[2], 0.7f))));
        vars.put("accentColor1b", colorToString(new Color(HSBVtoRGB(accentHsb[0], accentHsb[1], accentHsb[2], 1.0f))));
        vars.put("accentColor1c", colorToString(new Color(HSBVtoRGB(accentHsb[0], accentHsb[1], accentHsb[2], 1.3f))));

        return vars;
    }

    public static int HSBVtoRGB(float hue, float saturation, float brightness, float vibrance) {
        float ns = saturation * vibrance;
        float nb = 1 - (1 - brightness) * vibrance;
        return Color.HSBtoRGB(hue, ns, nb);
    }

    public static Color stringToColor(String value) {
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

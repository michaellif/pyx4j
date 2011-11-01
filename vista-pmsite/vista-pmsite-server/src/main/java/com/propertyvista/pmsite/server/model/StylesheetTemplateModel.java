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

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.LoadableDetachableModel;

import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColors;

import com.propertyvista.domain.site.SitePalette;

public class StylesheetTemplateModel extends LoadableDetachableModel<Map<String, Object>> {

    private static final long serialVersionUID = 1L;

    private final Palette palette;

    private static final int VIBRANCE_CNT = 10;

    public StylesheetTemplateModel(SitePalette sitePalette) {
        palette = new Palette();
        palette.putThemeColor(ThemeColors.object1, sitePalette.object1().getValue());
        palette.putThemeColor(ThemeColors.object2, sitePalette.object2().getValue());
        palette.putThemeColor(ThemeColors.contrast1, sitePalette.contrast1().getValue());
        palette.putThemeColor(ThemeColors.contrast2, sitePalette.contrast2().getValue());
        palette.putThemeColor(ThemeColors.background, sitePalette.background().getValue());
        palette.putThemeColor(ThemeColors.foreground, sitePalette.foreground().getValue());

//             palette.putThemeColor(ThemeColors.object1, "#269bff");
//            palette.putThemeColor(ThemeColors.object2, "#2670ff");
//            palette.putThemeColor(ThemeColors.contrast1, "#ff170f");
//            palette.putThemeColor(ThemeColors.contrast2, "#6f5879");
//            palette.putThemeColor(ThemeColors.background, "#ffffff");
//            palette.putThemeColor(ThemeColors.foreground, "#000000");
//            break;
//        case 2:
//        default:
//            palette.putThemeColor(ThemeColors.object1, "#072255");
//            palette.putThemeColor(ThemeColors.object2, "#5D466B");
//            palette.putThemeColor(ThemeColors.contrast1, "#8BAEDA");
//            palette.putThemeColor(ThemeColors.contrast2, "#5177A6");
//            palette.putThemeColor(ThemeColors.background, "#666666");
//            palette.putThemeColor(ThemeColors.foreground, "#444444");
//            break;
//        }
    }

    @Override
    public Map<String, Object> load() {
        final Map<String, Object> varModel = new HashMap<String, Object>();
        varModel.putAll(generateColorMap("object1", ThemeColors.object1));
        varModel.putAll(generateColorMap("object2", ThemeColors.object2));
        varModel.putAll(generateColorMap("contrast1", ThemeColors.contrast1));
        varModel.putAll(generateColorMap("contrast2", ThemeColors.contrast2));
        varModel.putAll(generateColorMap("background", ThemeColors.background));
        varModel.putAll(generateColorMap("foreground", ThemeColors.foreground));
        return varModel;
    }

    private Map<String, String> generateColorMap(String keyBase, ThemeColors color) {
        final Map<String, String> colorMap = new HashMap<String, String>(VIBRANCE_CNT);
        for (int vib = 0; vib < VIBRANCE_CNT; vib++) {
            float vibrance = (float) (1.0 - (float) vib / VIBRANCE_CNT); // 1.0 -> 0.1
            colorMap.put(keyBase + "_" + (100 - 10 * vib), palette.getThemeColor(color, vibrance));
        }
        return colorMap;
    }

}

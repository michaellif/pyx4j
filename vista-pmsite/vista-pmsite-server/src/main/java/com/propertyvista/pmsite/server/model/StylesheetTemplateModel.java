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
import org.apache.wicket.request.Request;

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColors;

import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.portal.rpc.portal.ImageConsts;

public class StylesheetTemplateModel extends LoadableDetachableModel<Map<String, Object>> {

    private static final long serialVersionUID = 1L;

    private final Palette palette;

    private static final int VIBRANCE_CNT = 10;

    public StylesheetTemplateModel(Request request) {
        SitePalette sitePalette = ((PMSiteWebRequest) request).getContentManager().getSiteDescriptor().sitePalette();
        Skin skin = ((PMSiteWebRequest) request).getContentManager().getSiteDescriptor().skin().getValue();
        palette = new Palette();
        palette.putThemeColor(
                ThemeColors.object1,
                ColorUtil.hsbToRgb((float) sitePalette.object1().getValue() / 360, (float) skin.getColorProperties()[0] / 100,
                        (float) skin.getColorProperties()[1] / 100));
        palette.putThemeColor(
                ThemeColors.object2,
                ColorUtil.hsbToRgb((float) sitePalette.object2().getValue() / 360, (float) skin.getColorProperties()[2] / 100,
                        (float) skin.getColorProperties()[3] / 100));
        palette.putThemeColor(
                ThemeColors.contrast1,
                ColorUtil.hsbToRgb((float) sitePalette.contrast1().getValue() / 360, (float) skin.getColorProperties()[4] / 100,
                        (float) skin.getColorProperties()[5] / 100));
        palette.putThemeColor(
                ThemeColors.contrast2,
                ColorUtil.hsbToRgb((float) sitePalette.contrast2().getValue() / 360, (float) skin.getColorProperties()[6] / 100,
                        (float) skin.getColorProperties()[7] / 100));
        palette.putThemeColor(
                ThemeColors.foreground,
                ColorUtil.hsbToRgb((float) sitePalette.background().getValue() / 360, (float) skin.getColorProperties()[8] / 100,
                        (float) skin.getColorProperties()[9] / 100));
        palette.putThemeColor(
                ThemeColors.background,
                ColorUtil.hsbToRgb((float) sitePalette.foreground().getValue() / 360, (float) skin.getColorProperties()[10] / 100,
                        (float) skin.getColorProperties()[11] / 100));

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

        varModel.put("image_building_xsmall_height", ImageConsts.BUILDING_XSMALL.height + "px");
        varModel.put("image_building_xsmall_width", ImageConsts.BUILDING_XSMALL.width + "px");
        varModel.put("image_building_small_height", ImageConsts.BUILDING_SMALL.height + "px");
        varModel.put("image_building_small_width", ImageConsts.BUILDING_SMALL.width + "px");
        varModel.put("image_building_medium_height", ImageConsts.BUILDING_MEDIUM.height + "px");
        varModel.put("image_building_medium_width", ImageConsts.BUILDING_MEDIUM.width + "px");
        varModel.put("image_building_large_height", ImageConsts.BUILDING_LARGE.height + "px");
        varModel.put("image_building_large_width", ImageConsts.BUILDING_LARGE.width + "px");

        varModel.put("image_floorplan_small_height", ImageConsts.FLOORPLAN_SMALL.height + "px");
        varModel.put("image_floorplan_small_width", ImageConsts.FLOORPLAN_SMALL.width + "px");
        varModel.put("image_floorplan_medium_height", ImageConsts.FLOORPLAN_MEDIUM.height + "px");
        varModel.put("image_floorplan_medium_width", ImageConsts.FLOORPLAN_MEDIUM.width + "px");
        varModel.put("image_floorplan_large_height", ImageConsts.FLOORPLAN_LARGE.height + "px");
        varModel.put("image_floorplan_large_width", ImageConsts.FLOORPLAN_LARGE.width + "px");

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

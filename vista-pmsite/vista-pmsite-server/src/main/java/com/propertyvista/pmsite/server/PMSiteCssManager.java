/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceReference.Key;
import org.apache.wicket.request.resource.ResourceReferenceRegistry;

import templates.TemplateResources;

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.pmsite.server.skins.base.PMSiteTheme;
import com.propertyvista.pmsite.server.skins.blacknight.PMSiteBlackNightTheme;
import com.propertyvista.pmsite.server.skins.future.PMSiteFutureTheme;
import com.propertyvista.pmsite.server.skins.power.PMSitePowerTheme;
import com.propertyvista.pmsite.server.skins.simple.PMSiteSimpleTheme;
import com.propertyvista.pmsite.server.skins.starlight.PMSiteStarlightTheme;
import com.propertyvista.pmsite.server.skins.strict.PMSiteStrictTheme;

public class PMSiteCssManager {
    // TODO - to be removed when a better way to handle old resource removal is found
    private static Map<String, Key> cssResourceKeyCache = new HashMap<String, Key>();

    private final PMSiteContentManager cm;

    public PMSiteCssManager(PMSiteContentManager cm) {
        this.cm = cm;
    }

    public ResourceReference getCssReference(PMSiteTheme.Stylesheet style) throws Exception {
        PMSiteTheme theme = null;
        switch (cm.getSiteDescriptor().skin().getValue()) {
        case skin1:
            theme = new PMSiteStarlightTheme(style);
            break;
        case skin2:
            theme = new PMSitePowerTheme(style);
            break;
        case skin3:
            theme = new PMSiteStrictTheme(style);
            break;
        case skin4:
            theme = new PMSiteSimpleTheme(style);
            break;
        case skin5:
            theme = new PMSiteFutureTheme(style);
            break;
        case skin6:
            theme = new PMSiteBlackNightTheme(style);
            break;
        default:
            throw new IllegalArgumentException(cm.getSiteDescriptor().skin().getValue().name());
        }
        // generate resource registry key
        final String css = theme.getCssString(getCssPalette());
        String var = DigestUtils.md5Hex(css);
        String scope = TemplateResources.class.getName();
        String name = cm.getSiteSkin() + "/" + style.name() + ".css";
        Key key = new Key(scope, name, null, null, var);
        // see if resource has changed
        Key oldKey = cssResourceKeyCache.get(name);
        ResourceReferenceRegistry registry = PMSiteApplication.get().getResourceReferenceRegistry();
        ResourceReference ref;
        if (!key.equals(oldKey)) {
            // create new reference if not found or changed
            if (oldKey != null) {
                registry.unregisterResourceReference(oldKey);
                cssResourceKeyCache.remove(name);
            }
            ref = new ResourceReference(key) {
                private static final long serialVersionUID = 1L;

                @Override
                public IResource getResource() {
                    return new ByteArrayResource("text/css", css.getBytes());
                }
            };
            registry.registerResourceReference(ref);
            cssResourceKeyCache.put(name, key);
        } else {
            ref = registry.getResourceReference(key, true, false);
        }
        return ref;
    }

    private Palette getCssPalette() {
        SiteDescriptor site = cm.getSiteDescriptor();
        SitePalette sitePalette = site.sitePalette();
        Skin skin = site.skin().getValue();
        Palette palette = new Palette();
        palette.putThemeColor(
                ThemeColor.object1,
                ColorUtil.hsbToRgb((float) sitePalette.object1().getValue() / 360, (float) skin.getColorProperties()[0] / 100,
                        (float) skin.getColorProperties()[1] / 100));
        palette.putThemeColor(
                ThemeColor.object2,
                ColorUtil.hsbToRgb((float) sitePalette.object2().getValue() / 360, (float) skin.getColorProperties()[2] / 100,
                        (float) skin.getColorProperties()[3] / 100));
        palette.putThemeColor(
                ThemeColor.contrast1,
                ColorUtil.hsbToRgb((float) sitePalette.contrast1().getValue() / 360, (float) skin.getColorProperties()[4] / 100,
                        (float) skin.getColorProperties()[5] / 100));
        palette.putThemeColor(
                ThemeColor.contrast2,
                ColorUtil.hsbToRgb((float) sitePalette.contrast2().getValue() / 360, (float) skin.getColorProperties()[6] / 100,
                        (float) skin.getColorProperties()[7] / 100));
        palette.putThemeColor(
                ThemeColor.foreground,
                ColorUtil.hsbToRgb((float) sitePalette.background().getValue() / 360, (float) skin.getColorProperties()[8] / 100,
                        (float) skin.getColorProperties()[9] / 100));
        palette.putThemeColor(
                ThemeColor.background,
                ColorUtil.hsbToRgb((float) sitePalette.foreground().getValue() / 360, (float) skin.getColorProperties()[10] / 100,
                        (float) skin.getColorProperties()[11] / 100));

        return palette;
    }
}

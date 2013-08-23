/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-07
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.site.gadgets.HomePageGadget;

@DiscriminatorValue("SiteDescriptor")
public interface SiteDescriptor extends Descriptor {

    //TODO make single instance objects part of framework
    public final String cacheKey = "SiteDescriptor";

    public enum Skin {

        skin1(23, 12, 37, 89, 100, 100, 100, 100, 0, 10, 0, 100),

        skin2(98, 70, 10, 98, 100, 100, 100, 100, 0, 10, 0, 100),

        skin3(92, 33, 35, 42, 98, 98, 98, 98, 0, 10, 0, 100),

        skin4(5, 30, 5, 40, 0, 50, 0, 60, 0, 10, 0, 20),

        skin5(5, 0, 5, 10, 0, 50, 0, 60, 0, 10, 0, 20),

        skin6(5, 0, 5, 10, 0, 50, 0, 60, 0, 10, 0, 20),

        crm(30, 70, 10, 98, 100, 100, 100, 100, 0, 40, 0, 100);

        private final int[] colorProperties;

        private Skin(int o1s, int o1b, int o2s, int o2b, int c1s, int c1b, int c2s, int c2b, int fs, int fb, int bs, int bb) {
            this.colorProperties = new int[] { o1s, o1b, o2s, o2b, c1s, c1b, c2s, c2b, fs, fb, bs, bb };
        }

        private Skin() {
            this(0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 100);
        }

        @Override
        public String toString() {
            switch (this) {
            case skin1:
                return "Starlight";
            case skin2:
                return "Power";
            case skin3:
                return "Strict";
            case skin4:
                return "Simple";
            case skin5:
                return "Future";
            case skin6:
                return "BlackNight";
            default:
                return super.toString();
            }
        }

        public int[] getColorProperties() {
            return colorProperties;
        }
    }

    @NotNull
    @ToString(index = 0)
    @Caption(name = "Website Skin")
    IPrimitive<Skin> skin();

    @Owned
    SitePalette sitePalette();

    @Caption(name = "Website Enabled")
    IPrimitive<Boolean> enabled();

    IPrimitive<Boolean> disableMapView();

    IPrimitive<Boolean> disableBuildingDetails();

    @Owned
    ResidentPortalSettings residentPortalSettings();

    @Owned
    SiteImageResource crmLogo();

    // Content ================================================================

    @Owned
    IList<HomePageGadget> homePageGadgetsNarrow();

    @Owned
    IList<HomePageGadget> homePageGadgetsWide();

    @Owned
    IList<CityIntroPage> cityIntroPages();

    // Branding ===============================================================

    @Owned
    IList<SiteTitles> siteTitles();

    @Owned
    IList<PortalLogoImageResource> logo();

    @Owned
    IList<HtmlContent> slogan();

    @Owned
    IList<HtmlContent> pmcInfo();

    @Owned
    IList<PortalImageSet> banner();

    @Owned
    IList<PageMetaTags> metaTags();

    @Owned
    IList<SocialLink> socialLinks();

    // Internals:

    @Owned
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    @MemberColumn(name = "updateFlag")
    SiteDescriptorChanges _updateFlag();
}

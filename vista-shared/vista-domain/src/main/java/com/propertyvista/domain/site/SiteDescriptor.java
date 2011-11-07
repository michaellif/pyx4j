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

import com.pyx4j.entity.annotations.Cached;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Cached
public interface SiteDescriptor extends IEntity {

    //TODO make single instance objects part of framework
    public final String cacheKey = "SiteDescriptor";

    @I18n
    public enum Skin {

        skin1(23, 12, 37, 89, 100, 100, 100, 100, 0, 10, 0, 100),

        skin2(98, 70, 10, 98, 100, 100, 100, 100, 0, 10, 0, 100),

        skin3(92, 33, 35, 42, 98, 98, 98, 98, 0, 10, 0, 100),

        crm(85, 90, 10, 98, 100, 100, 100, 100, 0, 40, 0, 100);

        private final int[] colorProperties;

        private Skin(int o1s, int o1b, int o2s, int o2b, int c1s, int c1b, int c2s, int c2b, int fs, int fb, int bs, int bb) {
            this.colorProperties = new int[] { o1s, o1b, o2s, o2b, c1s, c1b, c2s, c2b, fs, fb, bs, bb };
        }

        private Skin() {
            this(0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 100);
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public int[] getColorProperties() {
            return colorProperties;
        }
    }

    @NotNull
    @ToString(index = 0)
    @Caption(name = "Resident Portal Skin")
    IPrimitive<Skin> skin();

    @Owned
    SitePalette sitePalette();

    IList<Resource> logo();

    IList<Resource> slogan();

    @Owned
    IList<SiteTitles> siteTitles();

    // Image for landing page
    @Owned
    IList<Resource> images();

    @Owned
    IList<PageDescriptor> childPages();

    @Owned
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    @MemberColumn(name = "updateFlag")
    SiteDescriptorChanges _updateFlag();
}

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

        skin1(12, 23, 89, 37, 100, 100, 100, 100, 0, 10, 0, 100),

        skin2(70, 98, 10, 98, 100, 100, 100, 100, 0, 10, 0, 100),

        skin3(20, 80, 20, 80, 98, 98, 98, 98, 0, 10, 0, 100);

        private final Integer[] colorProperties;

        private Skin(Integer o1b, Integer o1s, Integer o2b, Integer o2s, Integer c1b, Integer c1s, Integer c2b, Integer c2s, Integer fb, Integer fs,
                Integer bb, Integer bs) {
            this.colorProperties = new Integer[] { o1b, o1s, o2b, o2s, c1b, c1s, c2b, c2s, fb, fs, bb, bs };
        }

        private Skin() {
            this(0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 100);
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public Integer[] getColorProperties() {
            return colorProperties;
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<Skin> skin();

    @Owned
    SitePalette sitePalette();

    IPrimitive<String> copyright();

    IList<Resource> logo();

    IList<Resource> slogan();

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

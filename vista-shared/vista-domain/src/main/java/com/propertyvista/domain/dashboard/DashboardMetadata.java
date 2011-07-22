/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-22
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.dashboard;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

import com.propertyvista.domain.ISharedUserEntity;

public interface DashboardMetadata extends ISharedUserEntity {

    @Translatable
    public enum DashboardType {
        system, building;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    public enum LayoutType {

        @Translation("One whole width column")
        One,

        @Translation("Two equal columns")
        Two11,

        @Translation("Two columns (33/67)")
        Two12,

        @Translation("Two columns (67/33)")
        Two21,

        @Translation("Three equal columns")
        Three,

        Report;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 1)
    @MemberColumn(name = "dashboardType")
    IPrimitive<DashboardType> type();

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> description();

    IPrimitive<LayoutType> layoutType();

    IPrimitive<Boolean> isFavorite();

    IPrimitive<Boolean> isShared();

    @Owned
    IList<GadgetMetadata> gadgets();
}

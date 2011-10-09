/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2011
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.StringHolder;

@DiscriminatorValue("ListerGadgetBaseSettings")
public interface ListerGadgetBaseSettings extends AbstractGadgetSettings {

    public enum RefreshInterval {

        @Translate("Never")
        Never(-1),

        @Translate("15 min")
        min15L(15),

        @Translate("30 min")
        min30(30),

        @Translate("1 hours")
        hour1(60),

        @Translate("2 hours")
        hour2(120);

        RefreshInterval(int value) {
            this.value = value;
        }

        private final int value;

        public int value() {
            return value;
        }

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    };

    public IPrimitive<RefreshInterval> refreshInterval();

    public IPrimitive<Integer> itemsPerPage();

    public IPrimitive<Integer> currentPage();

    /**
     * Use empty list of column names for default setting
     */
    @Owned
    public IList<StringHolder> columnPaths();

    @Owned
    public IList<SortEntity> sorting();
}

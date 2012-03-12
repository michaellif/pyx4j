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
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@DiscriminatorValue("UnitAvailability")
public interface UnitAvailability extends ListerGadgetBaseMetadata {
    enum FilterPreset {

        Vacant,

        Notice,

        @Translate("Vacant/Notice")
        VacantAndNotice,

        Rented,

        NetExposure;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    /** Defines the filtering criteria button by its caption */
    IPrimitive<FilterPreset> defaultFilteringPreset();

    /** <code>null</code> null means now */
    IPrimitive<LogicalDate> asFor();

    IPrimitive<Boolean> customizeDate();
}

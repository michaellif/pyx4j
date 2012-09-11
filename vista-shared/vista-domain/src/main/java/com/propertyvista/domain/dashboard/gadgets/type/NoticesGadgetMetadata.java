/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;

@DiscriminatorValue("Notices Gadget")
@Caption(name = "Notices Gadget", description = "TBD")
public interface NoticesGadgetMetadata extends GadgetMetadata {

    @I18n(strategy = I18nStrategy.IgnoreAll)
    enum NoticesFilter {
        THIS_MONTH, NEXT_MONTH, OVER_90_DAYS
    }

    enum NoticesGadgetMode {
        SUMMARY, NOTICES_DETAILS, VACANT_UNITS_DETAILS
    }

    IPrimitive<NoticesFilter> activeNoticesFilter();

    IPrimitive<NoticesGadgetMode> activeMode();

}

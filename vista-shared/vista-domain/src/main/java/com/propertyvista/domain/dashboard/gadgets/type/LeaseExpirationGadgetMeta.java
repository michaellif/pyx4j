/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;

@DiscriminatorValue("Lease Expiration Gadget")
@Caption(name = "Lease Expiration", description = "TBD")
public interface LeaseExpirationGadgetMeta extends GadgetMetadata {

    @I18n(strategy = I18nStrategy.IgnoreThis)
    enum LeaseExpirationGadgetMode {

        SUMMARY, LEASES_DETAILS, OCCUPIED_UNITS_DETAILS;

    }

    enum LeaseFilter {

        THIS_MONTH, NEXT_MONTH, OVER_90_DAYS, MONTH_ON_MONTH

    }

    IPrimitive<LeaseExpirationGadgetMode> activeMode();

    IPrimitive<LeaseFilter> activeLeaseFilterCriteria();

}

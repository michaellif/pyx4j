/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitiveSet;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.HasCustomizableDateGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.ListerGadgetBaseMetadata;
import com.propertyvista.domain.financial.PaymentRecord;

@DiscriminatorValue("PaymentsSummaryGadgetMetadata")
@Transient
@GadgetDescription(//@formatter:off
        name = "Payment Records Summary",
        description = "Displays payments aggregated by merchant account",
        keywords = { "Payments", "Collections", "Money"}
)//@formatter:on
public interface PaymentsSummaryGadgetMetadata extends ListerGadgetBaseMetadata, HasCustomizableDateGadgetMetadata, BuildingGadget {

    IPrimitiveSet<PaymentRecord.PaymentStatus> paymentStatus();

}

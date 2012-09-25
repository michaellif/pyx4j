/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.ListerGadgetBaseMetadata;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

/**
 * @deprecated it's gonna be replaced by an 'Arrears' gadget that shows summary and knows to zoom in to details
 */
@Deprecated
@DiscriminatorValue("ArrearsGadgetMeta")
@Caption(name = "Arrears Status", description = "Shows the information about lease arrears, including how long it is overdue, total balance, legal status information etc. This gadget can either show total arrears or arrears of specific type (i.e. rent, parking or other)")
@Transient
public interface ArrearsStatusGadgetMetadata extends ListerGadgetBaseMetadata, BuildingGadget {

    @Caption(description = "Choose which category of arrears to display")
    @NotNull
    IPrimitive<DebitType> category();

    IPrimitive<Boolean> customizeDate();

    @NotNull
    IPrimitive<LogicalDate> asOf();
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;

/**
 * The purpose of this DTO and its difference from {@link ArrearsSnapshot} is to transfer only one category of arrears.
 */
@Transient
public interface LeaseArrearsSnapshotDTO extends LeaseArrearsSnapshot {

    AgingBuckets selectedBuckets();

}

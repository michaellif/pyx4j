/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.tax.Tax;

/**
 * 
 * Corporate-wide reasons (AS defined 20 major)
 * 
 */
public interface LeaseAdjustmentReason extends IEntity {

    @ToString
    IPrimitive<String> name();

    IPrimitive<Boolean> precalculatedTax();

    GlCode glCode();

    /**
     * Fill in Service from @link AdjustmentTaxPolicy
     */
    @Transient
    IList<Tax> taxes();
}

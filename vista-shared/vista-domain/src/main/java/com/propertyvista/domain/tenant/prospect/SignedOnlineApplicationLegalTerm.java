/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm;
import com.propertyvista.domain.security.CustomerSignature;

@ToStringFormat("{0}")
public interface SignedOnlineApplicationLegalTerm extends IEntity {

    @ToString(index = 0)
    OnlineApplicationLegalTerm term();

    @Owned
    @Detached
    @Caption(name = "I agree to the Terms")
    CustomerSignature signature();

}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.tax;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.framework.PolicyNode;

//TODO add IVersionedEntity which will have effective date, expiration date
public interface Tax extends IEntity {

    @NotNull
    @Length(25)
    IPrimitive<String> name();

    @Length(50)
    IPrimitive<String> authority();

    @NotNull
    //TODO rate for particular period
    IPrimitive<BigDecimal> rate();

    IPrimitive<Boolean> compound();

    PolicyNode policyNode();
}
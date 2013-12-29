/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface CustomerAcceptedTerms extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Detached
    @ReadOnly
    Customer customer();

    // Link to VistaTermsV
    IPrimitive<Key> vistaTerms();

}

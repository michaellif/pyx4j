/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author vlads
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.AttachLevel;

import com.propertyvista.domain.security.common.AbstractPmcUser;

@Table
@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface PaymentMethod extends AbstractPaymentMethod {

    @ReadOnly
    @Editor(type = EditorType.label)
    @Detached(level = AttachLevel.ToStringMembers)
    AbstractPmcUser createdBy();
}

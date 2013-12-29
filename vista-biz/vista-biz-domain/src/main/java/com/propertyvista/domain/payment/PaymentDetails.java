/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;

@AbstractEntity
@Table(prefix = "payment")
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface PaymentDetails extends IEntity {

}

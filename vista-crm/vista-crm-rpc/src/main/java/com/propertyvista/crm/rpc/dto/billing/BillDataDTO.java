/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-05
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.billing;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.dto.BillDTO;

@Transient
@ExtendsBO(Bill.class)
public interface BillDataDTO extends IEntity {

    @ToString
    @EmbeddedEntity
    BillDTO bill();
}

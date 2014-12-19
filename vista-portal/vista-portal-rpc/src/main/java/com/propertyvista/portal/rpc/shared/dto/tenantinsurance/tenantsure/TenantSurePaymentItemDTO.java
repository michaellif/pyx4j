/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-21
 * @author ArtyomB
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

// TODO maybe tax should be another item of equal rights with pointer to other item?
@Transient
public interface TenantSurePaymentItemDTO extends IEntity {

    IPrimitive<String> description();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    /** absolute taxes applied to this item */
    IList<TenantSurePaymentItemTaxDTO> taxBreakdown();
}

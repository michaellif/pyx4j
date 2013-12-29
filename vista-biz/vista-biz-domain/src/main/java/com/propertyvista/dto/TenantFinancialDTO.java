/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;

@Transient
@ToStringFormat("{0}{1,choice,null#|!null#, ${1}}{2,choice,null#|!null#, {2}}{3,choice,null#|!null#, {3}}")
public interface TenantFinancialDTO extends IEntity {

    @ToString(index = 0)
    Person person();

    IList<CustomerScreeningIncome> incomes();

    IList<CustomerScreeningPersonalAsset> assets();

    IList<LeaseTermGuarantor> guarantors();

    // Quick summary:

    @ToString(index = 1)
    @Format("#,##0")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> consolidatedIncome();

    @ToString(index = 2)
    IPrimitive<String> employer();

    @ToString(index = 3)
    IPrimitive<String> position();
}

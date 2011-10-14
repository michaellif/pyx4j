/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@DiscriminatorValue("socialServices")
public interface IncomeInfoSocialServices extends IEntity, IEmploymentInfo {

    @Override
    @Caption(name = "Social Services Agency")
    IPrimitive<String> name();

    @Override
    @Caption(name = "Years receiving")
    IPrimitive<Double> employedForYears();

    @Override
    @Caption(name = "Social Service Agent or Case Worker")
    IPrimitive<String> supervisorName();

    @Override
    @Caption(name = "Social Service Agent's or Case Worker's Phone")
    @Editor(type = EditorType.phone)
    IPrimitive<String> supervisorPhone();

}

/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.settings;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@Caption(name = "Company Info")
public interface PmcCompanyInfo extends IEntity {

    IPrimitive<String> companyName();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<PmcCompanyInfoContact> contacts();
}

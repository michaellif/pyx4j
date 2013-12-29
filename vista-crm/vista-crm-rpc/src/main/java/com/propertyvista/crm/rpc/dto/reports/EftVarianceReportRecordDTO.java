/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.reports;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface EftVarianceReportRecordDTO extends IEntity {

    IPrimitive<String> building();

    IPrimitive<String> unit();

    IPrimitive<String> leaseId();

    /** holds only PK to create a hyperlink */
    Lease leaseId_();

    IList<EftVarianceReportRecordDetailsDTO> details();

    EftVarianceReportRecordLeaseTotalsDTO leaseTotals();

}

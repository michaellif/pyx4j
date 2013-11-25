/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.l1;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.legal.l1.L1FormFieldsData;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface L1GenerationWizardDTO extends IEntity {

    L1FormFieldsData formData();

    Lease leaseIdStub();

}

/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.l1;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.legal.l1.L1LandlordsContactInfo;
import com.propertyvista.domain.legal.l1.L1ScheduleAndPayment;
import com.propertyvista.domain.legal.l1.L1SignatureData;
import com.propertyvista.domain.legal.ltbcommon.LtbAgentContactInfo;

@Transient
public interface L1CommonFieldsDTO extends IEntity {

    IList<L1LandlordsContactInfo> landlordsContactInfos();

    LtbAgentContactInfo agentContactInfo();

    L1SignatureData signatureData();

    L1ScheduleAndPayment paymentAndScheduling();

}

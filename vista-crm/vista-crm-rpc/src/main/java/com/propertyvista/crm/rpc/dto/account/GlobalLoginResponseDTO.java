/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 15, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.account;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmUser;

@Transient
public interface GlobalLoginResponseDTO extends IEntity {

    Pmc pmc();

    CrmUser user();
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.organization;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

/**
 * Self administration
 * This is secure services, user need to be lodged in to do this actions.
 */
public interface CrmUserService extends AbstractCrudService<EmployeeDTO>, AbstractPasswordChangeService {

}
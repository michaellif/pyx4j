/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.security.UserCredentialEditDTO;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.domain.security.OperationsUser;

@Transient
@Caption(name = "User")
@ExtendsDBO(OperationsUser.class)
public interface OperationsUserDTO extends OperationsUser, UserCredentialEditDTO {

    IPrimitive<VistaOperationsBehavior> role();
}

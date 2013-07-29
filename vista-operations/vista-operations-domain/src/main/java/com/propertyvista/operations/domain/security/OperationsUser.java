/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.security;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Table;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.common.AbstractUser;

@Caption(name = "User")
@Table(namespace = VistaNamespace.operationsNamespace)
public interface OperationsUser extends AbstractUser {

}

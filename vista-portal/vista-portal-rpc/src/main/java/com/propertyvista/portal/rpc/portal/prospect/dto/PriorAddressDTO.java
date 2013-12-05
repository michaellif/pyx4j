/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.contact.AddressSimple;

@Transient
public interface PriorAddressDTO extends PriorAddress {

    AddressSimple address();
}

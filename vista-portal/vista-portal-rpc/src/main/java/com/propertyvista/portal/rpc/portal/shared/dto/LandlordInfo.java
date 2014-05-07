/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.shared.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LandlordInfo extends IEntity {

    IPrimitive<String> name();

    IPrimitive<String> address();

    IPrimitive<byte[]> logo();
}

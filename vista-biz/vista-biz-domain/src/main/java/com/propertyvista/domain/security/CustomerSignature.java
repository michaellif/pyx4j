/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.shared.ISignature;

import com.propertyvista.shared.adapters.CustomerSignatureAdapter;

@Adapters(entityModificationAdapters = CustomerSignatureAdapter.class)
public interface CustomerSignature extends ISignature {

    @Override
    CustomerUser signingUser();

}

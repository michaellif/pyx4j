/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2015
 * @author vlads
 */
package com.propertyvista.domain.pmc;

import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface PmcDnsConfigTO extends IEntity {

    // Read default value like in VistaDeployment.getBaseApplicationURL(VistaApplication.resident, false) only do not configure custom DNS
    @ReadOnly
    IPrimitive<String> dnsNameDefault();

    // Maps to PmcDnsName.enabled
    @ReadOnly
    IPrimitive<Boolean> dnsNameIsActive();

    // This is taken from config properties
    @ReadOnly
    IPrimitive<String> serverIPAddress();

    // ID of 'customerDnsName'  resolved and point to our Server
    @ReadOnly
    IPrimitive<Boolean> dnsResolved();

    // If 
    @ReadOnly
    IPrimitive<String> dnsResolutionMessage();

    /**
     * User enters:
     * www.customer.com -> we store only www
     * (next only for VistaApplication.resident)
     * my.customer.com -> we store "my", try to resolve "www.my" and if it is resolved we store it
     * www.my.customer.com -> see above, we store "www.my", try to resolve "my" and if it is resolved we store it
     * 
     * @return
     */
    // www.my.customer.com  or  www.customer.com,   we show what is the first record in table. 
    IPrimitive<String> customerDnsName();

}

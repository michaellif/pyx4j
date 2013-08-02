/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-01
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

public abstract class TenantSureConfiguration {

    public abstract boolean useCfcApiAdapterMockup();

    public abstract String cfcApiEndpointUrl();

    //TODO move cfcCredentials here

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                : ").append(getClass().getName()).append("\n");
        b.append("useCfcApiAdapterMockup            : ").append(useCfcApiAdapterMockup()).append("\n");
        b.append("cfcApiEndpointUrl                 : ").append(cfcApiEndpointUrl()).append("\n");
        //TODO b.append("cfcCredentials                   : ").append(cfcCredentials().userName).append("\n");
        return b.toString();
    }
}

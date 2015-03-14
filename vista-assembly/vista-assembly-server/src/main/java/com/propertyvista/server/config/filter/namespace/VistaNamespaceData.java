/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.namespace;

import com.pyx4j.config.server.NamespaceData;

import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.filter.special.SpecialURL;

public class VistaNamespaceData extends NamespaceData {

    protected VistaApplication application;

    protected PmcDnsName customerDnsName;

    private SpecialURL specialURL;

    public VistaNamespaceData() {
        super(null);
    }

    public VistaNamespaceData(VistaApplication app, String namespace) {
        super(namespace);
        this.application = app;
    }

    public VistaApplication getApplication() {
        return application;
    }

    public void setApplication(VistaApplication app) {
        this.application = app;
    }

    public PmcDnsName getCustomerDnsName() {
        return customerDnsName;
    }

    public void setCustomerDnsName(PmcDnsName customerDnsName) {
        this.customerDnsName = customerDnsName;
    }

    public SpecialURL getSpecialURL() {
        return specialURL;
    }

    public void setSpecialURL(SpecialURL specialURL) {
        this.specialURL = specialURL;
    }

}

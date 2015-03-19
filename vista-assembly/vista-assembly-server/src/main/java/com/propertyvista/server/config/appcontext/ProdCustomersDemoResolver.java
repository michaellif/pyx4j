/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2015
 * @author vlads
 */
package com.propertyvista.server.config.appcontext;

import com.propertyvista.config.deployment.ChaineApplicationContextResolver;
import com.propertyvista.config.deployment.EnvNApplicationContextResolver;
import com.propertyvista.config.deployment.SingleAppApplicationContextResolver;
import com.propertyvista.domain.security.common.VistaApplication;

public class ProdCustomersDemoResolver extends ChaineApplicationContextResolver {

    public ProdCustomersDemoResolver() {
        super(//
                new SingleAppApplicationContextResolver(".propertyvista.com", VistaApplication.crm), //
                new SingleAppApplicationContextResolver(".my-community.co", VistaApplication.resident), //
                new SingleAppApplicationContextResolver(".residentportalsite.com", VistaApplication.site), //
                new EnvNApplicationContextResolver("-cdemo.propertyvista.biz"));
    }
}

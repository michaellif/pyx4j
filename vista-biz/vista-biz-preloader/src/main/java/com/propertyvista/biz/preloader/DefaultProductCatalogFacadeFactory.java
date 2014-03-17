/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on March 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.preloader;

import com.pyx4j.config.server.FacadeFactory;

import com.propertyvista.biz.preloader.defaultcatalog.DefaultProductCatalogFacadeInternalImpl;
import com.propertyvista.biz.preloader.defaultcatalog.DefaultProductCatalogFacadeYardiImpl;
import com.propertyvista.shared.config.VistaFeatures;

public class DefaultProductCatalogFacadeFactory implements FacadeFactory<DefaultProductCatalogFacade> {

    @Override
    public DefaultProductCatalogFacade getFacade() {
        if (VistaFeatures.instance().yardiIntegration()) {
            return new DefaultProductCatalogFacadeYardiImpl();
        } else {
            return new DefaultProductCatalogFacadeInternalImpl();
        }
    }
}

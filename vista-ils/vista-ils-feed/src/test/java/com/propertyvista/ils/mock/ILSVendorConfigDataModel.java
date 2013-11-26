/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.mock;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.settings.ILSConfig;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.test.mock.MockDataModel;

public class ILSVendorConfigDataModel extends MockDataModel<ILSVendorConfig> {

    public ILSVendorConfigDataModel() {

    }

    @Override
    protected void generate() {
        ILSVendorConfig vendorConfig = EntityFactory.create(ILSVendorConfig.class);
        vendorConfig.vendor().setValue(ILSVendor.kijiji);
        vendorConfig.maxDailyAds().setValue(30);

        ILSConfig config = EntityFactory.create(ILSConfig.class);
        vendorConfig.config().set(config);

        Persistence.service().persist(config);
        Persistence.service().persist(vendorConfig);

        addItem(vendorConfig);
    }

}

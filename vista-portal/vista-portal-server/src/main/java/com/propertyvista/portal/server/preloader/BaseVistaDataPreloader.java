/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.IAddressFull.StreetDirection;
import com.propertyvista.common.domain.IAddressFull.StreetType;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Address.AddressType;
import com.propertyvista.portal.server.generator.SharedData;

abstract class BaseVistaDataPreloader extends AbstractDataPreloader {

    protected BaseVistaDataPreloader() {
        DataGenerator.setRandomSeed(100);
    }
}

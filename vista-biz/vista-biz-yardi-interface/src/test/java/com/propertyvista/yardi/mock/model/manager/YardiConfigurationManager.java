/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager;

import java.util.Collection;

import com.propertyvista.yardi.YardiInterface;

public interface YardiConfigurationManager extends YardiMockManager {

    void addProperty(Class<? extends YardiInterface> service, String propertyCode);

    void addChargeCode(Class<? extends YardiInterface> service, String chargeCode);

    Collection<String> getProperties(Class<? extends YardiInterface> service);

    Collection<String> getChargeCodes(Class<? extends YardiInterface> service);
}

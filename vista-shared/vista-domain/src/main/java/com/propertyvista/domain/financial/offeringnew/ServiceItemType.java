/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offeringnew;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.offeringnew.Feature.FeatureType;
import com.propertyvista.domain.financial.offeringnew.Service.ServiceType;

public interface ServiceItemType extends IEntity {

    IPrimitive<String> name();

    IPrimitive<ServiceType> serviceType();

    IPrimitive<FeatureType> featureType();

}

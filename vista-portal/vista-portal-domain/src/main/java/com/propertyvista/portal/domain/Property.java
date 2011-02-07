/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

public interface Property extends IEntity {

    IPrimitive<String> propertyCode();

    /**
     * Legal name of the property (max 120 char)
     */
    IPrimitive<String> name();

    /**
     * Property name used for marketing purposes (max 120 char)
     */
    IPrimitive<String> marketingName();

    ISet<Picture> pictures();

}

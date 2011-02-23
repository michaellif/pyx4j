/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-22
 * @author aroytbur
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain;


import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IEntity;

/**
 * For now assume add-ons have monthly cost to be used in calculation of total monthly cost
 * @author aroytbur
 *
 */
public interface AddOn extends IEntity {
	
    /**
     * Amenity type (max 32 chars)
     */
    IPrimitive<String> name();
    
    /**
     * Amenity type (max 32 chars)
     */
    IPrimitive<Integer> monthlyCost();

}

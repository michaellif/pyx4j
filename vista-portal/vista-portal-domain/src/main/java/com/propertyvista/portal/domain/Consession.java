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
 * For now we just assume 2 types of consessions:
 * - Free Months:
 *    certain number of months free
 * 
 * - Percent Discount:
 *     certain discount over a number of months
 * 
 * Need to redesign
 * @author aroytbur
 *
 */
public interface Consession extends IEntity {
    /**
     * Concession type (max 128 chars)
     */
    IPrimitive<String> name();
    
    public enum concessionType {
        freeMonths, percentDiscount
    }
    
    /**
     *  Number of months to apply concession to
     */
    IPrimitive<String> months();
    
    /**
     *  Percent discount, if applicable
     */
    IPrimitive<String> percent();
    
}

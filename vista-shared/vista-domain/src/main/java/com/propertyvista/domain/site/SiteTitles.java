/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface SiteTitles extends IEntity {

    @NotNull
    AvailableLocale locale();

    IPrimitive<String> crmHeader();

    IPrimitive<String> residentPortalTitle();

    IPrimitive<String> prospectPortalTitle();

    IPrimitive<String> copyright();

    IPrimitive<String> residentPortalPromotions();
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.property.asset.building.Building;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface CrmUserBuildings extends IEntity {

    @Indexed
    @MemberColumn(name = "usr")
    @JoinColumn
    @Detached
    CrmUser user();

    @JoinColumn
    @Detached
    Building building();

}

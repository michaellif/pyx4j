/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.n4;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface N4CandidateSearchCriteriaDTO extends IEntity {

    IPrimitive<Boolean> filterByBuildings();

    IList<Building> buildings();

    IPrimitive<Boolean> filterByPortfolios();

    IList<Portfolio> portfolios();

    @Caption(name = "Minimum Amount Owed")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> minAmountOwed();

    /** errors in definition of n4 policy delimited by '\n' char */
    // TODO Theoretically errors doesn't belong to this entity
    IPrimitive<String> n4PolicyErrors();

}

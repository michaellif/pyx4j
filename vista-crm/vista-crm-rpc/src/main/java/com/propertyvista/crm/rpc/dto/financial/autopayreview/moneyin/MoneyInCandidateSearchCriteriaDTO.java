/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface MoneyInCandidateSearchCriteriaDTO extends IEntity {

    IList<Portfolio> portfolios();

    IList<Building> buildings();

    IPrimitive<String> unit();

    IPrimitive<String> lease();

    IPrimitive<String> tenant();

}

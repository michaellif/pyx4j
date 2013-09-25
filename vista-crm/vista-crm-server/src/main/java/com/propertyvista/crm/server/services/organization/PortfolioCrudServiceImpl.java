/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.organization;

import java.util.HashSet;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public class PortfolioCrudServiceImpl extends AbstractCrudServiceImpl<Portfolio> implements PortfolioCrudService {

    private static final I18n i18n = I18n.get(PortfolioCrudServiceImpl.class);

    public PortfolioCrudServiceImpl() {
        super(Portfolio.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(Portfolio bo, Portfolio to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.buildings());
        to.buildings().set(bo.buildings());
        BuildingFolderUtil.stripExtraData(to.buildings());
    }

    @Override
    protected void persist(Portfolio bo, Portfolio to) {
        validate(bo);
        super.persist(bo, to);
    }

    private void validate(Portfolio portfolio) {
        // validate that entity doesn't contain the same building more than once
        HashSet<Building> keys = new HashSet<Building>(portfolio.buildings().size());
        for (Building building : portfolio.buildings()) {
            if (!keys.add(building)) {
                throw new UserRuntimeException(i18n.tr("the portfolio contains the same building ${1} more than once", building.propertyCode().getStringView()));
            }
        }
    }
}

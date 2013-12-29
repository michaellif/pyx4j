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
package com.propertyvista.crm.server.util;

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public final class BuildingsCriteriaNormalizer {

    private final Building buildingField;

    public BuildingsCriteriaNormalizer(Building buildingField) {
        this.buildingField = buildingField;
    }

    public <E extends IEntity> void addBuildingCriterion(EntityQueryCriteria<E> criteria, List<Portfolio> portfolios, List<Building> buildings) {
        List<Building> normalized = normalize(portfolios, buildings);
        if (normalized != null) {
            criteria.in(buildingField, normalized);
        }
    }

    /**
     * @return <code>null</code> if there's no selection or list filled with buildings populated with ids
     */
    public List<Building> normalize(List<Portfolio> portfolios, List<Building> buildings) {
        List<Building> selectedBuildings = new Vector<Building>();
        if (portfolios != null) {
            selectedBuildings.addAll(getPortfoliosBuildings(portfolios));
        }
        if (buildings != null) {
            selectedBuildings.addAll(buildings);
        }
        if (selectedBuildings.isEmpty()) {
            return null;
        } else {
            return selectedBuildings;
        }
    }

    private Vector<Building> getPortfoliosBuildings(List<Portfolio> portfolios) {
        Vector<Building> portfoliosBuildings = new Vector<Building>();
        if (!portfolios.isEmpty()) {
            EntityQueryCriteria<Portfolio> portfoliosCriteria = EntityQueryCriteria.create(Portfolio.class);
            portfoliosCriteria.in(portfoliosCriteria.proto().id(), new Vector<Portfolio>(portfolios));
            for (Portfolio pStub : portfolios) {
                Portfolio portfolio = Persistence.secureRetrieve(Portfolio.class, pStub.getPrimaryKey());
                Persistence.service().retrieveMember(portfolio.buildings(), AttachLevel.IdOnly);
                portfoliosBuildings.addAll(portfolio.buildings());
            }
        }
        return portfoliosBuildings;
    }

}

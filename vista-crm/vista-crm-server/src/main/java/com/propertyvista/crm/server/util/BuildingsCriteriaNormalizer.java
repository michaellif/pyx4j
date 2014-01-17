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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;
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
        List<Building> selectedBuildings = new ArrayList<Building>();
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

    public List<Building> normalizeDto(List<PortfolioForSelectionDTO> portfolioDtos, List<BuildingForSelectionDTO> buildingDtos) {
        // TODO this function should probably exist on server side, client should send identity stubs, these DTO objects are for selection and contain a lot of data that is not required
        List<Portfolio> portfolios = new ArrayList<Portfolio>(portfolioDtos.size());
        for (PortfolioForSelectionDTO dto : portfolioDtos) {
            portfolios.add(EntityFactory.createIdentityStub(Portfolio.class, dto.getPrimaryKey()));
        }
        List<Building> buildings = new ArrayList<Building>(buildingDtos.size());
        for (BuildingForSelectionDTO dto : buildingDtos) {
            buildings.add(EntityFactory.createIdentityStub(Building.class, dto.getPrimaryKey()));
        }
        return normalize(portfolios, buildings);
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

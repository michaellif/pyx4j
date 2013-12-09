/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class SelectPortfolioListServiceImpl extends AbstractListServiceImpl<Portfolio> implements SelectPortfolioListService {

    public SelectPortfolioListServiceImpl() {
        super(Portfolio.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }

    @Override
    public void getPortfoliosForSelection(AsyncCallback<Vector<PortfolioForSelectionDTO>> callback, EntityListCriteria<Portfolio> criteria) {
        EntitySearchResult<Portfolio> portfolios = Persistence.secureQuery(criteria);
        Vector<PortfolioForSelectionDTO> dtos = new Vector<PortfolioForSelectionDTO>(portfolios.getData().size());
        for (Portfolio p : portfolios.getData()) {
            dtos.add(convertTo4SelectionDto(p));
        }
        callback.onSuccess(dtos);
    }

    private PortfolioForSelectionDTO convertTo4SelectionDto(Portfolio p) {
        PortfolioForSelectionDTO dto = EntityFactory.create(PortfolioForSelectionDTO.class);
        dto.portfolioIdStub().setPrimaryKey(p.getPrimaryKey());
        dto.name().setValue(p.name().getValue());
        return dto;
    }
}

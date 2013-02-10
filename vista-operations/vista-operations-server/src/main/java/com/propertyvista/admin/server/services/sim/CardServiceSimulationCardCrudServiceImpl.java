/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.server.services.sim;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.admin.domain.dev.CardServiceSimulationCard;
import com.propertyvista.admin.rpc.services.sim.CardServiceSimulationCardCrudService;

public class CardServiceSimulationCardCrudServiceImpl extends AbstractCrudServiceImpl<CardServiceSimulationCard> implements CardServiceSimulationCardCrudService {

    public CardServiceSimulationCardCrudServiceImpl() {
        super(CardServiceSimulationCard.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

}

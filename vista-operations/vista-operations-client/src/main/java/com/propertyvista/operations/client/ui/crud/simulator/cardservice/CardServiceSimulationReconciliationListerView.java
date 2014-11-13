/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.pyx4j.site.client.backoffice.ui.prime.lister.IListerView;

import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCreateTO;

public interface CardServiceSimulationReconciliationListerView extends IListerView<CardServiceSimulationReconciliationRecord> {

    public interface Presenter extends IListerView.IListerPresenter<CardServiceSimulationReconciliationRecord> {

        public void createReconciliations(CardServiceSimulationReconciliationCreateTO to);

    }
}

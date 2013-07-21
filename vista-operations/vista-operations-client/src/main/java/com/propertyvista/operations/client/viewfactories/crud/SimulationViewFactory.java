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
package com.propertyvista.operations.client.viewfactories.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationCardEditorView;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationCardEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationCardListerView;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationCardListerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationMerchantAccountEditorView;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationMerchantAccountEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationMerchantAccountListerView;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationMerchantAccountListerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationTransactionEditorView;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationTransactionEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationTransactionListerView;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationTransactionListerViewImpl;

public class SimulationViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {

        if (!map.containsKey(type)) {
            if (type.equals(CardServiceSimulationCardListerView.class)) {
                map.put(type, new CardServiceSimulationCardListerViewImpl());
            } else if (type.equals(CardServiceSimulationCardEditorView.class)) {
                map.put(type, new CardServiceSimulationCardEditorViewImpl());

            } else if (type.equals(CardServiceSimulationTransactionListerView.class)) {
                map.put(type, new CardServiceSimulationTransactionListerViewImpl());
            } else if (type.equals(CardServiceSimulationTransactionEditorView.class)) {
                map.put(type, new CardServiceSimulationTransactionEditorViewImpl());

            } else if (type.equals(CardServiceSimulationMerchantAccountListerView.class)) {
                map.put(type, new CardServiceSimulationMerchantAccountListerViewImpl());
            } else if (type.equals(CardServiceSimulationMerchantAccountEditorView.class)) {
                map.put(type, new CardServiceSimulationMerchantAccountEditorViewImpl());
            }
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}

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
package com.propertyvista.admin.client.viewfactories.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.IView;

import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationEditorView;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationEditorViewImpl;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationListerView;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationListerViewImpl;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionEditorView;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionEditorViewImpl;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionListerView;
import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionListerViewImpl;

public class SimulationViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView> T instance(Class<T> type) {

        if (!map.containsKey(type)) {
            if (type.equals(CardServiceSimulationListerView.class)) {
                map.put(type, new CardServiceSimulationListerViewImpl());
            } else if (type.equals(CardServiceSimulationEditorView.class)) {
                map.put(type, new CardServiceSimulationEditorViewImpl());
            } else if (type.equals(CardServiceSimulationTransactionListerView.class)) {
                map.put(type, new CardServiceSimulationTransactionListerViewImpl());
            } else if (type.equals(CardServiceSimulationTransactionEditorView.class)) {
                map.put(type, new CardServiceSimulationTransactionEditorViewImpl());
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

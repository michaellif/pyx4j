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
package com.propertyvista.admin.client.ui.crud.cardservicesimulation;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.client.ui.crud.lister.ListerViewImplBase;

import com.propertyvista.admin.domain.dev.CardServiceSimulation;

public class CardServiceSimulationListerViewImpl extends ListerViewImplBase<CardServiceSimulation> implements CardServiceSimulationListerView {

    private static class CardServiceSimulationLister extends ListerBase<CardServiceSimulation> {

        public CardServiceSimulationLister() {
            super(CardServiceSimulation.class, true, true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().cardType()).build(),
                    new MemberColumnDescriptor.Builder(proto().number()).build(),
                    new MemberColumnDescriptor.Builder(proto().expiryDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().balance()).build(),
                    new MemberColumnDescriptor.Builder(proto().responseCode()).build()
            );//@formatter:off
        }

    }

    public CardServiceSimulationListerViewImpl() {
        setLister(new CardServiceSimulationLister());
    }

}

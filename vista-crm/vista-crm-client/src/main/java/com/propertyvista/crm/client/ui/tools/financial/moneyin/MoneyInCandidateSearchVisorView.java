/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-05-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.site.client.ui.visor.AbstractVisorPane;

import com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid.MoneyInCandidateLister;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;

public class MoneyInCandidateSearchVisorView extends AbstractVisorPane {

    private final MoneyInCandidateLister candidateLister;

    public MoneyInCandidateSearchVisorView(Controller controller, ListerDataSource<MoneyInCandidateDTO> dataSource) {
        super(controller);

        candidateLister = new MoneyInCandidateLister();
        candidateLister.setDataSource(dataSource);
        setContentPane(candidateLister);
    }

    public void populateLister() {
        candidateLister.obtain(0);
    }

}

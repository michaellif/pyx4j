/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author ernestog
 */
package com.propertyvista.portal.server.portal.resident.services;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.marketing.PortalResidentMarketingTip;
import com.propertyvista.portal.rpc.portal.resident.dto.QuickTipTO;
import com.propertyvista.portal.rpc.portal.resident.services.QuickTipService;
import com.propertyvista.server.TaskRunner;

public class QuickTipServiceImpl implements QuickTipService {

    @Override
    public void getQuickTips(AsyncCallback<Vector<QuickTipTO>> callback) {
        Vector<QuickTipTO> quickTips = TaskRunner.runInOperationsNamespace(new Callable<Vector<QuickTipTO>>() {
            @Override
            public Vector<QuickTipTO> call() {
                Vector<QuickTipTO> tipsList = null;
                EntityQueryCriteria<PortalResidentMarketingTip> criteria = EntityQueryCriteria.create(PortalResidentMarketingTip.class);
                List<PortalResidentMarketingTip> list = Persistence.service().query(criteria);
                if (!list.isEmpty()) {
                    tipsList = new Vector<QuickTipTO>();
                    for (PortalResidentMarketingTip tip : list) {
                        QuickTipTO tipTO = EntityFactory.create(QuickTipTO.class);
                        tipTO.target().setValue(tip.target().getValue());
                        tipTO.content().setValue(tip.content().getValue());
                        tipsList.add(tipTO);
                    }
                }
                return tipsList;
            }
        });

        callback.onSuccess(quickTips);

    }

}

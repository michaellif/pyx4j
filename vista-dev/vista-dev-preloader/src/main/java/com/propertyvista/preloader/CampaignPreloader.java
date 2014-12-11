/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.server.domain.CampaignHistory;
import com.propertyvista.server.domain.CampaignTrigger;
import com.propertyvista.server.domain.PhoneCallCampaign;

public class CampaignPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        PhoneCallCampaign campaign = EntityFactory.create(PhoneCallCampaign.class);

        //campaign.triger().setValue(CampaignTriger.Registration);
        //campaign.message().setValue("Hello ${b}, thank you for registering as Potential Tenant. Property Vista will call you later.");

        campaign.trigger().setValue(CampaignTrigger.ApplicationCompleted);
        campaign.message().setValue("Hello ${b}, at this point our presentation is concluded. Thank you very much for your time.");

        Persistence.service().persist(campaign);

        return "Created " + 1 + " PhoneCallCampaign";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(CampaignHistory.class, PhoneCallCampaign.class);
    }

}

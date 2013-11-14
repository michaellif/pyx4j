/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity.steps;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.services.AbstractWizardStepService;
import com.propertyvista.portal.shared.activity.AbstractWizardStepActivity;
import com.propertyvista.portal.shared.ui.IWizardStepView;

public abstract class AbstractProspectWizardStepActivity<E extends IEntity> extends AbstractWizardStepActivity<E> {

    public AbstractProspectWizardStepActivity(Class<? extends IWizardStepView<E>> viewType, AbstractWizardStepService<E> service) {
        super(viewType, service);
    }

    @Override
    public void navigateOut() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Status());
    }

}

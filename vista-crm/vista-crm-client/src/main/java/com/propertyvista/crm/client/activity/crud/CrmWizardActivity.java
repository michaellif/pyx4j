/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-26
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.activity.AbstractWizardActivity;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.rpc.CrudAppPlace;

public class CrmWizardActivity<E extends IEntity> extends AbstractWizardActivity<E> {

    public CrmWizardActivity(CrudAppPlace place, IWizard<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        super(place, view, service, entityClass);
    }
}

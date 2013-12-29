/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.domain.security.VistaCrmBehavior;

public class ListerActivityFactory {

    public static <E extends IEntity> AbstractListerActivity<E> create(Place place, ILister<E> view, AbstractListService<E> service, Class<E> entityClass,
            final VistaCrmBehavior... whoCanAdd) {
        return new AbstractListerActivity<E>(place, view, service, entityClass) {
            @Override
            public boolean canCreateNewItem() {
                return SecurityController.checkAnyBehavior(whoCanAdd);
            }
        };
    }

}

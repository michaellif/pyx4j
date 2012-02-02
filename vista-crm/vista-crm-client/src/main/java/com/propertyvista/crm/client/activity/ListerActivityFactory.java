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

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.domain.security.VistaCrmBehavior;

public class ListerActivityFactory {

    public static <E extends IEntity> ListerActivityBase<E> create(Place place, IListerView<E> view, AbstractListService<E> service, Class<E> entityClass,
            final VistaCrmBehavior... whoCanAdd) {
        return new ListerActivityBase<E>(place, view, service, entityClass) {
            @Override
            public boolean canEditNew() {
                return SecurityController.checkAnyBehavior(whoCanAdd);
            }
        };
    }

}

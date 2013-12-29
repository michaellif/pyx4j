/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 7, 2013
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.domain.security.VistaCrmBehavior;

public class ListerControllerFactory {

    public static <E extends IEntity> ListerController<E> create(ILister<E> view, AbstractListService<E> service, Class<E> entityClass,
            final VistaCrmBehavior... whoCanAdd) {
        return new ListerController<E>(view, service, entityClass) {
            @Override
            public boolean canCreateNewItem() {
                return SecurityController.checkAnyBehavior(whoCanAdd);
            }
        };
    }

}

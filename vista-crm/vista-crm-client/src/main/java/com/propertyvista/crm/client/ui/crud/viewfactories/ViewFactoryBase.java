/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import java.util.HashMap;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

public abstract class ViewFactoryBase {

    protected static HashMap<Class<? extends IView<? extends IEntity>>, IView<? extends IEntity>> map = new HashMap<Class<? extends IView<? extends IEntity>>, IView<? extends IEntity>>();

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        return map.get(type);
    }
}

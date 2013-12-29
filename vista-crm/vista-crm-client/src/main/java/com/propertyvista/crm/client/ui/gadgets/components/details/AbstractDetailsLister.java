/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components.details;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;

/**
 * A lister that allows to zoom-in into details entry
 */
public class AbstractDetailsLister<E extends IEntity> extends EntityDataTablePanel<E> {

    public AbstractDetailsLister(Class<E> clazz) {
        super(clazz, true, false);
    }

    @Override
    protected void onItemSelect(E item) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(proto().getInstanceValueClass()).formViewerPlace(item.getPrimaryKey()));
    };
}

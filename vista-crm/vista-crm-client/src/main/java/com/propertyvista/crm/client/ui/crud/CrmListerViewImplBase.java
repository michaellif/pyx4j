/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-18
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.ListerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;

public class CrmListerViewImplBase<E extends IEntity> extends ListerViewImplBase<E> {

    private final CrmTitleBar header;

    public CrmListerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        addNorth(header = new CrmTitleBar(AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption()), VistaCrmTheme.defaultHeaderHeight);
        header.setHeight("100%"); // fill all that defaultHeaderHeight!..
    }
}

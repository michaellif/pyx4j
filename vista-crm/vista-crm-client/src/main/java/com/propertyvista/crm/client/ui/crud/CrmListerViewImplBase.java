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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.ListerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public class CrmListerViewImplBase<E extends IEntity> extends ListerViewImplBase<E> {

    private static I18n i18n = I18nFactory.getI18n(CrmListerViewImplBase.class);

    public CrmListerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption()), 3);
    }
}

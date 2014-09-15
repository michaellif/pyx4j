/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;

public class CrmViewersComponentFactory extends VistaViewersComponentFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public CField<?, ?> create(IObject<?> member) {
        if (member.getOwner() == null) {
            throw new Error("Factory doesn't have editor for " + member);
        }
        MemberMeta mm = member.getMeta();
        if (mm.isEntity() && !mm.isOwnedRelationships()) {
            @SuppressWarnings("unchecked")
            CrudAppPlace place = AppPlaceEntityMapper.resolvePlace((Class<IEntity>) mm.getObjectClass());
            if (place != null) {
                return new CEntityCrudHyperlink(place);
            }
        }
        return super.create(member);

    }
}

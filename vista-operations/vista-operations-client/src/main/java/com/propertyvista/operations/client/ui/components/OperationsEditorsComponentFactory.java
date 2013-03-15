/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.components;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;

public class OperationsEditorsComponentFactory extends VistaEditorsComponentFactory {

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        return super.create(member);
    }

    public static <E extends IEntity> CEntityCrudHyperlink<E> createEntityHyperlink(Class<E> entityClass) {
        CEntityCrudHyperlink<E> link = new CEntityCrudHyperlink<E>(AppPlaceEntityMapper.resolvePlace(entityClass));
        link.inheritViewable(false);
        link.setViewable(true);
        return link;
    }

    public static <E extends IEntity> CEntityCollectionCrudHyperlink<ICollection<E, ?>> createEntityCollectionHyperlink(Class<E> entityClass) {
        CEntityCollectionCrudHyperlink<ICollection<E, ?>> link = new CEntityCollectionCrudHyperlink<ICollection<E, ?>>(
                AppPlaceEntityMapper.resolvePlaceClass(entityClass));
        link.inheritViewable(false);
        link.setViewable(true);
        return link;
    }
}

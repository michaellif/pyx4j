/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Vector;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

public class GenericConverter {

    //TODO move to more generic place
    public static <S extends IEntity, D extends S> S down(D src, Class<S> dstClass) {
        S dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }

    //TODO move to more generic place
    public static <S extends IEntity, D extends S> D up(S src, Class<D> dstClass) {
        D dst = EntityFactory.create(dstClass);
        dst.set(src);
        return dst;
    }

    //TODO move to more generic place
    public static <E extends IEntity, D extends E> EntitySearchResult<D> up(EntitySearchResult<E> resultE, Class<D> dstClass) {
        EntitySearchResult<D> result = new EntitySearchResult<D>();
        result.setEncodedCursorReference(resultE.getEncodedCursorReference());
        result.hasMoreData(resultE.hasMoreData());
        Vector<D> data = new Vector<D>();
        for (E entity : resultE.getData()) {
            data.add(up(entity, dstClass));
        }
        result.setData(data);
        return result;
    }

    //TODO move to more generic place
    public static <D extends IEntity> EntitySearchCriteria<D> down(EntitySearchCriteria<? extends D> src, Class<D> dstClass) {
        EntitySearchCriteria<D> dst = EntitySearchCriteria.create(dstClass);
        dst.setPageNumber(src.getPageNumber());
        dst.setPageSize(src.getPageSize());

        //TODO convert search criteria
        return dst;
    }
}

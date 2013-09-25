/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.oapi.binder;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityBinder;

public abstract class AbstractPersister<E extends IEntity, DTO extends IEntity> extends EntityBinder<E, DTO> {

    protected AbstractPersister(Class<E> dboClass, Class<DTO> dtoClass) {
        super(dboClass, dtoClass);
    }

    protected abstract E retrieve(DTO dto);

    protected E create(DTO dto) {
        return EntityFactory.create(boClass);
    }

    public final void persist(DTO dto) {
        E entity = retrieve(dto);
        if (entity == null) {
            entity = create(dto);
        }
        copyTOtoBO(dto, entity);
        Persistence.service().persist(entity);
    }
}

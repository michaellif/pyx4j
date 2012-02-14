/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertvista.generator.III;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;

public class PersistableDataFactory extends BasicGeneratorContext {

    private final boolean doPersist;

    private PersistableDataFactory(GeneratorContext factory) {
        this.doPersist = true;
    }

    public PersistableDataFactory(Generator<?>... generators) {
        this.doPersist = false;
    }

    @Override
    protected GeneratorContext newDataFactory() {
        return new PersistableDataFactory(this);
    }

    @Override
    public <E extends IEntity> E get(Class<E> type) {
        E entity = super.get(type);
        if (entity.getPrimaryKey() == null && doPersist) {
            Persistence.service().persist(entity);
        }
        return entity;
    }

    public <E extends IEntity> E getPersisted(Class<E> type) {
        E entity = super.get(type);
        Persistence.service().persist(entity);
        return entity;
    }

}

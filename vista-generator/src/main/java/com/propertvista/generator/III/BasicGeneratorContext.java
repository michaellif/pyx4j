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

import java.util.HashMap;
import java.util.List;

import com.pyx4j.entity.shared.IEntity;

public class BasicGeneratorContext implements GeneratorContext {

    private HashMap<Class<? extends IEntity>, List<? extends IEntity>> map;

    public BasicGeneratorContext(Generator<?>... generators) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public <E extends IEntity> E get(Class<E> type) {
        // ! fresh
        // map.get(type.class) != null - > return last

        // Find generarator for type.
        // context = newDataFactory();
        // context.put(entiies);
        // u = new TypeGenerator(context).generate();
        // ? persisted u -> persist
        // map.put(u);
        return null;
    }

    public <E extends IEntity> E create(Class<E> type, IEntity... entities) {
        // TODO Auto-generated method stub
        return null;
    }

    protected GeneratorContext newDataFactory() {
        return null;
    }

}

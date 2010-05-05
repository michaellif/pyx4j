/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-05-05
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityCSVReciver<E extends IEntity> implements CSVReciver {

    private final Class<E> entityClass;

    private final List<E> list = new Vector<E>();

    private final List<Path> headersPath = new Vector<Path>();

    public EntityCSVReciver(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T extends IEntity> EntityCSVReciver<T> create(Class<T> entityClass) {
        return new EntityCSVReciver<T>(entityClass);
    }

    @Override
    public void onHeader(String[] headers) {
        E entity = EntityFactory.create(entityClass);
        EntityMeta em = entity.getEntityMeta();
        Map<String, Path> memebersNames = new HashMap<String, Path>();
        for (String memberName : em.getMemberNames()) {
            IObject<?> memeber = entity.getMember(memberName);
            if (memeber instanceof IPrimitive<?>) {
                memebersNames.put(columnName(memeber), memeber.getPath());
            }
        }
        for (String header : headers) {
            headersPath.add(memebersNames.get(header));
        }
    }

    protected String columnName(IObject<?> memeber) {
        return memeber.getMeta().getCaption();
    }

    @Override
    public boolean canContuneLoad() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRow(String[] value) {
        E entity = EntityFactory.create(entityClass);

        int i = 0;
        for (Path path : headersPath) {
            if (path != null) {
                IPrimitive primitive = (IPrimitive<?>) entity.getMember(path);
                primitive.setValue(primitive.pars(value[i]));
            }
            i++;
            if (i >= value.length) {
                break;
            }
        }

        list.add(entity);
    }

    public List<E> getEntities() {
        return list;
    }

    public List<E> loadFile(String fileName) {
        CSVLoad.loadFile(fileName, this);
        return getEntities();
    }

}

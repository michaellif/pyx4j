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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.gwt.server.DateUtils;

public class EntityCSVReciver<E extends IEntity> implements CSVReciver {

    private final static Logger log = LoggerFactory.getLogger(EntityCSVReciver.class);

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
        log.debug("headers {}", (Object) headers);
        E entity = EntityFactory.create(entityClass);
        EntityMeta em = entity.getEntityMeta();
        Map<String, Path> membersNames = new HashMap<String, Path>();
        for (String memberName : em.getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            if (member instanceof IPrimitive<?>) {
                membersNames.put(columnName(member), member.getPath());
            }
        }
        for (String header : headers) {
            Path path = membersNames.get(header);
            if (path != null) {
                headersPath.add(path);
                continue;
            }
            try {
                headersPath.add(entity.getMember(header).getPath());
                continue;
            } catch (RuntimeException e) {
                // Unknown member
            }
            if (header.contains(".")) {
                IEntity ent = entity;
                IObject<?> member = null;
                for (String headerPart : header.split("\\.")) {
                    member = ent.getMember(headerPart);
                    if (member instanceof IEntity) {
                        ent = (IEntity) member;
                    } else {
                        ent = null;
                    }
                }
                headersPath.add(member.getPath());
            } else {
                log.warn("Unknown header [{}]", header);
                headersPath.add(null);
            }
        }
    }

    protected String columnName(IObject<?> member) {
        return member.getMeta().getCaption();
    }

    @Override
    public boolean canContuneLoad() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRow(String[] value) {
        //log.debug("value line {}", (Object) value);
        E entity = EntityFactory.create(entityClass);

        int i = 0;
        for (Path path : headersPath) {
            if (path != null) {
                IPrimitive primitive = (IPrimitive<?>) entity.getMember(path);
                primitive.setValue(parsValue(primitive, value[i]));
            }
            i++;
            if (i >= value.length) {
                break;
            }
        }

        list.add(entity);
    }

    protected Object parsValue(IPrimitive<?> primitive, String value) {
        if (Date.class.isAssignableFrom(primitive.getValueClass())) {
            return DateUtils.detectDateformat(value);
        } else if (Number.class.isAssignableFrom(primitive.getValueClass())) {
            // Is Local English?
            return primitive.pars(value.replaceAll(",", ""));
        } else {
            return primitive.pars(value);
        }
    }

    public List<E> getEntities() {
        return list;
    }

    public List<E> loadFile(String fileName) {
        CSVLoad.loadFile(fileName, this);
        return getEntities();
    }

}

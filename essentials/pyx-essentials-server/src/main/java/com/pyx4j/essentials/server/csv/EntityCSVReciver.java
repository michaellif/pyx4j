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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.FIFO;
import com.pyx4j.commons.LogicalDate;
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

    private int headerLinesCount = 1;

    private int headersMatchMinimum = 1;

    private FIFO<String[]> headersStack;

    public EntityCSVReciver(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T extends IEntity> EntityCSVReciver<T> create(Class<T> entityClass) {
        return new EntityCSVReciver<T>(entityClass);
    }

    public int getHeaderLinesCount() {
        return headerLinesCount;
    }

    public void setHeaderLinesCount(int headerLinesCount) {
        this.headerLinesCount = headerLinesCount;
        if (this.headerLinesCount > 1) {
            headersStack = new FIFO<String[]>(this.headerLinesCount);
        }
    }

    public int getHeadersMatchMinimum() {
        return headersMatchMinimum;
    }

    public void setHeadersMatchMinimum(int headersMatchMinimum) {
        this.headersMatchMinimum = headersMatchMinimum;
    }

    private String[] combineHeader(String[] headers) {
        if (this.headerLinesCount == 1) {
            return headers;
        } else {
            headersStack.push(headers);
            if (headersStack.size() < this.headerLinesCount) {
                return null;
            } else {
                Vector<String> headersCombined = new Vector<String>();
                int maxLen = headers.length;
                for (String[] headerLine : headersStack) {
                    if (maxLen < headerLine.length) {
                        maxLen = headerLine.length;
                    }
                }
                // Initialize empty
                for (int i = 0; i < maxLen; i++) {
                    headersCombined.add("");
                }
                for (String[] headerLine : headersStack) {
                    int col = 0;
                    for (String header : headerLine) {
                        headersCombined.set(col, combineHeader(headersCombined.get(col), header));
                        col++;
                    }
                }
                return headersCombined.toArray(new String[headersCombined.size()]);
            }
        }
    }

    protected String trimHeader(String headerfragmet) {
        return headerfragmet.replace((char) 0xA0, ' ').trim();
    }

    protected String combineHeader(String headerfragmet1, String headerfragmet2) {
        return CommonsStringUtils.nvl_concat(trimHeader(headerfragmet1), trimHeader(headerfragmet2), " ");
    }

    @Override
    public boolean onHeader(String[] headers) {
        log.debug("headers {}", (Object) headers);

        String[] headersCombined = combineHeader(headers);
        if (headersCombined == null) {
            return false;
        }

        E entity = EntityFactory.create(entityClass);
        EntityMeta em = entity.getEntityMeta();
        Map<String, Path> membersNames = new HashMap<String, Path>();
        for (String memberName : em.getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            if (member instanceof IPrimitive<?>) {
                membersNames.put(columnName(member), member.getPath());
            }
        }
        int headersFound = 0;
        headersPath.clear();
        nextHeader: for (String header : headersCombined) {
            Path path = membersNames.get(header);
            if (path != null) {
                headersPath.add(path);
                headersFound++;
                continue nextHeader;
            }
            try {
                headersPath.add(entity.getMember(header).getPath());
                headersFound++;
                continue nextHeader;
            } catch (RuntimeException e) {
                // Unknown member
            }
            if (header.contains(".")) {
                IEntity ent = entity;
                IObject<?> member = null;
                for (String headerPart : header.split("\\.")) {
                    try {
                        member = ent.getMember(headerPart);
                    } catch (RuntimeException e) {
                        // Unknown member
                        continue nextHeader;
                    }
                    if (member instanceof IEntity) {
                        ent = (IEntity) member;
                    } else {
                        ent = null;
                    }
                }
                headersPath.add(member.getPath());
                headersFound++;
            } else {
                log.warn("Unknown header [{}]", header);
                headersPath.add(null);
            }
        }
        return (getHeadersMatchMinimum() <= headersFound);
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
        if (LogicalDate.class.isAssignableFrom(primitive.getValueClass())) {
            if ("".equals(value)) {
                return null;
            } else {
                return new LogicalDate(DateUtils.detectDateformat(value));
            }
        } else if (Date.class.isAssignableFrom(primitive.getValueClass())) {
            if ("".equals(value)) {
                return null;
            } else {
                return DateUtils.detectDateformat(value);
            }
        } else if (Number.class.isAssignableFrom(primitive.getValueClass())) {
            // Is Local English?
            return primitive.parse(value.replaceAll(",", ""));
        } else {
            return primitive.parse(value);
        }
    }

    public List<E> getEntities() {
        return list;
    }

    public List<E> loadFile(String resourceName) {
        CSVLoad.loadFile(resourceName, this);
        return getEntities();
    }

}

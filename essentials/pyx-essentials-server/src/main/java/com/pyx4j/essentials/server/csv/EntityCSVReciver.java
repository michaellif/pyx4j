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
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.FIFO;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class EntityCSVReciver<E extends IEntity> implements CSVReciver {

    private final static Logger log = LoggerFactory.getLogger(EntityCSVReciver.class);

    private static final I18n i18n = I18n.get(EntityCSVReciver.class);

    private final Class<E> entityClass;

    private final List<E> list = new Vector<E>();

    private final List<Path> headersPath = new Vector<Path>();

    private boolean headerIgnoreCase = false;

    private boolean verifyRequiredHeaders = false;

    private boolean verifyRequiredValues = false;

    private int headerLinesCountMin = 1;

    private int headerLinesCountMax = 1;

    private int headersMatchMinimum = 1;

    private FIFO<String[]> headersStack;

    private int currentRow = 0;

    public EntityCSVReciver(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T extends IEntity> EntityCSVReciver<T> create(Class<T> entityClass) {
        return new EntityCSVReciver<T>(entityClass);
    }

    public int getHeaderLinesCountMin() {
        return headerLinesCountMin;
    }

    public int getHeaderLinesCountMax() {
        return headerLinesCountMax;
    }

    public void setHeaderLinesCount(int headerLinesCountMin, int headerLinesCountMax) {
        this.headerLinesCountMin = headerLinesCountMin;
        this.headerLinesCountMax = headerLinesCountMax;
        if (this.headerLinesCountMax > 1) {
            headersStack = new FIFO<String[]>(this.headerLinesCountMax);
        }
    }

    public int getHeadersMatchMinimum() {
        return headersMatchMinimum;
    }

    public void setHeadersMatchMinimum(int headersMatchMinimum) {
        this.headersMatchMinimum = headersMatchMinimum;
    }

    public boolean isHeaderIgnoreCase() {
        return headerIgnoreCase;
    }

    public void setHeaderIgnoreCase(boolean headerIgnoreCase) {
        this.headerIgnoreCase = headerIgnoreCase;
    }

    public boolean isVerifyRequiredHeaders() {
        return verifyRequiredHeaders;
    }

    public void setVerifyRequiredHeaders(boolean verifyRequiredHeaders) {
        this.verifyRequiredHeaders = verifyRequiredHeaders;
    }

    public boolean isVerifyRequiredValues() {
        return verifyRequiredValues;
    }

    public void setVerifyRequiredValues(boolean verifyRequiredValues) {
        this.verifyRequiredValues = verifyRequiredValues;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    private String[] combineHeader(String[] headers, int headerLinesCount) {
        if (headerLinesCount == 1) {
            return headers;
        } else {
            if (headersStack.size() < headerLinesCount) {
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
                int line = 0;
                for (String[] headerLine : headersStack) {
                    line++;
                    if (line > (headersStack.size() - headerLinesCount)) {
                        int col = 0;
                        for (String header : headerLine) {
                            headersCombined.set(col, combineHeader(headersCombined.get(col), header));
                            col++;
                        }
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
        currentRow++;
        log.debug("headers {}", (Object) headers);

        if (headersStack == null) {
            return matchHeader(headers, true);
        } else {
            headersStack.push(headers);
            for (int headerLinesCount = headerLinesCountMax; headerLinesCount >= headerLinesCountMin; headerLinesCount--) {
                String[] headersCombined = combineHeader(headers, headerLinesCount);
                if ((headersCombined != null) && (matchHeader(headersCombined, headerLinesCount == headerLinesCountMax))) {
                    return true;
                }
            }
            return false;
        }
    }

    protected boolean matchHeader(String[] headers, boolean throwError) {
        E entity = EntityFactory.create(entityClass);
        EntityMeta em = entity.getEntityMeta();
        Map<String, Path> membersNames = new HashMap<String, Path>();
        for (String memberName : em.getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            if (member instanceof IPrimitive<?>) {
                String names[] = columnNames(member);
                if (names != null) {
                    for (String name : names) {
                        if (isHeaderIgnoreCase()) {
                            name = name.toLowerCase(Locale.ENGLISH);
                        }
                        membersNames.put(name, member.getPath());
                    }
                }
            }
        }
        int headersFound = 0;
        headersPath.clear();
        nextHeader: for (String header : headers) {
            String name = header;
            if (isHeaderIgnoreCase()) {
                name = name.toLowerCase(Locale.ENGLISH);
            }
            Path path = membersNames.get(name);
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
                if (!"".equals(header)) {
                    log.warn("Unknown header [{}]", header);
                }
                headersPath.add(null);
            }
        }
        if (getHeadersMatchMinimum() <= headersFound) {
            if (isVerifyRequiredHeaders()) {
                return verifyRequiredHeaders(throwError);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    protected String[] columnNames(IObject<?> member) {
        ImportColumn importColumn = member.getMeta().getAnnotation(ImportColumn.class);
        if (importColumn == null) {
            return new String[] { member.getMeta().getCaption() };
        } else if (importColumn.ignore()) {
            return null;
        } else {
            return importColumn.names();
        }
    }

    protected boolean verifyRequiredHeaders(boolean throwError) {
        E entity = EntityFactory.create(entityClass);
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            MemberMeta memberMeta = member.getMeta();
            if ((memberMeta.getObjectClassType() == ObjectClassType.Primitive) && (memberMeta.getAnnotation(NotNull.class) != null)) {
                ImportColumn importColumn = member.getMeta().getAnnotation(ImportColumn.class);
                if ((importColumn != null) && (importColumn.ignore())) {
                    continue;
                }
                if (!headersPath.contains(member.getPath())) {
                    if (throwError) {
                        throw new UserRuntimeException(i18n.tr("Missing required column ''{0}''", memberMeta.getCaption()));
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean canContuneLoad() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRow(String[] value) {
        currentRow++;
        //log.debug("value line {}", (Object) value);
        E entity = EntityFactory.create(entityClass);

        if (value.length != 0) {
            int i = 0;
            for (Path path : headersPath) {
                if (path != null) {
                    @SuppressWarnings("rawtypes")
                    IPrimitive primitive = (IPrimitive<?>) entity.getMember(path);
                    primitive.setValue(parsValue(primitive, value[i]));
                }
                i++;
                if (i >= value.length) {
                    break;
                }
            }
        }

        onRow(entity);
    }

    public void onRow(E entity) {
        if (!entity.isNull()) {
            if (isVerifyRequiredValues()) {
                for (Path path : headersPath) {
                    if (path != null) {
                        @SuppressWarnings("rawtypes")
                        IPrimitive primitive = (IPrimitive<?>) entity.getMember(path);
                        if (primitive.isNull() && (primitive.getMeta().getAnnotation(NotNull.class) != null)) {
                            throw new UserRuntimeException(i18n.tr("Missing required value for column ''{0}'' in row {1}", primitive.getMeta().getCaption(),
                                    getCurrentRow()));
                        }
                    }
                }
            }
            list.add(entity);
        }
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
            if ("".equals(value)) {
                return null;
            } else {
                // Is Local English?
                return primitive.parse(value.replaceAll(",", ""));
            }
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

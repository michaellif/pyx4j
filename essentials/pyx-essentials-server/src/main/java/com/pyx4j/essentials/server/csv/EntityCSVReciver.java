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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.FIFO;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.impl.PrimitiveHandler;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

public class EntityCSVReciver<E extends IEntity> implements CSVReciver {

    private final static Logger log = LoggerFactory.getLogger(EntityCSVReciver.class);

    private static final I18n i18n = I18n.get(EntityCSVReciver.class);

    public static final String DEFAULT_COLLECTION_DELIM = ";";

    private final Class<E> entityClass;

    private final E entityModel;

    private final EntityMeta entityMeta;

    private final List<E> list = new Vector<E>();

    protected List<Path> headersPath;

    protected Map<String, Path> membersNames;

    private boolean headerIgnoreCase = false;

    private boolean memberNamesAsHeaders = true;

    private boolean verifyRequiredHeaders = false;

    private boolean verifyRequiredValues = false;

    private int headerLinesCountMin = 1;

    private int headerLinesCountMax = 1;

    private int headersMatchMinimum = 1;

    private FIFO<String[]> headersStack;

    private int currentRow = 0;

    private String collectionDelimiter = DEFAULT_COLLECTION_DELIM;

    public static <T extends IEntity> EntityCSVReciver<T> create(Class<T> entityClass) {
        return new EntityCSVReciver<T>(entityClass);
    }

    public EntityCSVReciver(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.entityModel = EntityFactory.create(entityClass);
        this.entityMeta = entityModel.getEntityMeta();
    }

    public int getHeaderLinesCountMin() {
        return headerLinesCountMin;
    }

    public int getHeaderLinesCountMax() {
        return headerLinesCountMax;
    }

    public void setHeaderLinesCount(int headerLinesCountMin, int headerLinesCountMax) {
        if (headerLinesCountMin > headerLinesCountMax) {
            throw new IllegalArgumentException();
        }
        this.headerLinesCountMin = headerLinesCountMin;
        this.headerLinesCountMax = headerLinesCountMax;
        if (this.headerLinesCountMax > 1) {
            headersStack = new FIFO<String[]>(this.headerLinesCountMax);
        }
    }

    public EntityCSVReciver<E> headerLinesCount(int headerLinesCountMin, int headerLinesCountMax) {
        setHeaderLinesCount(headerLinesCountMin, headerLinesCountMax);
        return this;
    }

    public int getHeadersMatchMinimum() {
        return headersMatchMinimum;
    }

    public void setHeadersMatchMinimum(int headersMatchMinimum) {
        this.headersMatchMinimum = headersMatchMinimum;
    }

    public EntityCSVReciver<E> headersMatchMinimum(int headersMatchMinimum) {
        setHeadersMatchMinimum(headersMatchMinimum);
        return this;
    }

    public boolean isHeaderIgnoreCase() {
        return headerIgnoreCase;
    }

    public void setHeaderIgnoreCase(boolean headerIgnoreCase) {
        this.headerIgnoreCase = headerIgnoreCase;
    }

    public EntityCSVReciver<E> headerIgnoreCase(boolean headerIgnoreCase) {
        setHeaderIgnoreCase(headerIgnoreCase);
        return this;
    }

    public boolean isMemberNamesAsHeaders() {
        return memberNamesAsHeaders;
    }

    public void setMemberNamesAsHeaders(boolean memberNamesAsHeaders) {
        this.memberNamesAsHeaders = memberNamesAsHeaders;
    }

    public EntityCSVReciver<E> memberNamesAsHeaders(boolean memberNamesAsHeaders) {
        setMemberNamesAsHeaders(memberNamesAsHeaders);
        return this;
    }

    public boolean isVerifyRequiredHeaders() {
        return verifyRequiredHeaders;
    }

    public void setVerifyRequiredHeaders(boolean verifyRequiredHeaders) {
        this.verifyRequiredHeaders = verifyRequiredHeaders;
    }

    public EntityCSVReciver<E> verifyRequiredHeaders(boolean verifyRequiredHeaders) {
        setVerifyRequiredHeaders(verifyRequiredHeaders);
        return this;
    }

    public boolean isVerifyRequiredValues() {
        return verifyRequiredValues;
    }

    public void setVerifyRequiredValues(boolean verifyRequiredValues) {
        this.verifyRequiredValues = verifyRequiredValues;
    }

    public EntityCSVReciver<E> verifyRequiredValues(boolean verifyRequiredValues) {
        setVerifyRequiredValues(verifyRequiredValues);
        return this;
    }

    public String getCollectionDelimiter() {
        return collectionDelimiter;
    }

    public void setCollectionDelimiter(String collectionDelimiter) {
        this.collectionDelimiter = collectionDelimiter;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    protected void buildMembersNames() {
        if (membersNames != null) {
            return;
        }
        membersNames = new HashMap<String, Path>();
        for (String memberName : entityMeta.getMemberNames()) {
            IObject<?> member = entityModel.getMember(memberName);
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
    }

    protected String[] columnNames(IObject<?> member) {
        ImportColumn importColumn = member.getMeta().getAnnotation(ImportColumn.class);
        if (importColumn == null) {
            return new String[] { member.getMeta().getCaptionNL(), member.getMeta().getCaption() };
        } else if (importColumn.ignore()) {
            return null;
        } else {
            return importColumn.names();
        }
    }

    private String[] combineHeader(String[] headers, int headerLinesCount) {
        if (headerLinesCount == 1) {
            return headers;
        } else {
            if (headersStack.size() < headerLinesCount) {
                return null;
            } else {
                List<String> headersCombined = new ArrayList<String>();
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

    protected String combineHeader(String headerfragmet1, String headerfragmet2) {
        return CommonsStringUtils.nvl_concat(headerfragmet1, headerfragmet2, " ");
    }

    protected String trimHeader(String header) {
        return header.replace((char) 0xA0, ' ').trim();
    }

    private String[] trimHeaders(String[] headers) {
        List<String> headersTrimmed = new ArrayList<String>();
        for (String header : headers) {
            headersTrimmed.add(trimHeader(header));
        }
        return headersTrimmed.toArray(new String[headersTrimmed.size()]);
    }

    @Override
    public boolean onHeader(String[] headers) {
        currentRow++;
        log.debug("headers {}", (Object) headers);
        headers = trimHeaders(headers);
        if (headersStack == null) {
            List<Path> paths = matchHeader(headers);
            if (matchAcceptable(headers, paths, true) > 0) {
                headersPath = paths;
                return true;
            }
        } else {
            headersStack.push(headers);
            // Find the longest header match
            TreeMap<Integer, List<Path>> matches = new TreeMap<Integer, List<Path>>();
            for (int headerLinesCount = headerLinesCountMin; headerLinesCount <= headerLinesCountMax; headerLinesCount++) {
                String[] headersCombined = combineHeader(headers, headerLinesCount);
                if (headersCombined != null) {
                    List<Path> paths = matchHeader(headersCombined);
                    int headersFound = matchAcceptable(headers, paths, false);
                    if (headersFound > 0) {
                        matches.put(headersFound, paths);
                    }
                }
            }
            if (matches.size() > 0) {
                List<Path> paths = matches.lastEntry().getValue();
                if (matchAcceptable(headers, paths, true) > 0) {
                    headersPath = paths;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHeaderFound() {
        return headersPath != null;
    }

    protected int matchAcceptable(String[] headers, List<Path> paths, boolean throwError) {
        int headersFound = 0;
        for (Path path : paths) {
            if (path != null) {
                headersFound++;
            }
        }
        if (getHeadersMatchMinimum() <= headersFound) {
            if (isVerifyRequiredHeaders()) {
                if (verifyHeaders(paths, throwError)) {
                    return headersFound;
                } else {
                    return 0;
                }
            } else {
                return headersFound;
            }
        } else {
            return 0;
        }
    }

    protected List<Path> matchHeader(String[] headers) {
        buildMembersNames();

        List<Path> paths = new ArrayList<Path>();
        paths.clear();
        nextHeader: for (String header : headers) {
            String name = header;
            if (isHeaderIgnoreCase()) {
                name = name.toLowerCase(Locale.ENGLISH);
            }
            Path path = membersNames.get(name);
            if (path != null) {
                if (paths.contains(path)) {
                    paths.add(null);
                } else {
                    paths.add(path);
                }
                continue nextHeader;
            }
            if (isMemberNamesAsHeaders()) {
                if (entityMeta.getMemberNames().contains(name)) {
                    paths.add(entityModel.getMember(header).getPath());
                    continue nextHeader;
                }
                if (header.contains(".")) {
                    IEntity ent = entityModel;
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
                    paths.add(member.getPath());
                } else {
                    if (!"".equals(header)) {
                        log.debug("Unknown header [{}]", header);
                    }
                    paths.add(null);
                }
            } else {
                if (!"".equals(header)) {
                    log.debug("Unknown header [{}]", header);
                }
                paths.add(null);
            }
        }
        return paths;
    }

    protected boolean verifyHeaders(List<Path> paths, boolean throwError) {
        for (String memberName : entityMeta.getMemberNames()) {
            IObject<?> member = entityModel.getMember(memberName);
            MemberMeta memberMeta = member.getMeta();
            if ((memberMeta.getObjectClassType() == ObjectClassType.Primitive) && (memberMeta.getAnnotation(NotNull.class) != null)) {
                ImportColumn importColumn = member.getMeta().getAnnotation(ImportColumn.class);
                if ((importColumn != null) && (importColumn.ignore())) {
                    continue;
                }
                if (!paths.contains(member.getPath())) {
                    if (throwError) {
                        throw new UserRuntimeException(i18n.tr("Missing required column header ''{0}'', row # {1}", memberMeta.getCaption(), getCurrentRow()));
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
                    IObject<?> member = entity.getMember(path);
                    switch (member.getMeta().getObjectClassType()) {
                    case Primitive:
                        @SuppressWarnings("rawtypes")
                        IPrimitive primitive = (IPrimitive<?>) member;
                        primitive.setValue(parsePrimitive(primitive, primitive.getValueClass(), value[i]));
                        break;
                    case PrimitiveSet:
                        @SuppressWarnings("rawtypes")
                        IPrimitiveSet primitiveSet = (IPrimitiveSet<?>) member;
                        primitiveSet.setCollectionValue(parsCollectionValue(primitiveSet, value[i]));
                        break;
                    default:
                        throw new Error("TODO implement this");
                    }
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

    protected Object parsePrimitive(IObject<?> member, Class<?> valueClass, String value) {
        if (valueClass.isAssignableFrom(byte[].class)) {
            return new Base64().decode(value);
        } else if (valueClass.isAssignableFrom(java.sql.Time.class)) {
            SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            try {
                return new java.sql.Time(tFormat.parse(value).getTime());
            } catch (ParseException e) {
                throw new Error("Error parsing time [" + value + "]", e);
            }
        } else if (LogicalDate.class.isAssignableFrom(valueClass)) {
            if ("".equals(value)) {
                return null;
            } else {
                return new LogicalDate(DateUtils.detectDateformat(value));
            }
        } else if (valueClass.isAssignableFrom(Date.class)) {
            if ("".equals(value)) {
                return null;
            } else {
                return DateUtils.detectDateformat(value);
            }
        } else if (Number.class.isAssignableFrom(valueClass)) {
            if ("".equals(value)) {
                return null;
            } else {
                // Is Local English?
                return PrimitiveHandler.parsString(valueClass, value.replaceAll(",", ""));
            }
        } else {
            return PrimitiveHandler.parsString(valueClass, value);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Collection<?> parsCollectionValue(IPrimitiveSet primitiveSet, String value) {
        Collection returnValue = new ArrayList();
        StringTokenizer st = new StringTokenizer(value, getCollectionDelimiter());
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            returnValue.add(parsePrimitive(primitiveSet, primitiveSet.getValueClass(), str));
        }
        return returnValue;
    }

    public List<E> getEntities() {
        return list;
    }

    public List<E> loadResourceFile(String resourceName) {
        DownloadFormat format = DownloadFormat.valueByExtension(FilenameUtils.getExtension(resourceName));
        switch (format) {
        case CSV:
            CSVLoad.loadResourceFile(resourceName, Charset.forName("Cp1252"), this);
            break;
        case XLSX:
        case XLS:
            XLSLoad.loadResourceFile(resourceName, format == DownloadFormat.XLSX, this);
            break;
        default:
            throw new Error("Unsupported format " + format);
        }
        return getEntities();
    }

    public List<E> loadFile(String fileName) {
        DownloadFormat format = DownloadFormat.valueByExtension(FilenameUtils.getExtension(fileName));
        InputStream is;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
        switch (format) {
        case CSV:
            CSVLoad.loadFile(is, Charset.forName("Cp1252"), new CSVParser(), this);
            break;
        case XLSX:
        case XLS:
            XLSLoad.loadFile(is, format == DownloadFormat.XLSX, this);
            break;
        default:
            throw new Error("Unsupported format " + format);
        }
        return getEntities();
    }
}

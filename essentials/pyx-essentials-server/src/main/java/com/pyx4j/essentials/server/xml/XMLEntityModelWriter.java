/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.server.report.XMLStringWriter;
import com.pyx4j.geo.GeoPoint;

public class XMLEntityModelWriter {

    protected final XMLStringWriter xml;

    private boolean emitId = true;

    private final XMLEntityName entityName;

    public XMLEntityModelWriter(XMLStringWriter xml) {
        this(xml, new XMLEntityNameDefault());
    }

    public XMLEntityModelWriter(XMLStringWriter xml, XMLEntityName entityName) {
        this.xml = xml;
        this.entityName = entityName;
    }

    public void writeRoot(IEntity entity, Map<String, String> attributes) {
        write(entity, entityName.getXMLName(entity.getObjectClass()), attributes, null, new HashSet<IEntity>());
    }

    public void write(IEntity entity) {
        write(entity, entityName.getXMLName(entity.getObjectClass()));
    }

    public void write(IEntity entity, String name) {
        write(entity, name, null, null, new HashSet<IEntity>());
    }

    private void write(IEntity entity, String name, Map<String, String> attributes, @SuppressWarnings("rawtypes") Class<? extends IObject> declaredObjectClass,
            Set<IEntity> processed) {
        Map<String, String> entityAttributes = new LinkedHashMap<String, String>();
        if (attributes != null) {
            entityAttributes.putAll(attributes);
        }
        if (isEmitId() && (entity.getPrimaryKey() != null)) {
            entityAttributes.put("id", String.valueOf(entity.getPrimaryKey()));
        }
        if ((declaredObjectClass != null) && (!entity.getObjectClass().equals(declaredObjectClass))) {
            String typeName = entityName.getXMLName(entity.getObjectClass());
            if (!typeName.equals(name)) {
                entityAttributes.put("type", typeName);
            }
        }
        xml.startIdented(name, entityAttributes);

        if (processed.contains(entity)) {
            xml.endIdented(name);
            return;
        }
        processed.add(entity);

        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity member = (IEntity) entity.getMember(memberName);
                if (!member.isObjectClassSameAsDef()) {
                    member = member.cast();
                }
                write(member, memberName, null, memberMeta.getObjectClass(), processed);
                break;
            case EntitySet:
            case EntityList:
                xml.startIdented(memberName);
                ICollection<?, ?> cm = (ICollection<?, ?>) entity.getMember(memberName);
                for (int i = 0; i < 2; i++) {
                    IEntity item = cm.$();
                    write(item, entityName.getXMLName(item.getObjectClass()), null, memberMeta.getObjectClass(), processed);
                }
                xml.endIdented(memberName);
                break;
            case PrimitiveSet:
                if (!((Collection<?>) entity.getMemberValue(memberName)).isEmpty()) {
                    xml.startIdented(memberName);
                    for (Object item : (Collection<?>) entity.getMemberValue(memberName)) {
                        xml.write("item", getValueAsString(item));
                    }
                    xml.endIdented(memberName);
                }
                break;
            case Primitive:
                if (!memberName.equals(IEntity.PRIMARY_KEY)) {
                    if (GeoPoint.class == memberMeta.getValueClass()) {
                        xml.startIdented(memberName);
                        xml.write("lat", getValueClassAsString(double.class));
                        xml.write("lng", getValueClassAsString(double.class));
                        xml.endIdented(memberName);
                    } else {
                        xml.write(memberName, getValueClassAsString(memberMeta.getValueClass()));
                    }
                }
                break;
            }
        }
        xml.endIdented(name);
    }

    private static <T extends Enum<T>> String allEnums(Class<T> enumClass) {
        StringBuilder b = new StringBuilder();
        for (T value : EnumSet.allOf(enumClass)) {
            if (b.length() > 0) {
                b.append(";");
            }
            b.append(value.name());
        }
        return b.toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected String getValueClassAsString(Class<?> valueClass) {
        if (valueClass.isEnum()) {
            return allEnums((Class<Enum>) valueClass);
        } else {
            return valueClass.getSimpleName();
        }
    }

    protected String getValueAsString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        } else if (value instanceof byte[]) {
            return new Base64().encodeToString((byte[]) value);
        } else if (value instanceof java.sql.Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format((Date) value);
        } else if (value instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format((Date) value);
        } else {
            return value.toString();
        }
    }

    public boolean isEmitId() {
        return emitId;
    }

    public void setEmitId(boolean emitId) {
        this.emitId = emitId;
    }
}

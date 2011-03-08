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
 * Created on 2011-03-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.server.report.XMLStringWriter;

public class XMLEntityWriter {

    protected final XMLStringWriter xml;

    public XMLEntityWriter(XMLStringWriter xml) {
        this.xml = xml;
    }

    public void write(IEntity entity, String name) {
        write(entity, name, null, new HashSet<Map<String, Object>>());
    }

    private void write(IEntity entity, String name, @SuppressWarnings("rawtypes") Class<? extends IObject> declaredObjectClass,
            Set<Map<String, Object>> processed) {
        Map<String, String> entityAttributes = new LinkedHashMap<String, String>();
        if (entity.getPrimaryKey() != null) {
            entityAttributes.put("id", String.valueOf(entity.getPrimaryKey()));
        }
        if ((declaredObjectClass != null) && (!entity.getObjectClass().equals(declaredObjectClass))) {
            entityAttributes.put("type", entity.getObjectClass().getName());
        }
        xml.startIdented(name, entityAttributes);

        Map<String, Object> entityValue = entity.getValue();
        if (processed.contains(entityValue)) {
            xml.endIdented(name);
            return;
        }

        processed.add(entityValue);

        nextValue: for (Map.Entry<String, Object> me : entityValue.entrySet()) {
            String propertyName = me.getKey();
            if (propertyName.equals(IEntity.PRIMARY_KEY) || propertyName.equals(IEntity.CONCRETE_TYPE_DATA_ATTR)) {
                continue nextValue;
            }
            Object value = me.getValue();

            if (value instanceof Map<?, ?>) {
                IEntity member = (IEntity) entity.getMember(propertyName);
                if (!member.isObjectClassSameAsDef()) {
                    member = member.cast();
                }
                write(member, propertyName, entity.getEntityMeta().getMemberMeta(propertyName).getObjectClass(), processed);
            } else if (value instanceof Collection) {
                xml.startIdented(propertyName);
                IObject<?> member = entity.getMember(propertyName);
                if (member instanceof ICollection<?, ?>) {
                    for (Object item : (ICollection<?, ?>) member) {
                        write((IEntity) item, "item", entity.getEntityMeta().getMemberMeta(propertyName).getObjectClass(), processed);
                    }
                } else {
                    for (Object item : (Collection<?>) value) {
                        xml.write("item", getValueAsString(item));
                    }
                }
                xml.endIdented(propertyName);
            } else {
                xml.write(propertyName, getValueAsString(value));
            }
        }

        xml.endIdented(name);
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

}

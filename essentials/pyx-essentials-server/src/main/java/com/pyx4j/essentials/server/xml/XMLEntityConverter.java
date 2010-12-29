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
 * Created on 2010-12-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.rpc.admin.BackupKey;
import com.pyx4j.essentials.server.report.XMLStringWriter;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.DateUtils;

public class XMLEntityConverter {

    public static String getValueType(Object value) {
        if ((value == null) || (value instanceof String)) {
            return null;
        } else if (value instanceof byte[]) {
            return "byte[]";
        } else if (value instanceof GeoPoint) {
            return GeoPoint.class.getSimpleName();
        } else {
            String name = value.getClass().getName();
            if ((name.startsWith("java.lang.")) || (name.startsWith("java.util."))) {
                name = value.getClass().getSimpleName();
            }
            return name;
        }
    }

    public static String getValueAsString(Object value) {
        if (value == null) {
            return null;
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

    public static Serializable valueOf(String s, String typeAttribute) {
        if ((s == null) || (s.length() == 0)) {
            return null;
        } else if ((typeAttribute == null) || (typeAttribute.length() == 0) || (typeAttribute.equals(String.class.getSimpleName()))) {
            return s;
        } else if (typeAttribute.equals("Key")) {
            return BackupKey.valueOf(s);
        } else if (typeAttribute.equals(Long.class.getSimpleName())) {
            return Long.valueOf(s);
        } else if (typeAttribute.equals(Boolean.class.getSimpleName())) {
            return Boolean.valueOf(s);
        } else if (typeAttribute.equals(Date.class.getSimpleName())) {
            return DateUtils.detectDateformat(s);
        } else if (typeAttribute.equals(Vector.class.getSimpleName()) || (typeAttribute.equals(List.class.getSimpleName()))) {
            return new Vector<Serializable>();
        } else if (typeAttribute.equals(GeoPoint.class.getSimpleName())) {
            return GeoPoint.valueOf(s);
        } else if (typeAttribute.equals("byte[]")) {
            return new Base64().decode(s);
        } else if (typeAttribute.equals(Integer.class.getSimpleName())) {
            return Integer.valueOf(s);
        } else if (typeAttribute.equals(Double.class.getSimpleName())) {
            return Double.valueOf(s);
        } else if (typeAttribute.equals(Float.class.getSimpleName())) {
            return Float.valueOf(s);
        } else if (typeAttribute.equals(Short.class.getSimpleName())) {
            return Short.valueOf(s);
        } else if (typeAttribute.equals(Byte.class.getSimpleName())) {
            return Byte.valueOf(s);
        } else {
            throw new RuntimeException("Unsupported data type [" + typeAttribute + "]");
        }
    }

    public static void write(XMLStringWriter xml, IEntity entity) {
        write(xml, entity, entity.getEntityMeta().getEntityClass().getName());
    }

    public static void write(XMLStringWriter xml, IEntity entity, String name) {
        Map<String, String> entityAttributes = new LinkedHashMap<String, String>();
        if (entity.getPrimaryKey() != null) {
            entityAttributes.put("id", String.valueOf(entity.getPrimaryKey()));
        }
        xml.startIdented(name, entityAttributes);

        nextValue: for (Map.Entry<String, Object> me : entity.getValue().entrySet()) {
            String propertyName = me.getKey();
            if (propertyName.equals(IEntity.PRIMARY_KEY)) {
                continue nextValue;
            }
            Object value = me.getValue();

            Map<String, String> attributes = new LinkedHashMap<String, String>();
            attributes.put("type", getValueType(value));

            if (value instanceof Map<?, ?>) {
                XMLEntityConverter.write(xml, (IEntity) entity.getMember(propertyName), propertyName);
            } else if (value instanceof Collection) {
                xml.startIdented(propertyName, attributes);
                for (Object item : (Collection<?>) value) {
                    Map<String, String> itemAttributes = new HashMap<String, String>();
                    itemAttributes.put("type", getValueType(item));
                    xml.write("item", itemAttributes, getValueAsString(item));
                }
                xml.endIdented(propertyName);
            } else {
                xml.write(propertyName, attributes, getValueAsString(value));
            }
        }

        xml.endIdented(name);
    }

    public static <T extends IEntity> T pars(Element node) {
        String entityClassName = node.getNodeName();
        Class<T> entityClass = ServerEntityFactory.entityClass(entityClassName);
        return pars(node, entityClass);
    }

    public static <T extends IEntity> T pars(Element node, Class<T> entityClass) {
        T entity = EntityFactory.create(entityClass);
        return pars(node, entity);
    }

    public static <T extends IEntity> T pars(Element node, T entity) {
        String id = node.getAttribute("id");
        if (CommonsStringUtils.isStringSet(id)) {
            entity.setPrimaryKey(Long.valueOf(id));
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node valueNode = nodeList.item(i);
            if (valueNode instanceof Element) {
                String memberName = valueNode.getNodeName();
                MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
                if (memberMeta.isEntity()) {
                    pars((Element) valueNode, (IEntity) entity.getMember(memberName));
                } else {
                    entity.setMemberValue(memberName, parsValueNode((Element) valueNode, memberMeta));
                }
            }
        }

        return entity;
    }

    private static Serializable parsValueNode(Element valueNode, MemberMeta memberMeta) {
        String typeAttr = valueNode.getAttribute("type");
        if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
            return valueOf(valueNode.getTextContent(), typeAttr);
        } else {
            throw new Error("Not yet implemented");
        }
    }
}

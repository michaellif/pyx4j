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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.impl.PrimitiveHandler;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.server.report.XMLStringWriter;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.DateUtils;

public class XMLEntityConverter {

    private static String getValueAsString(Object value) {
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
            if (propertyName.equals(IEntity.PRIMARY_KEY) || propertyName.equals(IEntity.CONCRETE_TYPE_DATA_ATTR)) {
                continue nextValue;
            }
            Object value = me.getValue();

            if (value instanceof Map<?, ?>) {
                XMLEntityConverter.write(xml, (IEntity) entity.getMember(propertyName), propertyName);
            } else if (value instanceof Collection) {
                xml.startIdented(propertyName);
                IObject<?> member = entity.getMember(propertyName);
                if (member instanceof ICollection<?, ?>) {
                    for (Object item : (ICollection<?, ?>) member) {
                        XMLEntityConverter.write(xml, (IEntity) item, "item");
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

    public static <T extends IEntity> T pars(Element node) {
        String entityClassName = node.getNodeName();
        Class<T> entityClass = ServerEntityFactory.entityClass(entityClassName);
        return pars(node, entityClass);
    }

    public static <T extends IEntity> T pars(Element node, Class<T> entityClass) {
        T entity = EntityFactory.create(entityClass);
        return pars(node, entity);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
                IObject<?> member = entity.getMember(memberName);
                MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
                if (memberMeta.isEntity()) {
                    pars((Element) valueNode, (IEntity) member);
                } else if (member instanceof ICollection<?, ?>) {
                    NodeList collectionNodeList = valueNode.getChildNodes();
                    for (int ci = 0; ci < collectionNodeList.getLength(); ci++) {
                        Node itemNode = collectionNodeList.item(ci);
                        if ((itemNode instanceof Element) && ("item".equals(itemNode.getNodeName()))) {
                            IEntity item = ((ICollection<?, ?>) member).$();
                            pars((Element) itemNode, item);
                            ((ICollection) member).add(item);
                        }
                    }
                } else if (member instanceof IPrimitiveSet<?>) {
                    NodeList collectionNodeList = valueNode.getChildNodes();
                    for (int ci = 0; ci < collectionNodeList.getLength(); ci++) {
                        Node itemNode = collectionNodeList.item(ci);
                        if ((itemNode instanceof Element) && ("item".equals(itemNode.getNodeName()))) {
                            ((IPrimitiveSet) member).add(parsPrimitive((Element) itemNode, memberMeta.getValueClass()));
                        }
                    }
                } else {
                    entity.setMemberValue(memberName, parsValueNode((Element) valueNode, memberMeta, member));
                }
            }
        }

        return entity;
    }

    private static Object parsPrimitive(Element valueNode, Class<?> valueClass) {
        String str = valueNode.getTextContent();
        if (valueClass.isAssignableFrom(byte[].class)) {
            return new Base64().decode(str);
        } else if (valueClass.isAssignableFrom(Date.class)) {
            return DateUtils.detectDateformat(str);
        } else if (valueClass.equals(GeoPoint.class)) {
            return GeoPoint.valueOf(str);
        } else {
            return PrimitiveHandler.parsString(valueClass, str);
        }
    }

    private static Object parsValueNode(Element valueNode, MemberMeta memberMeta, IObject<?> member) {
        String str = valueNode.getTextContent();
        if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
            Class<?> valueClass = member.getValueClass();
            if (valueClass.isAssignableFrom(byte[].class)) {
                return new Base64().decode(str);
            } else if (valueClass.isAssignableFrom(Date.class)) {
                return DateUtils.detectDateformat(str);
            } else if (valueClass.equals(GeoPoint.class)) {
                return GeoPoint.valueOf(str);
            } else {
                return ((IPrimitive<?>) member).pars(str);
            }
        } else {
            throw new Error("Pars of " + member.getFieldName() + " (" + memberMeta.getValueClass() + ") '" + str + "' Not yet implemented");
        }
    }
}

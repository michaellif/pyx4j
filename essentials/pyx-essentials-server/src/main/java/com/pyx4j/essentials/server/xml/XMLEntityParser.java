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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.impl.PrimitiveHandler;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.DateUtils;

public class XMLEntityParser {

    private final static Logger log = LoggerFactory.getLogger(XMLEntityParser.class);

    private final XMLEntityFactory factory;

    private static class EntityRefferences {

        Map<String, IEntity> map = new HashMap<String, IEntity>();

        public IEntity get(Class<?> entityClass, String id) {
            return map.get(entityClass.getName() + "-" + id);
        }

        public void put(IEntity entity, String id) {
            map.put(entity.getObjectClass().getName() + "-" + id, entity);
        }

        public void remove(Class<?> entityClass, String id) {
            map.remove(entityClass.getName() + "-" + id);
        }

        @Override
        public String toString() {
            return map.keySet().toString();
        }

        public int size() {
            return map.size();
        }
    }

    private final EntityRefferences processed = new EntityRefferences();

    private final EntityRefferences references = new EntityRefferences();

    public XMLEntityParser() {
        this(new XMLEntityFactoryDefault());
    }

    public XMLEntityParser(XMLEntityFactory factory) {
        this.factory = factory;
    }

    public IEntity parse(Element node) {
        return parse((Class<IEntity>) null, node);
    }

    public <T extends IEntity> T parse(Class<T> entityClass, Element node) {
        T r = createAndParse(entityClass, node);
        if (references.size() > 0) {
            throw new Error("Unresolved references detected " + references);
        }
        return r;
    }

    private <T extends IEntity> T createAndParse(Class<T> entityClass, Element node) {
        String xmlName = node.getAttribute("type");
        if (!CommonsStringUtils.isStringSet(xmlName)) {
            xmlName = node.getNodeName();
        }
        T entity = factory.createInstance(xmlName, entityClass);
        return parse(entity, node);
    }

    private <T extends IEntity> T createInstance(Class<T> objectClass, Element node) {
        String xmlName = node.getAttribute("type");
        if (!CommonsStringUtils.isStringSet(xmlName)) {
            return null;
        } else {
            return factory.createInstance(xmlName, objectClass);
        }
    }

    protected void onEntityParsed(IEntity entity) {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T extends IEntity> T parse(T entity, Element node) {
        {
            String reference = node.getAttribute("reference");
            if (CommonsStringUtils.isStringSet(reference)) {
                T exists = (T) processed.get(entity.getObjectClass(), reference);
                if (exists != null) {
                    if (entity.getObjectClass() != exists.getObjectClass()) {
                        throw new Error("Type corruption " + entity.getObjectClass() + "!=" + exists.getObjectClass());
                    }
                    entity.set(exists);
                    return entity;
                } else {
                    exists = (T) references.get(entity.getObjectClass(), reference);
                    if (exists != null) {
                        if (entity.getObjectClass() != exists.getObjectClass()) {
                            throw new Error("Type corruption " + entity.getObjectClass() + "!=" + exists.getObjectClass());
                        }
                        entity.setValue(exists.getValue());
                        return entity;
                    } else {
                        entity.setPrimaryKey(new Key(reference));
                        references.put(entity, reference);
                    }
                }
                return entity;
            }
        }

        {
            String id = node.getAttribute("id");
            if (CommonsStringUtils.isStringSet(id)) {
                T exists = (T) processed.get(entity.getObjectClass(), id);
                if (exists != null) {
                    if (entity.getObjectClass() != exists.getObjectClass()) {
                        throw new Error("Type corruption " + entity.getObjectClass() + "!=" + exists.getObjectClass());
                    }
                    entity.set(exists);
                    return entity;
                }

                // Merge references data if reading complete entity
                exists = (T) references.get(entity.getObjectClass(), id);
                if (exists != null) {
                    if (entity.getObjectClass() != exists.getObjectClass()) {
                        throw new Error("Type corruption " + entity.getObjectClass() + "!=" + exists.getObjectClass());
                    }
                    entity.setValue(exists.getValue());
                    references.remove(entity.getObjectClass(), id);
                } else {
                    entity.setPrimaryKey(new Key(id));
                }
                processed.put(entity, id);
            }
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node valueNode = nodeList.item(i);
            if (valueNode instanceof Element) {
                String memberName = valueNode.getNodeName();
                IObject<?> member = entity.getMember(memberName);
                MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
                if (memberMeta.getAnnotation(XmlTransient.class) != null) {
                    log.warn("ignore XmlTransient element {}", memberName);
                    continue;
                }
                if (memberMeta.isEntity()) {
                    IEntity concreteInctance = createInstance((Class<IEntity>) memberMeta.getObjectClass(), (Element) valueNode);
                    if (concreteInctance == null) {
                        parse((IEntity) member, (Element) valueNode);
                    } else {
                        if (concreteInctance.getEntityMeta().getAnnotation(XmlTransient.class) != null) {
                            log.warn("ignore XmlTransient entoty {}", concreteInctance.getObjectClass().getName());
                            continue;
                        }
                        parse(concreteInctance, (Element) valueNode);
                        ((IEntity) member).set(concreteInctance);
                    }
                } else if (member instanceof ICollection<?, ?>) {
                    NodeList collectionNodeList = valueNode.getChildNodes();
                    for (int ci = 0; ci < collectionNodeList.getLength(); ci++) {
                        Node itemNode = collectionNodeList.item(ci);
                        if (itemNode instanceof Element) {
                            IEntity item = createAndParse(((ICollection<?, ?>) member).getValueClass(), (Element) itemNode);
                            ((ICollection) member).add(item);
                        }
                    }
                } else if (member instanceof IPrimitiveSet<?>) {
                    NodeList collectionNodeList = valueNode.getChildNodes();
                    for (int ci = 0; ci < collectionNodeList.getLength(); ci++) {
                        Node itemNode = collectionNodeList.item(ci);
                        if ((itemNode instanceof Element) && ("item".equals(itemNode.getNodeName()))) {
                            ((IPrimitiveSet) member).add(parsePrimitive((Element) itemNode, memberMeta.getValueClass(), memberMeta, member));
                        }
                    }
                } else {
                    entity.setMemberValue(memberName, parseValueNode((Element) valueNode, memberMeta, member));
                }
            }
        }
        onEntityParsed(entity);
        return entity;
    }

    private Object parsePrimitive(Element valueNode, Class<?> valueClass, MemberMeta memberMeta, IObject<?> member) {
        String str = valueNode.getTextContent();
        if (valueClass.isAssignableFrom(byte[].class)) {
            return new Base64().decode(str);
        } else if (valueClass.isAssignableFrom(java.sql.Time.class)) {
            SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            tFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                return new java.sql.Time(tFormat.parse(str).getTime());
            } catch (ParseException e) {
                throw new Error("Error parsing time [" + str + "]", e);
            }
        } else if (valueClass.isAssignableFrom(Date.class)) {
            return DateUtils.detectDateformat(str);
        } else if (valueClass.equals(GeoPoint.class)) {
            return parsGeoPoint(valueNode, memberMeta, member);
        } else {
            return PrimitiveHandler.parsString(valueClass, str);
        }
    }

    private GeoPoint parsGeoPoint(Element node, MemberMeta memberMeta, IObject<?> member) {
        Double lat = null;
        Double lng = null;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node valueNode = nodeList.item(i);
            if (valueNode instanceof Element) {
                String name = valueNode.getNodeName();
                if ("lat".equals(name)) {
                    lat = Double.valueOf(valueNode.getTextContent());
                } else if ("lng".equals(name)) {
                    lng = Double.valueOf(valueNode.getTextContent());
                }
            }
        }
        if ((lat != null) && (lng != null)) {
            return new GeoPoint(lat, lng);
        } else {
            throw new Error("Error parsing " + member.getFieldName() + " (" + memberMeta.getValueClass() + ") '");
        }
    }

    private Object parseValueNode(Element valueNode, MemberMeta memberMeta, IObject<?> member) {
        String str = valueNode.getTextContent();
        if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
            if (CommonsStringUtils.isEmpty(str)) {
                return null;
            }
            Class<?> valueClass = member.getValueClass();
            if (valueClass.isAssignableFrom(byte[].class)) {
                return new Base64().decode(str);
            } else if (Date.class.isAssignableFrom(valueClass)) {
                if (valueClass.isAssignableFrom(java.sql.Time.class)) {
                    SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                    //TODO
                    //tFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        return new java.sql.Time(tFormat.parse(str).getTime());
                    } catch (ParseException e) {
                        throw new Error("Error parsing time [" + str + "]", e);
                    }
                } else {
                    if (valueClass.equals(java.sql.Date.class) || valueClass.equals(LogicalDate.class)) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        //TODO
                        //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date;
                        try {
                            date = df.parse(str);
                        } catch (ParseException e) {
                            date = DateUtils.detectDateformat(str);
                        }
                        if (valueClass.equals(java.sql.Date.class)) {
                            return new java.sql.Date(date.getTime());
                        } else {
                            return new LogicalDate(date.getTime());
                        }
                    } else {
                        return DateUtils.detectDateformat(str);
                    }
                }
            } else if (valueClass.equals(GeoPoint.class)) {
                return parsGeoPoint(valueNode, memberMeta, member);
            } else {
                return ((IPrimitive<?>) member).parse(str);
            }
        } else {
            throw new Error("Pars of " + member.getFieldName() + " (" + memberMeta.getValueClass() + ") '" + str + "' Not yet implemented");
        }
    }
}

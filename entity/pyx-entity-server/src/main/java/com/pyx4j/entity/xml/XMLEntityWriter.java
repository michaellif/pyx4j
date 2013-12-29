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
package com.pyx4j.entity.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.codec.binary.Base64;

import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.geo.GeoPoint;

public class XMLEntityWriter {

    protected final XMLStringWriter xml;

    private boolean emitId = true;

    private boolean emitIdentityHashCode = false;

    private boolean emitOnlyOwnedReferences = false;

    private boolean emitAttachLevel = false;

    private boolean emitXmlTransient = false;

    private boolean emitLogTransient = true;

    private final XMLEntityNamingConvention namingConvention;

    private final Map<String, String> detachedEntityAttributes = new LinkedHashMap<String, String>();

    private static class GlobalGraph {

        Set<IEntity> processed = new HashSet<IEntity>();

        Set<IEntity> references = new HashSet<IEntity>();

        boolean isEmitted(IEntity entity) {
            return processed.contains(entity);
        }

        void emitting(IEntity entity) {
            processed.add(entity);
            references.remove(entity);
        }
    }

    private static class VerticalGraph {

        GlobalGraph global;

        Set<IEntity> processedVerticaly = new HashSet<IEntity>();

        VerticalGraph() {
            global = new GlobalGraph();
        }

        VerticalGraph(VerticalGraph previous) {
            global = previous.global;
            processedVerticaly.addAll(previous.processedVerticaly);
        }

        boolean isEmitted(IEntity entity) {
            return processedVerticaly.contains(entity) || global.isEmitted(entity);
        }

        boolean isEmittedVerticaly(IEntity entity) {
            return processedVerticaly.contains(entity);
        }

        void emitting(IEntity entity) {
            processedVerticaly.add(entity);
            global.emitting(entity);
        }

        Set<IEntity> getReferences() {
            return global.references;
        }

        void addReference(IEntity entity) {
            global.references.add(entity);
        }
    }

    public XMLEntityWriter(XMLStringWriter xml) {
        this(xml, new XMLEntityNamingConventionDefault());
    }

    public XMLEntityWriter(XMLStringWriter xml, XMLEntityNamingConvention namingConvention) {
        this.xml = xml;
        this.namingConvention = namingConvention;
        detachedEntityAttributes.put("attachLevel", AttachLevel.Detached.name());
    }

    public boolean isEmitId() {
        return emitId;
    }

    public void setEmitId(boolean emitId) {
        this.emitId = emitId;
    }

    public boolean isEmitIdentityHashCode() {
        return emitIdentityHashCode;
    }

    public void setEmitIdentityHashCode(boolean emitIdentityHashCode) {
        this.emitIdentityHashCode = emitIdentityHashCode;
    }

    public void setEmitOnlyOwnedReferences(boolean emitOnlyOwnedReferences) {
        this.emitOnlyOwnedReferences = emitOnlyOwnedReferences;
    }

    public boolean isEmitOnlyOwnedReferences() {
        return emitOnlyOwnedReferences;
    }

    public boolean isEmitAttachLevel() {
        return emitAttachLevel;
    }

    public void setEmitAttachLevel(boolean emitAttachLevel) {
        this.emitAttachLevel = emitAttachLevel;
    }

    public boolean isEmitXmlTransient() {
        return emitXmlTransient;
    }

    public void setEmitXmlTransient(boolean emitXmlTransient) {
        this.emitXmlTransient = emitXmlTransient;
    }

    public boolean isEmitLogTransient() {
        return emitLogTransient;
    }

    public void setEmitLogTransient(boolean emitLogTransient) {
        this.emitLogTransient = emitLogTransient;
    }

    public void writeRoot(IEntity entity, Map<String, String> attributes) {
        VerticalGraph grapth = new VerticalGraph();
        write(entity, namingConvention.getXMLName(entity.getObjectClass()), attributes, null, grapth);
        if (isEmitOnlyOwnedReferences() && grapth.getReferences().size() > 0) {
            throw new Error("UnOwnedReferences references detected " + grapth.getReferences());
        }
    }

    public void write(IEntity entity) {
        write(entity, namingConvention.getXMLName(entity.getObjectClass()));
    }

    public void write(IEntity entity, String name) {
        write(entity, name, null, null, new VerticalGraph());
    }

    private void write(IEntity entity, String name, Map<String, String> attributes, @SuppressWarnings("rawtypes") Class<? extends IObject> declaredObjectClass,
            VerticalGraph graph) {
        if (!isEmitXmlTransient() && entity.getEntityMeta().getAnnotation(XmlTransient.class) != null) {
            return;
        }
        if (!isEmitLogTransient()) {
            if (entity.getEntityMeta().getAnnotation(LogTransient.class) != null) {
                return;
            }
        }
        Map<String, String> entityAttributes = new LinkedHashMap<String, String>();
        if (attributes != null) {
            entityAttributes.putAll(attributes);
        }

        boolean emitted = graph.isEmitted(entity);

        if (isEmitId() && (entity.getPrimaryKey() != null)) {
            if (emitted) {
                entityAttributes.put("reference", String.valueOf(entity.getPrimaryKey()));
            } else {
                if (isEmitOnlyOwnedReferences() && (entity.getOwner() != null) && !entity.getMeta().isOwnedRelationships()) {
                    entityAttributes.put("reference", String.valueOf(entity.getPrimaryKey()));
                    graph.addReference(entity);
                    emitted = true;
                } else {
                    entityAttributes.put("id", String.valueOf(entity.getPrimaryKey()));
                }
            }
        } else if (emitted && graph.isEmittedVerticaly(entity)) {
            // Avoid cyclic references even if not writing id
            if (isEmitIdentityHashCode()) {
                entityAttributes.put("identity", Integer.toHexString(System.identityHashCode(entity.getValue())));
            }
            return;
        }
        if (!isEmitId() && entity.isValueDetached()) {
            throw new Error("Writing detached entity " + entity.getDebugExceptionInfoString());
        }
        if (isEmitIdentityHashCode()) {
            entityAttributes.put("identity", Integer.toHexString(System.identityHashCode(entity.getValue())));
        }
        if ((declaredObjectClass != null) && (!entity.getObjectClass().equals(declaredObjectClass))) {
            String typeName = namingConvention.getXMLName(entity.getObjectClass());
            if (!typeName.equals(name)) {
                entityAttributes.put("type", typeName);
            }
        }

        AttachLevel attachLevel = entity.getAttachLevel();
        if (isEmitAttachLevel()) {
            if (attachLevel != AttachLevel.Attached) {
                entityAttributes.put("attachLevel", attachLevel.name());
            }
        }

        if (emitted || (attachLevel != AttachLevel.Attached)) {
            if (attachLevel == AttachLevel.ToStringMembers) {
                xml.write(name, entityAttributes, entity.getStringView());
            } else {
                xml.writeEmpty(name, entityAttributes);
            }
            return;
        }
        graph.emitting(entity);

        xml.startIdented(name, entityAttributes);

        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (!isEmitXmlTransient() && memberMeta.getAnnotation(XmlTransient.class) != null) {
                continue;
            }
            IObject<?> member = entity.getMember(memberName);
            if (member.getAttachLevel() == AttachLevel.Detached) {
                if (isEmitAttachLevel()) {
                    xml.writeEmpty(memberName, detachedEntityAttributes);
                }
                continue;
            }

            if (!emitMember(entity, memberName, member)) {
                continue;
            }
            if (!isEmitLogTransient()) {
                if (memberMeta.isLogTransient()) {
                    xml.write(memberName, "****");
                    continue;
                }
            }
            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity entityMember = (IEntity) member;
                if (!entityMember.isObjectClassSameAsDef()) {
                    entityMember = entityMember.cast();
                }
                write(entityMember, memberName, null, memberMeta.getObjectClass(), new VerticalGraph(graph));
                break;
            case EntitySet:
            case EntityList:
                if (member.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
                    if (isEmitAttachLevel()) {
                        Map<String, String> collectionAttributes = new LinkedHashMap<String, String>();
                        collectionAttributes.put("attachLevel", AttachLevel.CollectionSizeOnly.name());
                        collectionAttributes.put("size", String.valueOf(((ICollection<?, ?>) member).size()));
                        xml.writeEmpty(memberName, collectionAttributes);
                    }
                    continue;
                }

                if (!((ICollection<?, ?>) member).isEmpty()) {
                    xml.startIdented(memberName);
                    for (Object item : (ICollection<?, ?>) member) {
                        write((IEntity) item, namingConvention.getXMLName(((IEntity) item).getObjectClass()), null, memberMeta.getObjectClass(),
                                new VerticalGraph(graph));
                    }
                    xml.endIdented();
                }
                break;
            case PrimitiveSet:
                if (!((Collection<?>) entity.getMemberValue(memberName)).isEmpty()) {
                    xml.startIdented(memberName);
                    for (Object item : (Collection<?>) entity.getMemberValue(memberName)) {
                        xml.write("item", getValueAsString(item));
                    }
                    xml.endIdented();
                }
                break;
            case Primitive:
                if (!memberName.equals(IEntity.PRIMARY_KEY)) {
                    if (GeoPoint.class == memberMeta.getValueClass()) {
                        xml.startIdented(memberName);
                        xml.write("lat", getValueAsString(((GeoPoint) entity.getMemberValue(memberName)).getLat()));
                        xml.write("lng", getValueAsString(((GeoPoint) entity.getMemberValue(memberName)).getLng()));
                        xml.endIdented();
                    } else {
                        xml.write(memberName, getValueAsString(entity.getMemberValue(memberName)));
                    }
                }
                break;
            }
        }
        xml.endIdented();
    }

    protected boolean emitMember(IEntity entity, String memberName, IObject<?> member) {
        if (member.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
            return true;
        } else {
            return !member.isNull();
        }
    }

    protected String getValueAsString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        } else if (value instanceof byte[]) {
            return new Base64().encodeToString((byte[]) value);
        } else if (value instanceof java.sql.Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format((Date) value);
        } else if (value instanceof java.sql.Time) {
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            return df.format((Date) value);
        } else if (value instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format((Date) value);
        } else {
            return value.toString();
        }
    }

}

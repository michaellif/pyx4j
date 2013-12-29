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
 * Created on Sep 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.server.services.customization;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.xml.XMLEntityNamingConventionDefault;
import com.pyx4j.entity.xml.XMLEntityParser;
import com.pyx4j.entity.xml.XMLEntityWriter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.site.rpc.customization.CustomizationOverwriteAttemptException;
import com.pyx4j.site.shared.domain.cusomization.CustomizationHolder;

public class CustomizationPersistenceHelper<E extends IEntity> {

    private final Class<? extends CustomizationHolder> customizationHolderEntityClass;

    private final Class<E> baseClass;

    public <H extends CustomizationHolder> CustomizationPersistenceHelper(Class<H> customizationHolderEntityClass) {
        this(customizationHolderEntityClass, null);
    }

    public <H extends CustomizationHolder> CustomizationPersistenceHelper(Class<H> customizationHolderEntityClass, Class<E> baseClass) {
        this.customizationHolderEntityClass = customizationHolderEntityClass;
        this.baseClass = baseClass;
    }

    public Iterable<String> list(E proto) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        if (baseClass != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().baseClass(), baseClass.getSimpleName()));
        }
        if (proto != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getSimpleName()));
        }

        Iterator<? extends CustomizationHolder> i = Persistence.service().query(null, criteria, AttachLevel.ToStringMembers);

        Vector<String> result = new Vector<String>();
        while (i.hasNext()) {
            result.add(i.next().getStringView());
        }
        return result;
    }

    /**
     * Overloaded version of {@link #save(String, IEntity, boolean, boolean)} with <code>allowOverwriteReadonlyMembers</code> set to false
     */
    public void save(String id, E entity, boolean allowOverwriteEntity) {
        save(id, entity, allowOverwriteEntity, false);
    }

    /**
     * @param id
     * @param entity
     * @param allowOverwriteEntity
     *            defines whether we can save an entity with the same id (doesn't have anything to do with {@link ReadOnly})
     * @param allowOverwriteReadonlyMembers
     *            defines if members marked with {@link ReadOnly} can be overridden during save
     */
    public void save(String id, E entity, boolean allowOverwriteEntity, boolean allowOverwriteReadonlyMembers) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        if (baseClass != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().baseClass(), baseClass.getSimpleName()));
        }
        criteria.add(PropertyCriterion.eq(criteria.proto().className(), entity.getInstanceValueClass().getSimpleName()));
        criteria.add(PropertyCriterion.eq(criteria.proto().identifierKey(), id));

        CustomizationHolder oldVersionHolder = Persistence.service().retrieve(criteria);
        if (!allowOverwriteEntity & oldVersionHolder != null) {
            throw new CustomizationOverwriteAttemptException();
        }
        if (!allowOverwriteReadonlyMembers & oldVersionHolder != null) {
            E oldVersion = deserialize(oldVersionHolder, (E) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));

            for (String memberName : oldVersion.getEntityMeta().getMemberNames()) {
                ReadOnly readOnly = oldVersion.getEntityMeta().getMemberMeta(memberName).getAnnotation(ReadOnly.class);
                if (readOnly != null) {
                    boolean readOnlyViolation = false;
                    if (oldVersion.getMember(memberName).isNull()) {
                        if (!entity.getMember(memberName).isNull() & !readOnly.allowOverrideNull()) {
                            readOnlyViolation = true;
                        }
                    } else if (!oldVersion.getMember(memberName).equals(entity.getMember(memberName))) {
                        readOnlyViolation = true;
                    }
                    if (readOnlyViolation) {
                        throw new Error(SimpleMessageFormat.format("not allowed to overwrite readonly property {0} of {1}", memberName, oldVersion
                                .getInstanceValueClass().getSimpleName()));
                    }
                }
            }
        }

        // save new        
        XMLStringWriter stringWriter = new XMLStringWriter();
        XMLEntityWriter entityWriter = new XMLEntityWriter(stringWriter, new XMLEntityNamingConventionDefault());
        entityWriter.write(entity);

        CustomizationHolder settingsHolder = EntityFactory.create(customizationHolderEntityClass);
        if (oldVersionHolder != null) {
            settingsHolder.setPrimaryKey(oldVersionHolder.getPrimaryKey());
        }
        settingsHolder.identifierKey().setValue(id);
        if (baseClass != null) {
            settingsHolder.baseClass().setValue(baseClass.getSimpleName());
        }
        settingsHolder.className().setValue(entity.getInstanceValueClass().getSimpleName());
        settingsHolder.serializedForm().setValue(stringWriter.toString());

        Persistence.service().merge(settingsHolder);

    }

    /** for polymorphic loading */
    public E load(String id) {
        return load(id, null);
    }

    public E load(String id, E proto) {
        assert proto != null || (proto == null & baseClass != null) : "please intantiate CustomizationPersistenceHolder with base class in order to use polymorphic load";
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        if (baseClass != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().baseClass(), baseClass.getSimpleName()));
        }
        if (proto != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getSimpleName()));
        }
        criteria.add(PropertyCriterion.eq(criteria.proto().identifierKey(), id));
        CustomizationHolder holder = Persistence.service().retrieve(criteria);

        if (holder == null) {
            return null;
        } else {
            return deserialize(holder, proto);
        }

    }

    public void delete(String id) {
        delete(id, null);
    }

    public void delete(String id, E proto) {
        // TODO add assertion about polymorphic usage

        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        if (baseClass != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().baseClass(), baseClass.getSimpleName()));
        }
        if (proto != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getSimpleName()));
        }
        criteria.add(PropertyCriterion.eq(criteria.proto().identifierKey(), id));

        Persistence.service().delete(criteria);
    }

    /**
     * Deletes multiple customizations matching having <code>idPattern</code> as a substring
     * 
     * @param idPattern
     *            a substring used for matching the IDs, can contain standard SQL matching syntax ('%' and '?')
     */
    public void deleteMatching(String idPattern, E proto) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        if (baseClass != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().baseClass(), baseClass.getSimpleName()));
        }
        if (proto != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getSimpleName()));
        }
        criteria.add(PropertyCriterion.like(criteria.proto().identifierKey(), idPattern));

        Persistence.service().delete(criteria);
    }

    /**
     * Polymorphic version of {@link #deleteMatching(String, IEntity)}
     */
    public void deleteMatching(String idPattern) {
        deleteMatching(idPattern, null);
    }

    private E deserialize(CustomizationHolder cusomizationHolder, E proto) {
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            Document doc = builder.parse(new InputSource(new StringReader(cusomizationHolder.serializedForm().getValue())));

            return (E) new XMLEntityParser().parse(proto != null ? proto.getInstanceValueClass() : baseClass, doc.getDocumentElement());

        } catch (Throwable e) {
            throw new RuntimeException("failed to deserialize data", e);
        }
    }
}

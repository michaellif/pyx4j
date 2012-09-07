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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.xml.XMLEntityNamingConventionDefault;
import com.pyx4j.entity.xml.XMLEntityParser;
import com.pyx4j.entity.xml.XMLEntityWriter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.site.rpc.customization.CustomizationOverwriteAttemptException;
import com.pyx4j.site.shared.domain.cusomization.CustomizationHolder;

public class CustomizationPersistenceHelper<E extends IEntity> {

    private final Class<? extends CustomizationHolder> customizationHolderEntityClass;

    public <H extends CustomizationHolder> CustomizationPersistenceHelper(Class<H> customizationHolderEntityClass) {
        this.customizationHolderEntityClass = customizationHolderEntityClass;
    }

    public Iterable<String> list(E proto) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getName()));

        Iterator<? extends CustomizationHolder> i = Persistence.service().query(null, criteria, AttachLevel.ToStringMembers);

        Vector<String> result = new Vector<String>();
        while (i.hasNext()) {
            result.add(i.next().getStringView());
        }
        return result;
    }

    public void save(String id, E entity, boolean allowOverwrite) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().className(), entity.getInstanceValueClass().getName()));
        criteria.add(PropertyCriterion.eq(criteria.proto().identifierKey(), id));

        if (!allowOverwrite && Persistence.service().count(criteria) != 0) {
            throw new CustomizationOverwriteAttemptException();
        }

        // delete the old version
        Persistence.service().delete(criteria);

        // save new
        XMLStringWriter stringWriter = new XMLStringWriter();
        XMLEntityWriter entityWriter = new XMLEntityWriter(stringWriter, new XMLEntityNamingConventionDefault());
        entityWriter.write(entity);

        CustomizationHolder settingsHolder = EntityFactory.create(customizationHolderEntityClass);
        settingsHolder.identifierKey().setValue(id);
        settingsHolder.className().setValue(entity.getInstanceValueClass().getName());
        settingsHolder.serializedForm().setValue(stringWriter.toString());

        Persistence.service().persist(settingsHolder);

    }

    public E load(String id, E proto) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().identifierKey(), id));
        criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getName()));
        CustomizationHolder holder = Persistence.service().retrieve(criteria);

        if (holder == null) {
            return null;
        } else {
            E entity = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);
                factory.setValidating(false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setErrorHandler(null);
                Document doc = builder.parse(new InputSource(new StringReader(holder.serializedForm().getValue())));

                entity = (E) new XMLEntityParser().parse(proto.getInstanceValueClass(), doc.getDocumentElement());
            } catch (Throwable e) {
                throw new RuntimeException("failed to deserialize data", e);
            }
            return entity;
        }

    }

    public void delete(String id, E proto) {
        EntityQueryCriteria<? extends CustomizationHolder> criteria = EntityQueryCriteria.create(customizationHolderEntityClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().className(), proto.getInstanceValueClass().getName()));
        criteria.add(PropertyCriterion.eq(criteria.proto().identifierKey(), id));

        // delete the old version
        Persistence.service().delete(criteria);
    }
}

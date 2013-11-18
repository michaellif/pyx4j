/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

import com.propertyvista.domain.legal.utils.Formatter;
import com.propertyvista.domain.legal.utils.Partitioner;

public abstract class PdfFieldsMapping<E extends IEntity> {

    private final E proto;

    private final HashMap<String, PdfFieldDescriptor> mapping;

    public PdfFieldsMapping(Class<E> klass) {
        proto = EntityFactory.getEntityPrototype(klass);
        mapping = new HashMap<String, PdfFieldDescriptor>();

        configure();
    }

    public final PdfFieldDescriptor getDescriptor(IObject<?> field) {
        return getDescriptor(field.getFieldName());
    }

    public final PdfFieldDescriptor getDescriptor(String fieldName) {
        return mapping.get(fieldName);
    }

    /** called from constructor, should hold descriptors of fields */
    protected abstract void configure();

    protected final E proto() {
        return proto;
    }

    protected final PdfFieldDescriptorBuilder field(IObject<?> field) {
        return new PdfFieldDescriptorBuilder(field);
    }

    protected class PdfFieldDescriptorBuilder {

        private final IObject<?> field;

        private final List<String> mappings = new LinkedList<String>();

        private final List<Formatter> formatters = new LinkedList<Formatter>();

        private Partitioner partitioner = null;

        public PdfFieldDescriptorBuilder(IObject<?> field) {
            this.field = field;
        }

        public PdfFieldDescriptorBuilder formatBy(Formatter formatter) {
            this.formatters.add(formatter);
            return this;
        }

        public PdfFieldDescriptorBuilder mapTo(String fieldName) {
            this.mappings.add(fieldName);
            return this;
        }

        public PdfFieldDescriptorBuilder partitionBy(Partitioner partitioner) {
            this.partitioner = partitioner;
            return this;
        }

        public void define() {
            mapping.put(field.getFieldName(), new PdfFieldDescriptor(formatters, mappings, partitioner));
        }
    }
}

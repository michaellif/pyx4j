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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;

import com.propertyvista.domain.legal.utils.Formatter;
import com.propertyvista.domain.legal.utils.Partitioner;

public abstract class PdfFieldsMapping<E extends IEntity> {

    private final E proto;

    private final HashMap<String, PdfFieldDescriptor> mapping;

    private final HashMap<String, List<PdfFieldsMapping<?>>> tablesMapping;

    public PdfFieldsMapping(Class<E> klass) {
        proto = EntityFactory.getEntityPrototype(klass);
        mapping = new HashMap<String, PdfFieldDescriptor>();
        tablesMapping = new HashMap<String, List<PdfFieldsMapping<?>>>();
        configure();
    }

    public final PdfFieldDescriptor getDescriptor(IObject<?> field) {
        return mapping.get(field.getFieldName());
    }

    public final PdfFieldDescriptor getDescriptor(IList<?> tableField, IObject<?> field, int row) {
        return tablesMapping.get(tableField.getFieldName()).get(row).getDescriptor(field);
    }

    /** called from constructor, should hold descriptors of fields */
    protected abstract void configure();

    protected final E proto() {
        return proto;
    }

    protected final PdfFieldDescriptorBuilder field(IObject<?> field) {
        return new PdfFieldDescriptorBuilder(field);
    }

    protected final <TableRow extends IEntity> PdfTableDescriptorBuilder<TableRow> table(IList<TableRow> tableField) {
        return new PdfTableDescriptorBuilder<TableRow>(tableField.getFieldName(), tableField.getValueClass());
    }

    protected class PdfFieldDescriptorBuilder {

        private final IObject<?> field;

        private final List<String> mappings = new LinkedList<String>();

        private final List<Formatter> formatters = new LinkedList<Formatter>();

        private Partitioner partitioner = null;

        private List<String> states = Collections.emptyList();

        public PdfFieldDescriptorBuilder(IObject<?> field) {
            this.field = field;
        }

        public PdfFieldDescriptorBuilder formatBy(Formatter formatter) {
            this.formatters.add(formatter);
            return this;
        }

        public PdfFieldDescriptorBuilder mapTo(String... fieldName) {
            mapTo(Arrays.asList(fieldName));
            return this;
        }

        public PdfFieldDescriptorBuilder mapTo(List<String> fields) {
            this.mappings.addAll(fields);
            return this;
        }

        public PdfFieldDescriptorBuilder partitionBy(Partitioner partitioner) {
            this.partitioner = partitioner;
            return this;
        }

        public PdfFieldDescriptorBuilder states(String... states) {
            this.states = Arrays.asList(states);
            return this;
        }

        public void define() {
            mapping.put(field.getFieldName(), new PdfFieldDescriptor(formatters, mappings, partitioner, states));
        }
    }

    protected class PdfTableDescriptorBuilder<TableRow extends IEntity> {

        private final LinkedList<PdfFieldsMapping<?>> rowMapping;

        private final String fieldName;

        public PdfTableDescriptorBuilder(String fieldName, Class<TableRow> tableRowKlass) {
            this.rowMapping = new LinkedList<PdfFieldsMapping<?>>();
            this.fieldName = fieldName;
        }

        public PdfTableDescriptorBuilder rowMapping(List<? extends PdfFieldsMapping<TableRow>> rowMapping) {
            this.rowMapping.addAll(rowMapping);
            return this;
        }

        public PdfTableDescriptorBuilder rowMapping(PdfFieldsMapping<TableRow>... rowMapping) {
            return rowMapping(Arrays.asList(rowMapping));
        }

        public void define() {
            tablesMapping.put(fieldName, rowMapping);
        }

    }
}

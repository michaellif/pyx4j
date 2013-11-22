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
package com.propertyvista.biz.legal.forms.framework.mapping;

import java.util.Collections;
import java.util.List;


public class PdfFieldDescriptor {

    private final List<Formatter> formatters;

    private final List<String> mappedFields;

    private final Partitioner partitioner;

    private final List<String> states;

    public PdfFieldDescriptor(List<Formatter> formatters, List<String> mappedFields, Partitioner partitioner, List<String> states) {
        this.formatters = formatters;
        this.mappedFields = mappedFields;
        this.partitioner = partitioner;
        this.states = states;
    }

    public List<String> mappedFields() {
        return Collections.unmodifiableList(mappedFields);
    }

    public Partitioner partitioner() {
        return this.partitioner;
    }

    public List<Formatter> formatters() {
        return Collections.unmodifiableList(formatters);
    }

    public List<String> states() {
        return Collections.unmodifiableList(states);
    }

}

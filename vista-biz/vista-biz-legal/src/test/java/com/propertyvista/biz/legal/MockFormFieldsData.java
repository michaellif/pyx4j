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
 * Created on 2013-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.legal.utils.PdfFormFieldFormatter;
import com.propertyvista.domain.legal.utils.PdfFormFieldMapping;
import com.propertyvista.domain.legal.utils.PdfFormFieldPartitioner;

@Transient
public interface MockFormFieldsData extends IEntity {

    @PdfFormFieldMapping("field1")
    IPrimitive<String> field1();

    @PdfFormFieldPartitioner(MockFieldPartitioner.class)
    @PdfFormFieldMapping("field2_1{2},field2_2{3},field2_3{4}")
    IPrimitive<String> field2();

    @PdfFormFieldFormatter(MockFieldFormatter.class)
    @PdfFormFieldMapping("field10")
    IPrimitive<String> field10();

}

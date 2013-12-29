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
 * Created on 2013-11-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.ltbcommon;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;

import com.propertyvista.biz.legal.forms.framework.mapping.PdfFieldsMapping;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.formatters.DateFormatter;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.formatters.MoneyFormatter;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.formatters.MoneyShortFormatter;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.formatters.UppercaseFormatter;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.partitioners.DatePartitioner;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.partitioners.MoneyPartitioner;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.partitioners.MoneyShortPartitioner;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.partitioners.PhoneNumberPartitioner;
import com.propertyvista.biz.legal.forms.ltbcommon.utils.LandlordAndTenantBoardPdfFormUtils;

public abstract class LtbFormFieldsMapping<E extends IEntity> extends PdfFieldsMapping<E> {

    public LtbFormFieldsMapping(Class<E> klass) {
        super(klass);
    }

    /** generate names of partitioned fields according to convention used in Landlord and Tenant Board forms */
    protected List<String> fieldsPartition(String fieldName, int... partLengthsVector) {
        return LandlordAndTenantBoardPdfFormUtils.split(fieldName, partLengthsVector);
    }

    protected List<String> phonePartition(String fieldName) {
        return LandlordAndTenantBoardPdfFormUtils.split(fieldName, 3, 3, 4);
    }

    protected List<String> datePartition(String fieldName) {
        return LandlordAndTenantBoardPdfFormUtils.split(fieldName, 2, 2, 4);
    }

    protected PdfFieldDescriptorBuilder money(IObject<?> member) {
        return field(member).formatBy(new MoneyFormatter()).partitionBy(new MoneyPartitioner());
    }

    protected PdfFieldDescriptorBuilder moneyShort(IObject<?> member) {
        return field(member).formatBy(new MoneyShortFormatter()).partitionBy(new MoneyShortPartitioner());
    }

    protected PdfFieldDescriptorBuilder date(IObject<?> member) {
        return field(member).formatBy(new DateFormatter()).partitionBy(new DatePartitioner());
    }

    protected PdfFieldDescriptorBuilder text(IObject<?> member) {
        return field(member).formatBy(new UppercaseFormatter());
    }

    protected PdfFieldDescriptorBuilder phone(IObject<?> member) {
        return field(member).partitionBy(new PhoneNumberPartitioner());
    }

}

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
 * Created on Oct 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.domain.test;

import java.sql.Time;
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public interface EntityI extends IEntity {

    @I18n
    public enum Enum1 {

        Value0, Value1, Value2, Value3;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Caption(description = "Description for optionalStringMemberI")
    IPrimitive<String> optionalTextI();

    IPrimitive<String> optionalTextII();

    @NotNull
    IPrimitive<String> mandatoryTextI();

    @NotNull
    IPrimitive<String> mandatoryTextII();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> optionalTextAreaI();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> optionalTextAreaII();

    @Editor(type = EditorType.textarea)
    @NotNull
    IPrimitive<String> mandatoryTextAreaI();

    @Editor(type = EditorType.textarea)
    @NotNull
    IPrimitive<String> mandatoryTextAreaII();

    IPrimitive<Integer> optionalIntegerI();

    IPrimitive<Integer> optionalIntegerII();

    @NotNull
    IPrimitive<Integer> mandatoryIntegerI();

    @NotNull
    IPrimitive<Integer> mandatoryIntegerII();

    IPrimitive<Boolean> checkBox();

    @Editor(type = EditorType.password)
    IPrimitive<String> optionalPasswordI();

    @Editor(type = EditorType.password)
    IPrimitive<String> optionalPasswordII();

    @Editor(type = EditorType.password)
    @NotNull
    IPrimitive<String> mandatoryPasswordI();

    @Editor(type = EditorType.password)
    @NotNull
    IPrimitive<String> mandatoryPasswordII();

    IPrimitive<Enum1> optionalEnumI();

    @NotNull
    IPrimitive<Enum1> mandatoryEnumI();

    IPrimitive<Enum1> optionalEnumII();

    @NotNull
    IPrimitive<Enum1> mandatoryEnumII();

    @Editor(type = EditorType.suggest)
    IPrimitive<String> optionalSuggest();

    @NotNull
    @Editor(type = EditorType.suggest)
    IPrimitive<String> mandatorySuggest();

    IPrimitive<Date> optionalDatePicker();

    @NotNull
    IPrimitive<Date> mandatoryDatePicker();

    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> optionalTimePicker();

    @NotNull
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> mandatoryTimePicker();

    IPrimitive<Date> optionalSingleMonthDatePicker();

    @NotNull
    IPrimitive<Date> mandatorySingleMonthDatePicker();

    @Editor(type = EditorType.phone)
    IPrimitive<String> optionalPhone();

    @Editor(type = EditorType.phone)
    @NotNull
    IPrimitive<String> mandatoryPhone();

    @Editor(type = EditorType.money)
    IPrimitive<Double> optionalMoney();

    @Editor(type = EditorType.money)
    @NotNull
    IPrimitive<Double> mandatoryMoney();

    @Owned
    IList<EntityII> entityIIList();

    @Owned
    IList<EntityIV> entityIVList();

    IList<EntityIV> entityIVListNotOwned();

    @Editor(type = EditorType.email)
    IPrimitive<String> optionalEmail();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> mandatoryEmail();

}

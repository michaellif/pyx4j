/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 29, 2014
 * @author stanp
 */
package com.pyx4j.entity.shared;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@EmbeddedEntity
@ToStringFormat("{0,choice,!null#${0,number,#,##0.00}|null#{1,number,percent}}")
public interface IMoneyPercentAmount extends IEntity {

    @I18n
    @XmlType(name = "ValueType")
    public enum ValueType {
        Monetary, Percentage;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 0)
    @Format("$#,##0.00")
    IPrimitive<BigDecimal> amount();

    @ToString(index = 1)
    @Format("#.00%")
    IPrimitive<BigDecimal> percent();
}

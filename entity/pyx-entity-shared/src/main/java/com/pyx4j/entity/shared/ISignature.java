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
 * Created on Dec 5, 2013
 * @author michaellif
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@ToStringFormat("{0,choice,null#|!null#{0}}{1,choice,null#|!null#, IP: {1}}{2,choice,null#|!null#, {2}}{3,choice,null#|!null#, {3}}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@EmbeddedEntity
public interface ISignature extends IEntity {

    public static enum SignatureFormat implements Serializable {
        FullName, Initials, AgreeBox, AgreeBoxAndFullName, None
    }

    @ToString(index = 0)
    @Format("MM/dd/yyyy hh:mm a")
    IPrimitive<Date> signDate();

    @ToString(index = 1)
    @Caption(name = "IP Address")
    @Length(39)
    IPrimitive<String> ipAddress();

    IPrimitive<SignatureFormat> signatureFormat();

    @ToString(index = 2)
    @NotNull
    @Caption(name = "Type Your Full Name")
    IPrimitive<String> fullName();

    @ToString(index = 3)
    @NotNull
    @Caption(name = "Type Your Initials", description = "Maximum 3 letters. Do not use periods or spaces.")
    IPrimitive<String> initials();

    @ToString(index = 4)
    IPrimitive<Boolean> agree();
}

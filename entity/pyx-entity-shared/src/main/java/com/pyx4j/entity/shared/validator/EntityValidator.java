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
 * Created on 2012-05-17
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.validator;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;

public class EntityValidator {

    private static final I18n i18n = I18n.get(EntityValidator.class);

    public static void validate(IEntity entity) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = entity.getMember(memberName);
            if ((memberMeta.isValidatorAnnotationPresent(NotNull.class)) && (member.isNull())) {
                throw new RuntimeException(i18n.tr("{0} is required", member.getMeta().getCaption()));
            }
            if (memberMeta.isValidatorAnnotationPresent(Length.class) && memberMeta.getValueClass().equals(String.class)) {
                String value = (String) member.getValue();
                if ((value != null) && (value.length() > memberMeta.getLength())) {
                    throw new RuntimeException(i18n.tr("Length of member {0}  is greater then required", member.getMeta().getCaption()));
                }
            }

        }
    }
}

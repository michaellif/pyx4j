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
package com.pyx4j.entity.core.validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;

public class EntityValidator {

    private static final I18n i18n = I18n.get(EntityValidator.class);

    public static class DefaultValidationMesageFormatter implements ValidationMesageFormatter {

        @Override
        public String format(IEntity entity, IObject<?> member) {
            return member.getMeta().getCaption();
        }

    };

    public static class StringViewValidationMesageFormatter implements ValidationMesageFormatter {

        @Override
        public String format(IEntity entity, IObject<?> member) {
            String str = entity.getStringView();
            if (CommonsStringUtils.isStringSet(str)) {
                return entity.getEntityMeta().getCaption() + "(" + str + ")." + member.getMeta().getCaption();
            } else {
                return entity.getEntityMeta().getCaption() + "." + member.getMeta().getCaption();
            }
        }

    };

    public static void validate(IEntity entity) {
        validate(entity, new DefaultValidationMesageFormatter());
    }

    public static void validate(IEntity entity, ValidationMesageFormatter formatter) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = entity.getMember(memberName);
            if ((memberMeta.isValidatorAnnotationPresent(NotNull.class)) && (member.isNull())) {
                throw new RuntimeException(i18n.tr("{0} is required", formatter.format(entity, member)));
            }
            if (memberMeta.isValidatorAnnotationPresent(Length.class) && memberMeta.getValueClass().equals(String.class)) {
                String value = (String) member.getValue();
                if ((value != null) && (value.length() > memberMeta.getLength())) {
                    throw new RuntimeException(i18n.tr("Length of member {0} is greater then required", formatter.format(entity, member)));
                }
            }

        }
    }

    public static void validateRecursively(IEntity entity, ValidationMesageFormatter formatter) {
        validateRecursively(entity, formatter, new HashSet<IEntity>());
    }

    private static void validateRecursively(IEntity entity, ValidationMesageFormatter formatter, Set<IEntity> dejaVu) {
        if (dejaVu.contains(entity)) {
            return;
        }
        dejaVu.add(entity);
        EntityValidator.validate(entity, formatter);
        EntityMeta em = entity.getEntityMeta();

        for (String memberName : em.getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            switch (member.getMeta().getObjectClassType()) {
            case Entity:
                if (member.isNull()) {
                    if ((member.getMeta().isValidatorAnnotationPresent(NotNull.class))) {
                        throw new RuntimeException(i18n.tr("{0} is required", formatter.format(entity, member)));
                    }
                } else {
                    EntityValidator.validateRecursively(((IEntity) member).cast(), formatter, dejaVu);
                }
                break;
            case EntityList:
            case EntitySet:
                @SuppressWarnings("unchecked")
                Iterator<IEntity> lit = ((ICollection<IEntity, ?>) member).iterator();
                while (lit.hasNext()) {
                    IEntity ent = lit.next();
                    EntityValidator.validateRecursively(ent.cast(), formatter, dejaVu);
                }
                break;
            default:
                break;
            }
        }
    }
}

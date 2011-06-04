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
 * Created on Jun 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityArgsConverter {

    public static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static Map<String, String> convertToArgs(IEntity entity) {
        Map<String, String> map = new HashMap<String, String>();

        for (String memberName : entity.getEntityMeta().getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
            if (!member.isNull()) {
                if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                    if (memberMeta.getValueClass().equals(Date.class)) {
                        map.put(memberName, TimeUtils.simpleFormat((Date) entity.getMember(memberName).getValue(), DATE_TIME_FORMAT));
                    } else if (memberMeta.getValueClass().equals(LogicalDate.class)) {
                        map.put(memberName, TimeUtils.simpleFormat((Date) entity.getMember(memberName).getValue(), DATE_FORMAT));
                    } else {
                        map.put(memberName, entity.getMember(memberName).getValue().toString());
                    }
                } else if (ObjectClassType.Entity.equals(memberMeta.getObjectClassType())) {
                    IEntity nested = (IEntity) entity.getMember(memberName);
                    Map<String, String> nestedMap = convertToArgs(nested);
                    for (String nestedKey : nestedMap.keySet()) {
                        map.put(memberMeta.getFieldName() + "." + nestedKey, nestedMap.get(nestedKey));
                    }
                }
            }
        }
        return map;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <E extends IEntity> E createFromArgs(Class<E> clazz, Map<String, String> args) {

        E entity = EntityFactory.create(clazz);

        List<String> memberNames = entity.getEntityMeta().getMemberNames();

        if (memberNames == null) {
            return entity;
        }

        for (String memberName : args.keySet()) {
            if (memberNames.contains(memberName)) {
                MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
                IObject<?> member = entity.getMember(memberName);
                if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                    if (memberMeta.getValueClass().equals(Date.class)) {
                        ((IPrimitive<Date>) member).setValue(TimeUtils.simpleParse(args.get(memberName), DATE_TIME_FORMAT));
                    } else if (memberMeta.getValueClass().equals(LogicalDate.class)) {
                        ((IPrimitive<Date>) member).setValue(new LogicalDate(TimeUtils.simpleParse(args.get(memberName), DATE_FORMAT)));
                    } else {
                        ((IPrimitive) member).setValue(((IPrimitive) member).parse(args.get(memberName)));
                    }
                }
            }
        }

        return entity;
    }
}

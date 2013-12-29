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
 * Created on Aug 10, 2012
 * @author Artyom
 * @version $Id$
 */
package com.pyx4j.essentials.server.services.reports;

import java.io.Serializable;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.site.shared.domain.reports.PropertyCriterionEntity;

public class ReportCriteriaBuilder {

    public static <E extends IEntity> EntityQueryCriteria<E> build(Class<E> entityClass, List<PropertyCriterionEntity> criteriaEntity) {
        EntityQueryCriteria<E> criteria = EntityQueryCriteria.create(entityClass);

        E proto = EntityFactory.getEntityPrototype(entityClass);
        for (PropertyCriterionEntity criterionEntity : criteriaEntity) {
            Serializable parsedValue = null;
            String strValue = criterionEntity.value().getValue();
            IObject<?> member = proto.getMember(new Path(criterionEntity.path().getValue()));
            if (member instanceof IPrimitive<?>) {
                if (member.getValueClass().equals(Integer.class)) {
                    parsedValue = Integer.parseInt(strValue);
                } else if (member.getValueClass().equals(String.class)) {
                    parsedValue = strValue;
                } else if (member.getValueClass().equals(LogicalDate.class)) {
                    parsedValue = new LogicalDate(DateUtils.detectDateformat(strValue));
                }
            } else {
                throw new Error("interperation of entity value is not implemented (yet)");
            }

            criteria.add(new PropertyCriterion(member, criterionEntity.restriction().getValue(), parsedValue));
        }
        return criteria;
    }
}

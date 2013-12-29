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
 * Created on 2012-11-06
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;

class EntityImplValidator {

    private static final Logger log = LoggerFactory.getLogger(EntityImplValidator.class);

    public static void validate(Class<? extends IEntity> handlerClass) {
        IEntity entity;
        try {
            entity = handlerClass.newInstance();
        } catch (Throwable e) {
            log.error(handlerClass.getName() + " instantiation error", e);
            throw new Error(e.getMessage());
        }

        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta;
            try {
                memberMeta = em.getMemberMeta(memberName);
            } catch (Throwable e) {
                log.error("getMemberMeta error", e);
                throw new Error("Can't get MemberMeta '" + memberName + "' of " + em.getEntityClass().getName());
            }
            if (memberMeta.getValueClass() == null) {
                throw new Error("Can't access member '" + memberName + "' of " + em.getEntityClass().getName());
            }
        }

    }

}

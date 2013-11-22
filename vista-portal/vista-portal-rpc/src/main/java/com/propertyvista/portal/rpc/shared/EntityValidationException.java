/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

public class EntityValidationException extends UserRuntimeException {

    private static final long serialVersionUID = -2620946849094280783L;

    public static final class MemberValidationError implements Serializable {

        private static final long serialVersionUID = -4023967634652358384L;

        IObject<?> member;

        String message;

        private MemberValidationError() {
        };

        private MemberValidationError(IObject<?> member, String validationErrorMessage) {
            this.member = member;
            this.message = validationErrorMessage;
        }

        public IObject<?> getMember() {
            return this.member;
        }

        public String getMessage() {
            return this.message;
        }

    }

    public static class Builder<E extends IEntity> {

        E proto;

        EntityValidationException entityValidationException;

        private Builder() {
        };

        private Builder(Class<E> clazz) {
            this.proto = EntityFactory.getEntityPrototype(clazz);
            this.entityValidationException = new EntityValidationException(this.proto);
        }

        public Builder<E> addError(IObject<?> member, String message) {
            // check that member belongs to the entity:
            try {
                proto.getMember(new Path(member.getPath().toString()));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("member " + member.getPath().toString() + " doesn't belong to entity "
                        + this.proto.getInstanceValueClass().getName());
            }

            entityValidationException.validationErrors.put(member.getPath().toString(), message);
            return this;
        }

        public EntityValidationException build() {
            return entityValidationException;
        }

    }

    HashMap<String, String> validationErrors;

    IEntity proto;

    private EntityValidationException() {

    }

    private EntityValidationException(IEntity proto) {
        this.validationErrors = new HashMap<String, String>();
        this.proto = proto;
    }

    public List<MemberValidationError> getErrors() {
        ArrayList<MemberValidationError> errors = new ArrayList<EntityValidationException.MemberValidationError>();
        for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
            errors.add(new MemberValidationError(proto.getMember(new Path(entry.getKey())), entry.getValue()));
        }
        return errors;
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder();
        for (MemberValidationError validationError : getErrors()) {
            messageBuilder.append(validationError.getMember().getPath().toString() + ": " + validationError.getMessage());
            messageBuilder.append("; ");
        }
        return messageBuilder.toString();
    }

    public static <E extends IEntity> Builder<E> make(Class<E> clazz) {
        return new EntityValidationException.Builder<E>(clazz);
    }

}

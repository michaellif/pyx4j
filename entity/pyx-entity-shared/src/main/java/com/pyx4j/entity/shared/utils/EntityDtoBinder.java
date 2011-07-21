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
 * Created on Jul 21, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

public abstract class EntityDtoBinder<DBO extends IEntity, DTO extends IEntity> {

    protected Class<DBO> dboClass;

    protected Class<DTO> dtoClass;

    protected final DBO dboProto;

    protected final DTO dtoProto;

    private final HashMap<Path, Path> binding = new HashMap<Path, Path>();

    protected EntityDtoBinder(Class<DBO> dboClass, Class<DTO> dtoClass) {
        this.dboClass = dboClass;
        this.dtoClass = dtoClass;

        dboProto = EntityFactory.getEntityPrototype(dboClass);
        dtoProto = EntityFactory.getEntityPrototype(dtoClass);
    }

    protected abstract void bind();

    protected final <TYPE> void bind(IObject<TYPE> dtoMember, IObject<TYPE> dboMember) {
        binding.put(dtoMember.getPath(), dboMember.getPath());
    }

    protected final <F extends IEntity, S extends F, D extends F> void bind(Class<F> fragmentClass, S dto, D dbo) {
        for (String memberName : EntityFactory.getEntityMeta(fragmentClass).getMemberNames()) {
            binding.put(dto.getMember(memberName).getPath(), dbo.getMember(memberName).getPath());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DTO dto(DBO dbo) {
        DTO dto = EntityFactory.create(dtoClass);
        dto.setPrimaryKey(dbo.getPrimaryKey());

        if (binding.isEmpty()) {
            bind();
        }
        for (Map.Entry<Path, Path> me : binding.entrySet()) {
            IObject dtoM = dto.getMember(me.getKey());
            IObject dboM = dbo.getMember(me.getValue());
            dtoM.setValue(dboM.getValue());
        }

        return dto;
    }

    public DBO dbo(DTO dto) {
        DBO dbo = EntityFactory.create(dboClass);
        dbo.setPrimaryKey(dto.getPrimaryKey());
        toDbo(dto, dbo);
        return dbo;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void toDbo(DTO dto, DBO dbo) {
        if (binding.isEmpty()) {
            bind();
        }
        for (Map.Entry<Path, Path> me : binding.entrySet()) {
            IObject dtoM = dto.getMember(me.getKey());
            IObject dboM = dbo.getMember(me.getValue());
            dboM.setValue(dtoM.getValue());
        }
    }

}

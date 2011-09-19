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

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

/**
 * The target of this class is bidirectional copy of data between two objects.
 * 
 * bind() function should be implemented to map members that needs to be copied of one class to another.
 */
public abstract class EntityDtoBinder<DBO extends IEntity, DTO extends IEntity> {

    protected Class<DBO> dboClass;

    protected Class<DTO> dtoClass;

    protected final DBO dboProto;

    protected final DTO dtoProto;

    private final boolean copyPrimaryKey;

    private final List<Binding> binding = new Vector<Binding>();

    private static class Binding {

        Path dtoMemberPath;

        Path dboMemberPath;

        @SuppressWarnings("rawtypes")
        EntityDtoBinder binder;

        Binding(IObject<?> dtoMember, IObject<?> dboMember, @SuppressWarnings("rawtypes") EntityDtoBinder binder) {
            dtoMemberPath = dtoMember.getPath();
            dboMemberPath = dboMember.getPath();
            this.binder = binder;
        }

    }

    protected EntityDtoBinder(Class<DBO> dboClass, Class<DTO> dtoClass) {
        this(dboClass, dtoClass, true);
    }

    /**
     * Allow to skip automatic copy of PK, Used to allow duplicated in XML
     */
    protected EntityDtoBinder(Class<DBO> dboClass, Class<DTO> dtoClass, boolean copyPrimaryKey) {
        this.dboClass = dboClass;
        this.dtoClass = dtoClass;
        this.copyPrimaryKey = copyPrimaryKey;

        dboProto = EntityFactory.getEntityPrototype(dboClass);
        dtoProto = EntityFactory.getEntityPrototype(dtoClass);
    }

    protected abstract void bind();

    protected final <TYPE> void bind(IObject<TYPE> dtoMember, IObject<TYPE> dboMember) {
        binding.add(new Binding(dtoMember, dboMember, null));
    }

    protected final <TDBO extends IEntity, TDTO extends IEntity> void bind(TDTO dtoMember, TDBO dboMember, EntityDtoBinder<TDBO, TDTO> binder) {
        binding.add(new Binding(dtoMember, dboMember, binder));
    }

    protected final <TDBO extends IEntity, TDTO extends IEntity> void bind(IList<TDTO> dtoMember, IList<TDBO> dboMember, EntityDtoBinder<TDBO, TDTO> binder) {
        binding.add(new Binding(dtoMember, dboMember, binder));
    }

    protected final <F extends IEntity, S extends F, D extends F> void bind(Class<F> fragmentClass, S dto, D dbo) {
        for (String memberName : EntityFactory.getEntityMeta(fragmentClass).getMemberNames()) {
            binding.add(new Binding(dto.getMember(memberName), dbo.getMember(memberName), null));
        }
    }

    private void init() {
        if (binding.isEmpty()) {
            synchronized (binding) {
                if (binding.isEmpty()) {
                    bind();
                }
            }
        }
    }

    public DTO createDTO(DBO dbo) {
        DTO dto = EntityFactory.create(dtoClass);
        if (copyPrimaryKey) {
            dto.setPrimaryKey(dbo.getPrimaryKey());
        }
        copyDBOtoDTO(dbo, dto);
        return dto;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyDBOtoDTO(DBO dbo, DTO dto) {
        init();
        for (Binding b : binding) {
            IObject dtoM = dto.getMember(b.dtoMemberPath);
            IObject dboM = dbo.getMember(b.dboMemberPath);

            // Assert that all data has been retrieved
            if ((dboM instanceof IEntity) && ((IEntity) dboM).isValuesDetached() && !dtoM.getMeta().isDetached()) {
                throw new Error("Copying detached entity " + ((IEntity) dboM).getDebugExceptionInfoString());
            }

            if (b.binder == null) {
                if (dboM instanceof ICollection) {
                    ((ICollection<IEntity, ?>) dtoM).clear();
                    for (IEntity dboMi : (ICollection<IEntity, ?>) dboM) {
                        if (dboMi.isValuesDetached() && !dtoM.getMeta().isDetached()) {
                            throw new Error("Copying detached entity " + dboMi.getDebugExceptionInfoString());
                        }
                        ((ICollection<IEntity, ?>) dtoM).add(dboMi);
                    }
                } else {
                    dtoM.setValue(dboM.getValue());
                }
            } else if (dtoM instanceof IEntity) {
                b.binder.copyDBOtoDTO((IEntity) dboM, (IEntity) dtoM);
            } else if (dboM instanceof ICollection) {
                ((ICollection<IEntity, ?>) dtoM).clear();
                for (IEntity dboMi : (ICollection<IEntity, ?>) dboM) {
                    ((ICollection<IEntity, ?>) dtoM).add(b.binder.createDTO(dboMi));
                }
            }
        }
    }

    public DBO createDBO(DTO dto) {
        DBO dbo = EntityFactory.create(dboClass);
        if (copyPrimaryKey) {
            dbo.setPrimaryKey(dto.getPrimaryKey());
        }
        copyDTOtoDBO(dto, dbo);
        return dbo;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyDTOtoDBO(DTO dto, DBO dbo) {
        init();
        for (Binding b : binding) {
            IObject dtoM = dto.getMember(b.dtoMemberPath);
            IObject dboM = dbo.getMember(b.dboMemberPath);
            if (b.binder == null) {
                if (dtoM instanceof ICollection) {
                    ((ICollection<IEntity, ?>) dboM).clear();
                    for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                        ((ICollection<IEntity, ?>) dboM).add(dtoMi);
                    }
                } else {
                    dboM.setValue(dtoM.getValue());
                }
            } else if (dtoM instanceof IEntity) {
                b.binder.copyDTOtoDBO((IEntity) dtoM, (IEntity) dboM);
            } else if (dtoM instanceof ICollection) {
                ((ICollection<IEntity, ?>) dboM).clear();
                for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                    ((ICollection<IEntity, ?>) dboM).add(b.binder.createDBO(dtoMi));
                }
            }
        }
    }

}

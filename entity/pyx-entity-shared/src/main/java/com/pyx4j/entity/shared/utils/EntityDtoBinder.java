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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
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

    private final Map<Path, Binding> bindingByDTOMemberPath = new HashMap<Path, Binding>();

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

    private void addBinding(Binding b) {
        if (!b.dboMemberPath.isUndefinedCollectionPath() && !b.dtoMemberPath.isUndefinedCollectionPath()) {
            binding.add(b);
        }
        bindingByDTOMemberPath.put(b.dtoMemberPath, b);
    }

    @SuppressWarnings("unchecked")
    protected final void bindCompleteDBO() {
        bind((Class<IEntity>) dboProto.getValueClass(), dtoProto, dboProto);
    }

    /**
     * binds DTO member of DBO type to DBO
     */
    protected final void bindCompleteDtoMember(DBO dtoEntityMember) {
        addBinding(new Binding(dtoEntityMember.getMember(IEntity.PRIMARY_KEY), dboProto.getMember(IEntity.PRIMARY_KEY), null));
        for (String memberName : EntityFactory.getEntityMeta(dboClass).getMemberNames()) {
            addBinding(new Binding(dtoEntityMember.getMember(memberName), dboProto.getMember(memberName), null));
        }
    }

    protected final <TYPE> void bind(IObject<TYPE> dtoMember, IObject<TYPE> dboMember) {
        addBinding(new Binding(dtoMember, dboMember, null));
    }

    protected final <TDBO extends IEntity, TDTO extends IEntity> void bind(TDTO dtoMember, TDBO dboMember, EntityDtoBinder<TDBO, TDTO> binder) {
        addBinding(new Binding(dtoMember, dboMember, binder));
    }

    protected final <TDBO extends IEntity, TDTO extends IEntity> void bind(IList<TDTO> dtoMember, IList<TDBO> dboMember, EntityDtoBinder<TDBO, TDTO> binder) {
        addBinding(new Binding(dtoMember, dboMember, binder));
    }

    protected final <F extends IEntity, S extends F, D extends F> void bind(Class<F> fragmentClass, S dto, D dbo) {
        addBinding(new Binding(dto.getMember(IEntity.PRIMARY_KEY), dbo.getMember(IEntity.PRIMARY_KEY), null));
        for (String memberName : EntityFactory.getEntityMeta(fragmentClass).getMemberNames()) {
            addBinding(new Binding(dto.getMember(memberName), dbo.getMember(memberName), null));
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

    public Path getBoundDboMemberPath(Path dtoMemberPath) {
        init();
        Binding b = bindingByDTOMemberPath.get(dtoMemberPath);
        if (b != null) {
            return b.dboMemberPath;
        }
        // The binding may have been done by Entity member,
        StringBuilder shortDTOMemberPath = new StringBuilder();
        shortDTOMemberPath.append(dtoMemberPath.getRootObjectClassName()).append(Path.PATH_SEPARATOR);
        Iterator<String> it = dtoMemberPath.getPathMembers().iterator();
        while (it.hasNext()) {
            String member = it.next();
            shortDTOMemberPath.append(member).append(Path.PATH_SEPARATOR);
            b = bindingByDTOMemberPath.get(new Path(shortDTOMemberPath.toString()));
            if (b != null) {
                StringBuilder shortDBOMemberPath = new StringBuilder(b.dboMemberPath.toString());
                while (it.hasNext()) {
                    shortDBOMemberPath.append(it.next()).append(Path.PATH_SEPARATOR);
                }
                return new Path(shortDBOMemberPath.toString());
            }
        }
        return null;
    }

    public DTO createDTO(DBO dbo) {
        DTO dto = EntityFactory.create(dtoClass);
        if (copyPrimaryKey) {
            dto.setPrimaryKey(dbo.getPrimaryKey());
        }
        copyDBOtoDTO(dbo, dto);
        return dto;
    }

    protected boolean retriveDetachedMember(IEntity dboMember) {
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyDBOtoDTO(DBO dbo, DTO dto) {
        init();
        for (Binding b : binding) {
            IObject dtoM = dto.getMember(b.dtoMemberPath);
            IObject dboM = dbo.getMember(b.dboMemberPath);

            // Assert that all data has been retrieved
            if ((dboM instanceof IEntity) && ((IEntity) dboM).isValueDetached() && !dtoM.getMeta().isDetached()) {
                if (!retriveDetachedMember((IEntity) dboM)) {
                    throw new Error("Copying detached entity " + ((IEntity) dboM).getDebugExceptionInfoString());
                }
            }

            if (dboM.getAttachLevel() == AttachLevel.Detached) {
                if (!(dtoM instanceof IPrimitive<?>)) {
                    dtoM.setAttachLevel(AttachLevel.Detached);
                }
            } else if (b.binder == null) {
                if (dboM instanceof ICollection) {
                    if (dboM.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
                        ((ICollection<IEntity, ?>) dtoM).setCollectionSizeOnly(((ICollection<IEntity, ?>) dboM).size());
                    } else {
                        ((ICollection<IEntity, ?>) dtoM).clear();
                        for (IEntity dboMi : (ICollection<IEntity, ?>) dboM) {
                            if (dboMi.isValueDetached() && !dtoM.getMeta().isDetached()) {
                                if (!retriveDetachedMember(dboMi)) {
                                    throw new Error("Copying detached entity " + dboMi.getDebugExceptionInfoString());
                                }
                            }
                            ((ICollection<IEntity, ?>) dtoM).add(dboMi);
                        }
                    }
                } else if (dboM.getAttachLevel() == AttachLevel.IdOnly) {
                    if (!((IEntity) dboM).isObjectClassSameAsDef()) {
                        ((IEntity) dtoM).set(((IEntity) dboM).createIdentityStub());
                    } else {
                        ((IEntity) dtoM).setPrimaryKey(((IEntity) dboM).getPrimaryKey());
                        dtoM.setAttachLevel(AttachLevel.IdOnly);
                    }
                } else if (dboM.getAttachLevel() == AttachLevel.ToStringMembers) {
                    ((IEntity) dboM).copyStringView((IEntity) dtoM);
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

            if (dtoM.getAttachLevel() == AttachLevel.Detached) {
                dboM.setAttachLevel(AttachLevel.Detached);
            } else if (b.binder == null) {
                if (dtoM instanceof ICollection) {
                    ICollection<IEntity, ?> dboMc = (ICollection<IEntity, ?>) dboM;
                    if (dtoM.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
                        dboMc.setCollectionSizeOnly(((ICollection<IEntity, ?>) dtoM).size());
                    } else {
                        dboMc.setAttachLevel(AttachLevel.Attached);
                        dboMc.clear();
                        for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                            ((ICollection<IEntity, ?>) dboM).add(dtoMi);
                        }
                    }
                } else if (dtoM.getAttachLevel() == AttachLevel.IdOnly) {
                    if (!((IEntity) dtoM).isObjectClassSameAsDef()) {
                        ((IEntity) dboM).set(((IEntity) dtoM).createIdentityStub());
                    } else {
                        ((IEntity) dboM).setPrimaryKey(((IEntity) dtoM).getPrimaryKey());
                        dboM.setAttachLevel(AttachLevel.IdOnly);
                    }
                } else if (dtoM.getAttachLevel() == AttachLevel.ToStringMembers) {
                    ((IEntity) dtoM).copyStringView((IEntity) dboM);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean updateDBO(DTO dto, DBO dbo) {
        init();
        boolean updated = false;
        Set<IEntity> processed = new HashSet<IEntity>();
        for (Binding b : binding) {
            IObject dtoM = dto.getMember(b.dtoMemberPath);
            if (dtoM.isNull()) {
                continue;
            }
            IObject dboM = dbo.getMember(b.dboMemberPath);
            if (b.binder == null) {
                if (dtoM instanceof ICollection) {
                    if (EntityGraph.update((ICollection<IEntity, ?>) dboM, (ICollection<IEntity, ?>) dtoM, processed)) {
                        onUpdateDBOmember(dto, dbo, dboM);
                        updated = true;
                    }
                } else if (dtoM instanceof IEntity) {
                    if (EntityGraph.update((IEntity) dboM, (IEntity) dtoM, processed)) {
                        onUpdateDBOmember(dto, dbo, dboM);
                        updated = true;
                    }
                } else {
                    if (!EqualsHelper.equals(dboM.getValue(), dtoM.getValue())) {
                        dboM.setValue(dtoM.getValue());
                        onUpdateDBOmember(dto, dbo, dboM);
                        updated = true;
                    }
                }
            } else if (dtoM instanceof IEntity) {
                updated |= b.binder.updateDBO((IEntity) dtoM, (IEntity) dboM);
            } else if (dtoM instanceof ICollection) {
                ICollection<IEntity, ?> dboMc = (ICollection<IEntity, ?>) dboM;
                for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                    //find
                    boolean found = false;
                    for (IEntity dboMi : dboMc) {
                        if (dtoMi.equals(dboMi) || dtoMi.businessEquals(dboMi)) {
                            found = true;
                            updated |= b.binder.updateDBO(dtoMi, dboMi);
                            break;
                        }
                    }

                    if (!found) {
                        ((ICollection<IEntity, ?>) dboM).add(b.binder.createDBO(dtoMi));
                        updated = true;
                    }
                }
            } else {
                throw new Error("Unexpected object class " + dtoM.getObjectClass());
            }
        }
        return updated;
    }

    protected void onUpdateDBOmember(DTO dto, DBO dbo, IObject<?> dboM) {

    }

}

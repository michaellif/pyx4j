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
public abstract class EntityBinder<BO extends IEntity, TO extends IEntity> {

    protected Class<BO> boClass;

    protected Class<TO> toClass;

    protected final BO boProto;

    protected final TO toProto;

    private final boolean copyPrimaryKey;

    private final List<Binding> binding = new Vector<Binding>();

    private final Map<Path, Binding> bindingByTOMemberPath = new HashMap<Path, Binding>();

    private static class Binding {

        Path toMemberPath;

        Path boMemberPath;

        @SuppressWarnings("rawtypes")
        EntityBinder binder;

        Binding(IObject<?> dtoMember, IObject<?> dboMember, @SuppressWarnings("rawtypes") EntityBinder binder) {
            toMemberPath = dtoMember.getPath();
            boMemberPath = dboMember.getPath();
            this.binder = binder;
        }

    }

    protected EntityBinder(Class<BO> boClass, Class<TO> toClass) {
        this(boClass, toClass, true);
    }

    /**
     * Allow to skip automatic copy of PK, Used to allow duplicated in XML
     */
    protected EntityBinder(Class<BO> boClass, Class<TO> toClass, boolean copyPrimaryKey) {
        this.boClass = boClass;
        this.toClass = toClass;
        this.copyPrimaryKey = copyPrimaryKey;

        boProto = EntityFactory.getEntityPrototype(boClass);
        toProto = EntityFactory.getEntityPrototype(toClass);
    }

    protected abstract void bind();

    private void addBinding(Binding b) {
        if (!b.boMemberPath.isUndefinedCollectionPath() && !b.toMemberPath.isUndefinedCollectionPath()) {
            binding.add(b);
        }
        bindingByTOMemberPath.put(b.toMemberPath, b);
    }

    @SuppressWarnings("unchecked")
    protected final void bindCompleteDBO() {
        bind((Class<IEntity>) boProto.getValueClass(), toProto, boProto);
    }

    /**
     * binds TO member of BO type to BO
     */
    protected final void bindCompleteDtoMember(BO toEntityMember) {
        addBinding(new Binding(toEntityMember.getMember(IEntity.PRIMARY_KEY), boProto.getMember(IEntity.PRIMARY_KEY), null));
        for (String memberName : EntityFactory.getEntityMeta(boClass).getMemberNames()) {
            addBinding(new Binding(toEntityMember.getMember(memberName), boProto.getMember(memberName), null));
        }
    }

    protected final <TYPE> void bind(IObject<TYPE> toMember, IObject<TYPE> boMember) {
        addBinding(new Binding(toMember, boMember, null));
    }

    protected final <TBO extends IEntity, TTO extends IEntity> void bind(TTO toMember, TBO boMember, EntityBinder<TBO, TTO> binder) {
        addBinding(new Binding(toMember, boMember, binder));
    }

    protected final <TDBO extends IEntity, TDTO extends IEntity> void bind(IList<TDTO> toMember, IList<TDBO> boMember, EntityBinder<TDBO, TDTO> binder) {
        addBinding(new Binding(toMember, boMember, binder));
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

    public Path getBoundDboMemberPath(Path toMemberPath) {
        init();
        Binding b = bindingByTOMemberPath.get(toMemberPath);
        if (b != null) {
            return b.boMemberPath;
        }
        // The binding may have been done by Entity member,
        StringBuilder shortDTOMemberPath = new StringBuilder();
        shortDTOMemberPath.append(toMemberPath.getRootObjectClassName()).append(Path.PATH_SEPARATOR);
        Iterator<String> it = toMemberPath.getPathMembers().iterator();
        while (it.hasNext()) {
            String member = it.next();
            shortDTOMemberPath.append(member).append(Path.PATH_SEPARATOR);
            b = bindingByTOMemberPath.get(new Path(shortDTOMemberPath.toString()));
            if (b != null) {
                StringBuilder shortDBOMemberPath = new StringBuilder(b.boMemberPath.toString());
                while (it.hasNext()) {
                    shortDBOMemberPath.append(it.next()).append(Path.PATH_SEPARATOR);
                }
                return new Path(shortDBOMemberPath.toString());
            }
        }
        return null;
    }

    public TO createTO(BO bo) {
        TO dto = EntityFactory.create(toClass);
        if (copyPrimaryKey) {
            dto.setPrimaryKey(bo.getPrimaryKey());
        }
        copyBOtoTO(bo, dto);
        return dto;
    }

    protected boolean retriveDetachedMember(IEntity dboMember) {
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyBOtoTO(BO bo, TO to) {
        init();
        for (Binding b : binding) {
            IObject dtoM = to.getMember(b.toMemberPath);
            IObject dboM = bo.getMember(b.boMemberPath);

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
                b.binder.copyBOtoTO((IEntity) dboM, (IEntity) dtoM);
            } else if (dboM instanceof ICollection) {
                ((ICollection<IEntity, ?>) dtoM).clear();
                for (IEntity dboMi : (ICollection<IEntity, ?>) dboM) {
                    ((ICollection<IEntity, ?>) dtoM).add(b.binder.createTO(dboMi));
                }
            }
        }
    }

    public BO createBO(TO to) {
        BO dbo = EntityFactory.create(boClass);
        if (copyPrimaryKey) {
            dbo.setPrimaryKey(to.getPrimaryKey());
        }
        copyTOtoBO(to, dbo);
        return dbo;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyTOtoBO(TO to, BO bo) {
        init();
        for (Binding b : binding) {
            IObject dtoM = to.getMember(b.toMemberPath);
            IObject dboM = bo.getMember(b.boMemberPath);

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
                b.binder.copyTOtoBO((IEntity) dtoM, (IEntity) dboM);
            } else if (dtoM instanceof ICollection) {
                ((ICollection<IEntity, ?>) dboM).clear();
                for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                    ((ICollection<IEntity, ?>) dboM).add(b.binder.createBO(dtoMi));
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean updateBO(TO to, BO bo) {
        init();
        boolean updated = false;
        Set<IEntity> processed = new HashSet<IEntity>();
        for (Binding b : binding) {
            IObject dtoM = to.getMember(b.toMemberPath);
            if (dtoM.isNull()) {
                continue;
            }
            IObject dboM = bo.getMember(b.boMemberPath);
            if (b.binder == null) {
                if (dtoM instanceof ICollection) {
                    if (EntityGraph.update((ICollection<IEntity, ?>) dboM, (ICollection<IEntity, ?>) dtoM, processed)) {
                        onUpdateBOmember(to, bo, dboM);
                        updated = true;
                    }
                } else if (dtoM instanceof IEntity) {
                    if (EntityGraph.update((IEntity) dboM, (IEntity) dtoM, processed)) {
                        onUpdateBOmember(to, bo, dboM);
                        updated = true;
                    }
                } else {
                    if (!EqualsHelper.equals(dboM.getValue(), dtoM.getValue())) {
                        dboM.setValue(dtoM.getValue());
                        onUpdateBOmember(to, bo, dboM);
                        updated = true;
                    }
                }
            } else if (dtoM instanceof IEntity) {
                updated |= b.binder.updateBO((IEntity) dtoM, (IEntity) dboM);
            } else if (dtoM instanceof ICollection) {
                ICollection<IEntity, ?> dboMc = (ICollection<IEntity, ?>) dboM;
                for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                    //find
                    boolean found = false;
                    for (IEntity dboMi : dboMc) {
                        if (dtoMi.equals(dboMi) || dtoMi.businessEquals(dboMi)) {
                            found = true;
                            updated |= b.binder.updateBO(dtoMi, dboMi);
                            break;
                        }
                    }

                    if (!found) {
                        ((ICollection<IEntity, ?>) dboM).add(b.binder.createBO(dtoMi));
                        updated = true;
                    }
                }
            } else {
                throw new Error("Unexpected object class " + dtoM.getObjectClass());
            }
        }
        return updated;
    }

    protected void onUpdateBOmember(TO to, BO bo, IObject<?> boM) {

    }

}

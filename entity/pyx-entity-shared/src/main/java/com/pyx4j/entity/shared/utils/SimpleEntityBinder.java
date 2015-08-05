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
 */
package com.pyx4j.entity.shared.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;

/**
 * The target of this class is bidirectional copy of data between two objects.
 *
 * bind() function should be implemented to map members that needs to be copied of one class to another.
 */
//TODO Change order of parameters or order of generics, Binding to use EntityBinder interface
public abstract class SimpleEntityBinder<BO extends IEntity, TO extends IEntity> implements EntityBinder<BO, TO> {

    protected Class<BO> boClass;

    protected Class<TO> toClass;

    protected final boolean preserveType;

    protected final BO boProto;

    protected final TO toProto;

    protected final boolean copyPrimaryKey;

    private boolean bindingInitialized = false;

    private final List<Binding> binding = new Vector<Binding>();

    private final Map<Path, Binding> bindingByTOMemberPath = new HashMap<>();

    private static class Binding {

        final Path toMemberPath;

        final Path boMemberPath;

        @SuppressWarnings("rawtypes")
        SimpleEntityBinder binder;

        @SuppressWarnings("rawtypes")
        ValueConverter valueConverter;

        Binding(IObject<?> dtoMember, IObject<?> dboMember, ValueConverter<?, ?> valueConverter) {
            this(dtoMember, dboMember);
            this.valueConverter = valueConverter;
        }

        Binding(IObject<?> dtoMember, IObject<?> dboMember, @SuppressWarnings("rawtypes") SimpleEntityBinder binder) {
            this(dtoMember, dboMember);
            this.binder = binder;
        }

        Binding(IObject<?> dtoMember, IObject<?> dboMember) {
            toMemberPath = dtoMember.getPath();
            boMemberPath = (dboMember != null) ? dboMember.getPath() : null;
        }

        @Override
        public String toString() {
            return "Binding:" + toMemberPath + " -> " + boMemberPath;
        }

    }

    protected SimpleEntityBinder(Class<BO> boClass, Class<TO> toClass) {
        this(boClass, toClass, false, true);
    }

    /**
     * Preserve Type in binding, Like in PolymorphicEntityBinder but don't need to explicit
     */
    @SuppressWarnings("unchecked")
    protected SimpleEntityBinder(Class<BO> boClass) {
        this(boClass, (Class<TO>) boClass, true, true);
        // Ensure AbstractType
        assert boProto.getEntityMeta().isAnnotationPresent(AbstractEntity.class) : "AbstractEntity expected in preserveType binder; got " + boClass;
    }

    @Override
    public String toString() {
        return "SimpleEntityBinder: " + toClass.getSimpleName() + " -> " + boClass.getSimpleName();
    }

    /**
     * Allow to skip automatic copy of PK, Used to allow duplicated in XML
     */
    protected SimpleEntityBinder(Class<BO> boClass, Class<TO> toClass, boolean copyPrimaryKey) {
        this(boClass, toClass, false, copyPrimaryKey);
    }

    private SimpleEntityBinder(Class<BO> boClass, Class<TO> toClass, boolean preserveType, boolean copyPrimaryKey) {
        this.boClass = boClass;
        this.toClass = toClass;
        this.preserveType = preserveType;
        this.copyPrimaryKey = copyPrimaryKey;

        boProto = EntityFactory.getEntityPrototype(boClass);
        toProto = EntityFactory.getEntityPrototype(toClass);

    }

    @Override
    public final Class<BO> boClass() {
        return boClass;
    }

    @Override
    public final Class<TO> toClass() {
        return toClass;
    }

    protected abstract void bind();

    private boolean canAddToBinding(Path path) {
        return (path == null) || !path.isUndefinedCollectionPath();
    }

    private void addBinding(Binding b) {
        if (canAddToBinding(b.boMemberPath) && canAddToBinding(b.toMemberPath)) {
            binding.add(b);
        }
        Binding previous = bindingByTOMemberPath.put(b.toMemberPath, b);
        if (previous != null) {
            binding.remove(previous);
        }
    }

    @SuppressWarnings("unchecked")
    protected final void bindCompleteObject() {
        bind((Class<IEntity>) boProto.getValueClass(), toProto, boProto);
    }

    /**
     * binds TO member of BO type to BO
     */
    protected final void bindCompleteDtoMember(BO toEntityMember) {
        addBinding(new Binding(toEntityMember.getMember(IEntity.PRIMARY_KEY), boProto.getMember(IEntity.PRIMARY_KEY)));
        for (String memberName : EntityFactory.getEntityMeta(boClass).getMemberNames()) {
            addBinding(new Binding(toEntityMember.getMember(memberName), boProto.getMember(memberName)));
        }
    }

    protected final <TYPE extends Serializable> void bind(IPrimitive<TYPE> toMember, IPrimitive<TYPE> boMember) {
        addBinding(new Binding(toMember, boMember));
    }

    protected final <TYPE extends IEntity> void bind(IList<TYPE> toMember, IList<TYPE> boMember) {
        addBinding(new Binding(toMember, boMember));
    }

    protected final <TBO extends IEntity, TTO extends TBO> void bind(TTO toMember, TBO boMember) {
        addBinding(new Binding(toMember, boMember));
    }

    protected final <TBO extends IEntity, TTO extends IEntity> void bind(TTO toMember, TBO boMember, SimpleEntityBinder<TBO, TTO> binder) {
        addBinding(new Binding(toMember, boMember, binder));
    }

    protected final <TDBO extends IEntity, TDTO extends IEntity> void bind(IList<TDTO> toMember, IList<TDBO> boMember, SimpleEntityBinder<TDBO, TDTO> binder) {
        addBinding(new Binding(toMember, boMember, binder));
    }

    protected final <F extends IEntity, S extends F, D extends F> void bind(Class<F> fragmentClass, S dto, D dbo) {
        addBinding(new Binding(dto.getMember(IEntity.PRIMARY_KEY), dbo.getMember(IEntity.PRIMARY_KEY)));
        for (String memberName : EntityFactory.getEntityMeta(fragmentClass).getMemberNames()) {
            addBinding(new Binding(dto.getMember(memberName), dbo.getMember(memberName)));
        }
    }

    @Override
    public <TYPE extends Serializable> void addValueConverter(IPrimitive<TYPE> toMember, ValueConverter<BO, TYPE> valueConverter) {
        addBinding(new Binding(toMember, null, valueConverter));
    }

    @Override
    public <TYPE extends IEntity> void addValueConverter(TYPE toMember, ValueConverter<BO, TYPE> valueConverter) {
        addBinding(new Binding(toMember, null, valueConverter));
    }

    private void init() {
        if (!bindingInitialized) {
            synchronized (binding) {
                if (!bindingInitialized) {
                    bind();
                    bindingInitialized = true;
                }
            }
        }
    }

    @Override
    public Path getBoundBOMemberPath(Path toMemberPath) {
        init();
        Binding b = bindingByTOMemberPath.get(toMemberPath);
        if (b != null) {
            return b.boMemberPath;
        }
        // The binding may have been done by full member of type Entity
        // Find longest bound path.
        for (int l = toMemberPath.getPathMembers().size() - 1; l > 0; l--) {
            List<String> pathMembers = toMemberPath.getPathMembers().subList(0, l);
            Path toMemberPathShort = new Path(toClass, pathMembers);
            b = bindingByTOMemberPath.get(toMemberPathShort);
            if (b != null) {
                return new Path(b.boMemberPath, toMemberPath.getPathMembers().subList(l, toMemberPath.getPathMembers().size()));
            }
        }
        return null;
    }

    @Override
    public TO createTO(BO bo, BindingContext context) {
        @SuppressWarnings("unchecked")
        TO to = (TO) context.get(bo);
        if (to != null) {
            return to;
        }

        if (preserveType) {
            @SuppressWarnings("unchecked")
            Class<TO> polymorphicToClass = (Class<TO>) bo.getInstanceValueClass();
            to = EntityFactory.create(polymorphicToClass);
        } else {
            to = EntityFactory.create(toClass);
        }
        if (copyPrimaryKey) {
            to.setPrimaryKey(bo.getPrimaryKey());
        }

        if (bo.getAttachLevel() != AttachLevel.Attached) {
            to.setAttachLevel(bo.getAttachLevel());
        }

        context.put(bo, to);
        // TODO pass context as argument
        copyBOtoTO(bo, to, context);
        return to;
    }

    protected boolean retriveDetachedMember(IEntity dboMember) {
        return false;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyBOtoTO(BO bo, TO to, BindingContext context) {
        init();
        to.setAttachLevel(AttachLevel.Attached);
        bo = bo.cast();
        to = to.cast();
        for (Binding b : binding) {
            IObject dtoM = to.getMember(b.toMemberPath);
            if (b.boMemberPath == null) {
                Object value = b.valueConverter.convertValue(bo);
                if (dtoM instanceof IEntity) {
                    value = ((IEntity) value).getValue();
                }
                to.setValue(b.toMemberPath, (Serializable) value);
            } else {
                IObject dboM = bo.getMember(b.boMemberPath);

                // Assert that all data has been retrieved
                if (dboM.isValueDetached() && !dtoM.getMeta().isDetached()) {
                    IEntity retrive;
                    if (dboM instanceof IEntity) {
                        retrive = (IEntity) dboM;
                    } else {
                        retrive = dboM.getOwner();
                    }
                    if (!retriveDetachedMember(retrive)) {
                        throw new Error("Copying detached entity " + retrive.getDebugExceptionInfoString());
                    }
                }

                // Bypass copy if it was already copied.
                if (dboM instanceof IEntity) {
                    IEntity processed = context.get((IEntity) dboM);
                    if (processed != null) {
                        ((IEntity) dtoM).set(processed);
                        continue;
                    }
                }

                if (dboM.getAttachLevel() == AttachLevel.Detached) {
                    //TODO slava, considre refactoring in order to merge functionality with copyTOtoBO
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
                    if (((IEntity) dboM).isNull()) {
                        ((IEntity) dtoM).set(null);
                    } else {
                        ((IEntity) dtoM).set(b.binder.createTO((IEntity) dboM, context));
                    }
                } else if (dboM instanceof ICollection) {
                    ((ICollection<IEntity, ?>) dtoM).clear();
                    for (IEntity dboMi : (ICollection<IEntity, ?>) dboM) {
                        ((ICollection<IEntity, ?>) dtoM).add(b.binder.createTO(dboMi, context));
                    }
                }
            }
        }
    }

    @Override
    public BO createBO(TO to, BindingContext context) {
        @SuppressWarnings("unchecked")
        BO bo = (BO) context.get(to);
        if (bo != null) {
            return bo;
        }

        if (preserveType) {
            @SuppressWarnings("unchecked")
            Class<BO> polymorphicBoClass = (Class<BO>) to.getInstanceValueClass();
            bo = EntityFactory.create(polymorphicBoClass);
        } else {
            bo = EntityFactory.create(boClass);
        }
        if (copyPrimaryKey) {
            bo.setPrimaryKey(to.getPrimaryKey());
        }

        if (to.getAttachLevel() != AttachLevel.Attached) {
            bo.setAttachLevel(to.getAttachLevel());
        }

        context.put(to, bo);
        copyTOtoBO(to, bo, context);
        return bo;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyTOtoBO(TO to, BO bo, BindingContext context) {
        init();
        bo = bo.cast();
        to = to.cast();
        for (Binding b : binding) {
            IObject dtoM = to.getMember(b.toMemberPath);
            if (b.boMemberPath != null) {
                IObject dboM = bo.getMember(b.boMemberPath);

                // Bypass copy if it was already copied.
                if (dboM instanceof IEntity) {
                    IEntity processed = context.get((IEntity) dtoM);
                    if (processed != null) {
                        ((IEntity) dboM).set(processed);
                        continue;
                    }
                }

                if (dtoM.getAttachLevel() == AttachLevel.Detached) {
                    if (dboM.getAttachLevel() != AttachLevel.Detached) {
                        dboM.setAttachLevel(AttachLevel.Detached);
                    }
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
                    if (((IEntity) dtoM).isNull()) {
                        ((IEntity) dboM).set(null);
                        continue;
                    } else {
                        ((IEntity) dboM).set(b.binder.createBO((IEntity) dtoM, context));
                    }
                } else if (dtoM instanceof ICollection) {
                    ((ICollection<IEntity, ?>) dboM).clear();
                    for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                        ((ICollection<IEntity, ?>) dboM).add(b.binder.createBO(dtoMi, context));
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean updateBO(TO to, BO bo, BindingContext context) {
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
                updated |= b.binder.updateBO((IEntity) dtoM, (IEntity) dboM, context);
            } else if (dtoM instanceof ICollection) {
                ICollection<IEntity, ?> dboMc = (ICollection<IEntity, ?>) dboM;
                for (IEntity dtoMi : (ICollection<IEntity, ?>) dtoM) {
                    //find
                    boolean found = false;
                    for (IEntity dboMi : dboMc) {
                        if (dtoMi.equals(dboMi) || dtoMi.businessEquals(dboMi)) {
                            found = true;
                            updated |= b.binder.updateBO(dtoMi, dboMi, context);
                            break;
                        }
                    }

                    if (!found) {
                        ((ICollection<IEntity, ?>) dboM).add(b.binder.createBO(dtoMi, context));
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

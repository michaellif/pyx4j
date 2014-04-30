package com.pyx4j.entity.shared;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;

/**
 * IEntity wrapper to hold IList; can be useful for custom components
 * that bind to IList and utilize inner CForm, such as CImageSlider.
 */
@Transient
public interface IListWrapper<E extends IEntity> extends IEntity {

    IList<E> items();
}

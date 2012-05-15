package com.pyx4j.entity.client.ui;

import com.pyx4j.entity.client.IDecorator;
import com.pyx4j.entity.shared.IEntity;

public interface ICollapsableDecorator<E extends IEntity> extends IDecorator<CEntityCollapsableViewer<E>> {
    @Override
    public void setComponent(CEntityCollapsableViewer<E> viewer);

}

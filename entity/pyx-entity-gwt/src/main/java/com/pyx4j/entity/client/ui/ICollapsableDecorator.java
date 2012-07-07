package com.pyx4j.entity.client.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.decorators.IDecorator;

public interface ICollapsableDecorator<E extends IEntity> extends IDecorator<CEntityCollapsableViewer<E>> {
    @Override
    public void setComponent(CEntityCollapsableViewer<E> viewer);

}

package com.pyx4j.forms.client.ui;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;

public class NCollectionBox<E> extends NFocusField<Collection<E>, INativeListBox<E>, CCollectionBox<E>, HTML> implements INativeListBox<E> {

    private int visibleItemCount;

    public NCollectionBox(CCollectionBox<E> cComponent) {
        super(cComponent);
        visibleItemCount = 10;
        refreshOptions();
    }

    @Override
    public void setNativeValue(Collection<E> value) {
        if (isViewable()) {
            getViewer().setHTML(getCComponent().format(value));
        } else {
            getEditor().setNativeValue(value);
        }
    }

    @Override
    public List<E> getNativeValue() {
        if (isViewable()) {
            throw new Error("Error: Value requested in view-mode");
        } else {
            return getEditor().getNativeValue();
        }
    }

    @Override
    protected INativeListBox<E> createEditor() {
        INativeListBox<E> editor = null;
        switch (getCComponent().getSelectionMode()) {
        case SINGLE_PANEL:
            editor = new NativeListBox<E>(visibleItemCount, this);
            break;
        case TWO_PANEL:
            editor = new NativeListSelectionComposite<E>(visibleItemCount, this);
            break;
        }
        return editor;
    }

    @Override
    public String getItemName(E item) {
        return getCComponent().getItemName(item);
    }

    @Override
    public void onNativeValueChange(Collection<E> values) {
        getCComponent().setValue(values);
    }

    @Override
    public String itemCannotBeRemovedMessage(E item) {
        List<E> required = getCComponent().getRequiredValues();
        if (required != null && required.contains(item)) {
            return getCComponent().getValidationMessage(item);
        } else {
            return null;
        }
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    public void setVisibleItemCount(int count) {
        visibleItemCount = count;
        getEditor().setVisibleItemCount(count);
    }

    public int getVisibleItemCount() {
        return visibleItemCount;
    }

    public void refreshOptions() {
        setOptions(getCComponent().getOptions());
    }

    @Override
    public void setOptions(Collection<E> options) {
        if (getEditor() != null) {
            getEditor().setOptions(options);
        }
    }

    @Override
    public Comparator<E> getComparator() {
        return getCComponent().getComparator();
    }
}

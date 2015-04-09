package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;

import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.widgets.client.TextBox;

public class NPersonalIdentityField<E extends IPersonalIdentity> extends NValueBoxBase<E, TextBox<E>, CPersonalIdentityField<E>> {

    public NPersonalIdentityField(CPersonalIdentityField<E> cComponent) {
        super(cComponent);
    }

    @Override
    protected TextBox<E> createEditor() {
        return new TextBox<E>();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                // clear value for new input
                if (isEditable() && getCComponent().getValue() != null && !getCComponent().getValue().obfuscatedNumber().isNull()) {
                    getEditor().setValue(null);
                }
            }
        });
        getEditor().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                // this will re-format native value in case it's been changed
                // as CComponent#update() will only do that if value is valid
                if (isEditable()) {
                    try {
                        E nativeValue = getNativeValue();
                        if (nativeValue != null && !nativeValue.isEmpty()) {
                            setNativeValue(nativeValue);
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        });
    }

    @Override
    protected void onViewerInit() {
        getCComponent().postprocess();
        super.onViewerInit();
    }

}

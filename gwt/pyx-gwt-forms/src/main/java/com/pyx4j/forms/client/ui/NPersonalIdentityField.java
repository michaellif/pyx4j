package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;

import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.widgets.client.ToggleButton;

public class NPersonalIdentityField<E extends IPersonalIdentity> extends NTextBox<E> {

    public NPersonalIdentityField(CPersonalIdentityField<E> cComponent) {
        super(cComponent);
    }

    public NPersonalIdentityField(CPersonalIdentityField<E> cComponent, ToggleButton triggerButton) {
        super(cComponent, triggerButton);
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                // clear value for new input
                if (isEditable() && getCComponent().getValue() != null && !getCComponent().getValue().obfuscatedNumber().isNull()) {
                    getEditor().setText("");
                }
            }
        });
        getEditor().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                // this will re-format native value in case it's been changed
                // as CComponent#update() only does that if value INSTANCE has changed (!?)
                if (isEditable() && getCComponent().getValue() != null) {
                    setNativeValue(getCComponent().getValue());
                }
            }
        });
    }

    @Override
    protected void onViewerInit() {
        ((CPersonalIdentityField) getCComponent()).postprocess();
        super.onViewerInit();
    }
}

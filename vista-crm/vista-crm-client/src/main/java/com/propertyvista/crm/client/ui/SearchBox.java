package com.propertyvista.crm.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.propertyvista.crm.client.resources.CrmImages;

import com.pyx4j.widgets.client.style.IStyleSuffix;

/**
 * 
 * @author vadims
 *         TODO finish implementation
 * 
 */
public class SearchBox extends Composite implements ClickHandler, KeyDownHandler, KeyUpHandler, KeyPressHandler {

    public static final String DEFAULT_STYLE_NAME = "pyx4j-searchbox";

    public static enum StyleSuffix implements IStyleSuffix {
        Text, Trigger
    }

    private final FlowPanel container;

    private final TextBox searchfld;

    private final Image trigger;

    public SearchBox() {
        container = new FlowPanel();
        Style st = container.getElement().getStyle();
        st.setDisplay(Display.INLINE_BLOCK);
        st.setBorderStyle(BorderStyle.NONE);

        searchfld = new TextBox();
        searchfld.setStyleName(DEFAULT_STYLE_NAME + StyleSuffix.Text);
        searchfld.setWidth("12em");
        st = searchfld.getElement().getStyle();
        st.setBorderStyle(BorderStyle.NONE);

        trigger = new Image(CrmImages.INSTANCE.search());
        st = trigger.getElement().getStyle();
        st.setBorderStyle(BorderStyle.NONE);
        //   trigger.setSize("16px", "16px");
        trigger.setStyleName(DEFAULT_STYLE_NAME + StyleSuffix.Trigger);
        trigger.getElement().getStyle().setCursor(Cursor.POINTER);

        container.add(searchfld);
        container.add(trigger);
        container.setWidth("13em");
        initWidget(container);
        setStyleName(DEFAULT_STYLE_NAME);
    }

    public void setSize() {
        //TODO implement
    }

    public SearchBox(String tooltip) {
        this();
        container.setTitle(tooltip);
    }

    @Override
    public void onClick(ClickEvent event) {

        Object sender = event.getSource();
        if (sender == trigger)
            trigger.fireEvent(event);
        else if (sender == searchfld)
            searchfld.fireEvent(event);
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        // TODO Auto-generated method stub

    }

}

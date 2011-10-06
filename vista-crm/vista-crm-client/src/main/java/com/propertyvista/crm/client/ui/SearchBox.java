package com.propertyvista.crm.client.ui;

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

import com.pyx4j.widgets.client.style.IStyleName;

import com.propertyvista.crm.client.resources.CrmImages;

/**
 * 
 * @author vadims
 *         TODO finish implementation
 * 
 */
public class SearchBox extends Composite implements ClickHandler, KeyDownHandler, KeyUpHandler, KeyPressHandler {

    public static final String DEFAULT_STYLE_NAME = "pyx4j-searchbox";

    public static enum StyleSuffix implements IStyleName {
        Text, Trigger
    }

    private final FlowPanel container;

    private final TextBox searchfld;

    private final Image trigger;

    public SearchBox() {
        container = new FlowPanel();
        container.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        searchfld = new TextBox();
        searchfld.setStyleName(DEFAULT_STYLE_NAME + StyleSuffix.Text);
        searchfld.setWidth("8em");

        trigger = new Image(CrmImages.INSTANCE.search());
        trigger.setStyleName(DEFAULT_STYLE_NAME + StyleSuffix.Trigger);
        trigger.getElement().getStyle().setCursor(Cursor.POINTER);

        container.add(searchfld);
        container.add(trigger);
        container.setWidth("10em");
        initWidget(container);
        setStyleName(DEFAULT_STYLE_NAME);
    }

    @Override
    public void setWidth(String width) {
        //TODO finish
        container.setWidth(width);
        searchfld.setWidth("85%");

/*
 * int cw = container.getElement().getClientWidth();
 * int tw = trigger.getElement().getClientWidth();
 * if (cw > 4) {
 * System.out.println(cw + "  px " + tw);
 * searchfld.setWidth(String.valueOf(cw - tw - 2) + "px");
 * }
 */

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

package com.pyx4j.examples.site.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PhotoViewer extends Composite implements ClickHandler, LoadHandler {

	private final static Image loadingImage = new Image(
			"images/blanksearching.gif");

	private final static Image nextButton = new Image("images/forward.gif");

	private final static Image prevButton = new Image("images/back.gif");

	private String[] sImages;

	private int curImage;

	private Image image = new Image();

	public PhotoViewer(String[] sImages) {
		this.sImages = sImages;
		image.addLoadHandler(this);
		prevButton.addClickHandler(this);
		nextButton.addClickHandler(this);
		DOM.setStyleAttribute(prevButton.getElement(), "cursor", "pointer");
		DOM.setStyleAttribute(prevButton.getElement(), "cursor", "hand");
		DOM.setStyleAttribute(nextButton.getElement(), "cursor", "pointer");
		DOM.setStyleAttribute(nextButton.getElement(), "cursor", "hand");

		DockPanel topPanel = new DockPanel();
		topPanel.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
		topPanel.add(prevButton, DockPanel.WEST);
		topPanel.add(nextButton, DockPanel.EAST);
		topPanel.add(loadingImage, DockPanel.CENTER);

		VerticalPanel panel = new VerticalPanel();
		panel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		panel.add(topPanel);
		panel.add(image);

		panel.setWidth("100%");
		initWidget(panel);

		loadImage(0);
	}

	@Override
	public void onClick(ClickEvent event) {
		Element sender = event.getRelativeElement();
		if (sender == prevButton.getElement()) {
			loadImage(curImage - 1);
		} else if (sender == nextButton.getElement()) {
			loadImage(curImage + 1);
		}
	}

	public void onError(Widget sender) {
	}

	@Override
	public void onLoad(LoadEvent event) {
		loadingImage.setUrl("images/blanksearching.gif");
	}

	public void onShow() {
	}

	private void loadImage(int index) {
		if (index < 0)
			index = sImages.length - 1;
		else if (index > sImages.length - 1)
			index = 0;

		curImage = index;
		loadingImage.setUrl("images/searching.gif");
		image.setUrl(sImages[curImage]);
		image.setSize("500px", "375px");
	}

}

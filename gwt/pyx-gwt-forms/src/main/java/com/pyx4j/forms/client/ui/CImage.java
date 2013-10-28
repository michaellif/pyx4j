package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public class CImage<E extends IFile> extends CField<E, NImage<E>> {

    private Image placeholder;

    private Dimension imageSize;

    private final UploadService<?, E> service;

    private final FileURLBuilder<E> fileUrlBuilder;

    public CImage(UploadService<?, E> service, FileURLBuilder<E> fileURLBuilder) {
        this.service = service;
        this.fileUrlBuilder = fileURLBuilder;
        this.imageSize = new Dimension(250, 250);
        setNativeWidget(new NImage<E>(this));
    }

    public void setThumbnailPlaceholder(Image placeholder) {
        this.placeholder = placeholder;
        getWidget().reset();
    }

    public Image getThumbnailPlaceholder() {
        return placeholder;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        getWidget().setScaleMode(scaleMode);
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        getWidget().resizeToFit();
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    public String getImageUrl(E file) {
        if (file == null || file.isNull() || fileUrlBuilder == null) {
            return null;
        } else {
            return fileUrlBuilder.getUrl(file);
        }
    }

    UploadService<?, E> getUploadService() {
        return service;
    }

}

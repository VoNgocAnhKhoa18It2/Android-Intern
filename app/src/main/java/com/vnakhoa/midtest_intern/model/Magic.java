package com.vnakhoa.midtest_intern.model;

import java.io.Serializable;

public class Magic implements Serializable {
    private String urlMagic;
    private boolean isOpen;

    public Magic(String urlMagic, boolean isOpen) {
        this.urlMagic = urlMagic;
        this.isOpen = isOpen;
    }

    public String getUrlMagic() {
        return urlMagic;
    }

    public void setUrlMagic(String urlMagic) {
        this.urlMagic = urlMagic;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}

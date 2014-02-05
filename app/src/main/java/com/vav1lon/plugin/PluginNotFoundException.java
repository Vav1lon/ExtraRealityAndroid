package com.vav1lon.plugin;

public class PluginNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PluginNotFoundException() {
        super();
    }

    public PluginNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public PluginNotFoundException(String message) {
        super(message);
    }

}

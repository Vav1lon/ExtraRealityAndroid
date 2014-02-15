package com.vav1lon.plugin;

import com.vav1lon.plugin.connection.BootStrapActivityConnection;
import com.vav1lon.plugin.connection.DataHandlerServiceConnection;
import com.vav1lon.plugin.connection.MarkerServiceConnection;

public enum PluginType {
    /**
     * A bootstrap plugin that will be loaded first I.E. a splashscreen
     */
    BOOTSTRAP_PHASE_1() {
        public String getActionName() {
            return "com.vav1lon.plugin.bootstrap1";
        }

        public PluginConnection getPluginConnection() {
            PluginConnection pluginConnection = new BootStrapActivityConnection();
            pluginConnection.setPluginType(this);
            return pluginConnection;
        }

        public Loader getLoader() {
            return Loader.Activity;
        }
    },
    /**
     * A bootstrap plugin that will be loaded after the fist boostrap phase
     */
    BOOTSTRAP_PHASE_2() {
        public String getActionName() {
            return "com.vav1lon.plugin.bootstrap2";
        }

        public PluginConnection getPluginConnection() {
            PluginConnection pluginConnection = new BootStrapActivityConnection();
            pluginConnection.setPluginType(this);
            return pluginConnection;
        }

        public Loader getLoader() {
            return Loader.Activity;
        }
    },
    /**
     * A plugin that returns a custom datasource
     */
    DATASELECTOR() {
        public String getActionName() {
            return "com.vav1lon.plugin.dataselector";
        }

        public PluginConnection getPluginConnection() {
            PluginConnection pluginConnection = new BootStrapActivityConnection();
            pluginConnection.setPluginType(this);
            return pluginConnection;
        }

        public Loader getLoader() {
            return Loader.Activity;
        }
    },
    /**
     * A plugin that contains a custom marker
     */
    MARKER() {
        public String getActionName() {
            return "com.vav1lon.plugin.marker";
        }

        public PluginConnection getPluginConnection() {
            PluginConnection pluginConnection = new MarkerServiceConnection();
            pluginConnection.setPluginType(this);
            return pluginConnection;
        }

        public Loader getLoader() {
            return Loader.Service;
        }
    },
    /**
     * A plugin that handles the conversion of data to marker
     */
    DATAHANDLER() {
        public String getActionName() {
            return "com.vav1lon.plugin.datahandler";
        }

        public PluginConnection getPluginConnection() {
            PluginConnection pluginConnection = new DataHandlerServiceConnection();
            pluginConnection.setPluginType(this);
            return pluginConnection;
        }

        public Loader getLoader() {
            return Loader.Service;
        }
    };

    /**
     * The package name to find the plugin
     */
    public abstract String getActionName();

    /**
     * The loader to know how to handle a plugin (activity / service)
     */
    public abstract Loader getLoader();

    /**
     * Returns the instance of an activity plugin loader that can load activity plugins
     */
    public abstract PluginConnection getPluginConnection();
}

enum Loader {
    Activity,
    Service
}

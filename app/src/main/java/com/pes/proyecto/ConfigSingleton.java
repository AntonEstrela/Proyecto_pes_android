package com.pes.proyecto;

class ConfigSingleton {
    private static final ConfigSingleton ourInstance = new ConfigSingleton();

    static ConfigSingleton getInstance() {
        return ourInstance;
    }

    private ConfigSingleton() {
        ServerAddress = "http://192.168.0.16:9000";
    }
    public String ServerAddress;
}

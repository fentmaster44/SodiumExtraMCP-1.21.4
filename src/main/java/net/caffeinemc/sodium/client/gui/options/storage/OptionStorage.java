package net.caffeinemc.sodium.client.gui.options.storage;

public interface OptionStorage<T> {
    T getData();

    void save();
}

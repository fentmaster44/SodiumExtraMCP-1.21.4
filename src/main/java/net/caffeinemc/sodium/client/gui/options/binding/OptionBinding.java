package net.caffeinemc.sodium.client.gui.options.binding;

public interface OptionBinding<S, T> {
    void setValue(S storage, T value);

    T getValue(S storage);
}

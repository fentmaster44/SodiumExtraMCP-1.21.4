package net.caffeinemc.sodium.client.gui.options;

import net.caffeinemc.sodium.client.gui.options.control.Control;
import net.caffeinemc.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;
import java.util.Collection;

public interface Option<T> {
    Component getName();

    Component getTooltip();

    OptionImpact getImpact();

    Control<T> getControl();

    T getValue();

    void setValue(T value);

    void reset();

    OptionStorage<?> getStorage();

    boolean isAvailable();

    boolean hasChanged();

    void applyChanges();

    Collection<OptionFlag> getFlags();
}

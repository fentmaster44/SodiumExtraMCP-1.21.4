package net.caffeinemc.sodium.client.gui.options.control;

import net.caffeinemc.sodium.client.gui.options.Option;
import net.caffeinemc.sodium.client.util.Dim2i;

public interface Control<T> {
    Option<T> getOption();

    ControlElement<T> createElement(Dim2i dim);

    int getMaxWidth();
}

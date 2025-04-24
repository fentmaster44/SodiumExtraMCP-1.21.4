package net.caffeinemc.sodium.client.console.message;

public record Message(MessageLevel level, String text, boolean translated, double duration) {

}

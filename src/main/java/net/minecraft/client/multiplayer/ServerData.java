package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.util.PngInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_ICON_SIZE = 1024;
    public String name;
    public String ip;
    public Component status;
    public Component motd;
    @Nullable
    public ServerStatus.Players players;
    public long ping;
    public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
    public Component version = Component.literal(SharedConstants.getCurrentVersion().getName());
    public List<Component> playerList = Collections.emptyList();
    private ServerData.ServerPackStatus packStatus = ServerData.ServerPackStatus.PROMPT;
    @Nullable
    private byte[] iconBytes;
    private ServerData.Type type;
    private ServerData.State state = ServerData.State.INITIAL;

    public ServerData(String p_105375_, String p_105376_, ServerData.Type p_297678_) {
        this.name = p_105375_;
        this.ip = p_105376_;
        this.type = p_297678_;
    }

    public CompoundTag write() {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("name", this.name);
        compoundtag.putString("ip", this.ip);
        if (this.iconBytes != null) {
            compoundtag.putString("icon", Base64.getEncoder().encodeToString(this.iconBytes));
        }

        if (this.packStatus == ServerData.ServerPackStatus.ENABLED) {
            compoundtag.putBoolean("acceptTextures", true);
        } else if (this.packStatus == ServerData.ServerPackStatus.DISABLED) {
            compoundtag.putBoolean("acceptTextures", false);
        }

        return compoundtag;
    }

    public ServerData.ServerPackStatus getResourcePackStatus() {
        return this.packStatus;
    }

    public void setResourcePackStatus(ServerData.ServerPackStatus p_105380_) {
        this.packStatus = p_105380_;
    }

    public static ServerData read(CompoundTag p_105386_) {
        ServerData serverdata = new ServerData(p_105386_.getString("name"), p_105386_.getString("ip"), ServerData.Type.OTHER);
        if (p_105386_.contains("icon", 8)) {
            try {
                byte[] abyte = Base64.getDecoder().decode(p_105386_.getString("icon"));
                serverdata.setIconBytes(validateIcon(abyte));
            } catch (IllegalArgumentException illegalargumentexception) {
                LOGGER.warn("Malformed base64 server icon", (Throwable)illegalargumentexception);
            }
        }

        if (p_105386_.contains("acceptTextures", 99)) {
            if (p_105386_.getBoolean("acceptTextures")) {
                serverdata.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
            } else {
                serverdata.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
            }
        } else {
            serverdata.setResourcePackStatus(ServerData.ServerPackStatus.PROMPT);
        }

        return serverdata;
    }

    @Nullable
    public byte[] getIconBytes() {
        return this.iconBytes;
    }

    public void setIconBytes(@Nullable byte[] p_272760_) {
        this.iconBytes = p_272760_;
    }

    public boolean isLan() {
        return this.type == ServerData.Type.LAN;
    }

    public boolean isRealm() {
        return this.type == ServerData.Type.REALM;
    }

    public ServerData.Type type() {
        return this.type;
    }

    public void copyNameIconFrom(ServerData p_233804_) {
        this.ip = p_233804_.ip;
        this.name = p_233804_.name;
        this.iconBytes = p_233804_.iconBytes;
    }

    public void copyFrom(ServerData p_105382_) {
        this.copyNameIconFrom(p_105382_);
        this.setResourcePackStatus(p_105382_.getResourcePackStatus());
        this.type = p_105382_.type;
    }

    public ServerData.State state() {
        return this.state;
    }

    public void setState(ServerData.State p_336358_) {
        this.state = p_336358_;
    }

    @Nullable
    public static byte[] validateIcon(@Nullable byte[] p_301776_) {
        if (p_301776_ != null) {
            try {
                PngInfo pnginfo = PngInfo.fromBytes(p_301776_);
                if (pnginfo.width() <= 1024 && pnginfo.height() <= 1024) {
                    return p_301776_;
                }
            } catch (IOException ioexception) {
                LOGGER.warn("Failed to decode server icon", (Throwable)ioexception);
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum ServerPackStatus {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Component name;

        private ServerPackStatus(final String p_105399_) {
            this.name = Component.translatable("addServer.resourcePack." + p_105399_);
        }

        public Component getName() {
            return this.name;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum State {
        INITIAL,
        PINGING,
        UNREACHABLE,
        INCOMPATIBLE,
        SUCCESSFUL;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Type {
        LAN,
        REALM,
        OTHER;
    }
}
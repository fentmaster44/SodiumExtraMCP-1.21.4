package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.caffeinemc.sodium.api.util.ColorABGR;
import net.caffeinemc.sodium.api.util.ColorARGB;
import net.caffeinemc.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.sodium.api.vertex.format.common.ColorVertex;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class LevelLoadingScreen extends Screen {
    private static final long NARRATION_DELAY_MS = 2000L;
    private final StoringChunkProgressListener progressListener;
    private long lastNarration = -1L;
    private boolean done;
    private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap<>(), p_280803_ -> {
        p_280803_.defaultReturnValue(0);
        p_280803_.put(ChunkStatus.EMPTY, 5526612);
        p_280803_.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
        p_280803_.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        p_280803_.put(ChunkStatus.BIOMES, 8434258);
        p_280803_.put(ChunkStatus.NOISE, 13750737);
        p_280803_.put(ChunkStatus.SURFACE, 7497737);
        p_280803_.put(ChunkStatus.CARVERS, 3159410);
        p_280803_.put(ChunkStatus.FEATURES, 2213376);
        p_280803_.put(ChunkStatus.INITIALIZE_LIGHT, 13421772);
        p_280803_.put(ChunkStatus.LIGHT, 16769184);
        p_280803_.put(ChunkStatus.SPAWN, 15884384);
        p_280803_.put(ChunkStatus.FULL, 16777215);
    });

    private static Reference2IntOpenHashMap<ChunkStatus> STATUS_TO_COLOR_FAST;

    // Sodium
    private static final int NULL_STATUS_COLOR = ColorABGR.pack(0, 0, 0, 0xFF);
    private static final int DEFAULT_STATUS_COLOR = ColorARGB.pack(0, 0x11, 0xFF, 0xFF);

    public LevelLoadingScreen(StoringChunkProgressListener p_96143_) {
        super(GameNarrator.NO_TITLE);
        this.progressListener = p_96143_;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public void removed() {
        this.done = true;
        this.triggerImmediateNarration(true);
    }

    @Override
    protected void updateNarratedWidget(NarrationElementOutput p_169312_) {
        if (this.done) {
            p_169312_.add(NarratedElementType.TITLE, Component.translatable("narrator.loading.done"));
        } else {
            p_169312_.add(NarratedElementType.TITLE, this.getFormattedProgress());
        }
    }

    private Component getFormattedProgress() {
        return Component.translatable("loading.progress", Mth.clamp(this.progressListener.getProgress(), 0, 100));
    }

    @Override
    public void render(GuiGraphics p_283534_, int p_96146_, int p_96147_, float p_96148_) {
        super.render(p_283534_, p_96146_, p_96147_, p_96148_);
        long i = Util.getMillis();
        if (i - this.lastNarration > 2000L) {
            this.lastNarration = i;
            this.triggerImmediateNarration(true);
        }

        int j = this.width / 2;
        int k = this.height / 2;
        renderChunks(p_283534_, this.progressListener, j, k, 2, 0);
        int l = this.progressListener.getDiameter() + 9 + 2;
        p_283534_.drawCenteredString(this.font, this.getFormattedProgress(), j, k - l, 16777215);
    }

    // Sodium
    public static void renderChunks(GuiGraphics graphics, StoringChunkProgressListener listener, int mapX, int mapY, int mapScale, int mapPadding) {
        Matrix4f pose = graphics.pose()
                .last()
                .pose();

        graphics.drawSpecial((bufferSource -> {
            var writer = VertexBufferWriter.of(bufferSource.getBuffer(RenderType.gui()));

            sodium$drochingMap(listener, mapX, mapY, mapScale, mapPadding, writer, pose);
        }));
    }

    private static void sodium$drochingMap(StoringChunkProgressListener listener,
                                           int mapX,
                                           int mapY,
                                           int mapScale,
                                           int mapPadding,
                                           VertexBufferWriter writer,
                                           Matrix4f pose) {

        if (STATUS_TO_COLOR_FAST == null) {
            STATUS_TO_COLOR_FAST = new Reference2IntOpenHashMap<>(COLORS.size());
            STATUS_TO_COLOR_FAST.put(null, NULL_STATUS_COLOR);
            COLORS.object2IntEntrySet()
                    .forEach(entry -> STATUS_TO_COLOR_FAST.put(entry.getKey(), ColorARGB.toABGR(entry.getIntValue(), 0xFF)));
        }

        int centerSize = listener.getFullDiameter();
        int size = listener.getDiameter();

        int tileSize = mapScale + mapPadding;

        if (mapPadding != 0) {
            int mapRenderCenterSize = centerSize * tileSize - mapPadding;
            int radius = mapRenderCenterSize / 2 + 1;

            addRect(writer, pose, mapX - radius, mapY - radius, mapX - radius + 1, mapY + radius, DEFAULT_STATUS_COLOR);
            addRect(writer, pose, mapX + radius - 1, mapY - radius, mapX + radius, mapY + radius, DEFAULT_STATUS_COLOR);
            addRect(writer, pose, mapX - radius, mapY - radius, mapX + radius, mapY - radius + 1, DEFAULT_STATUS_COLOR);
            addRect(writer, pose, mapX - radius, mapY + radius - 1, mapX + radius, mapY + radius, DEFAULT_STATUS_COLOR);
        }

        int mapRenderSize = size * tileSize - mapPadding;
        int mapStartX = mapX - mapRenderSize / 2;
        int mapStartY = mapY - mapRenderSize / 2;

        ChunkStatus prevStatus = null;
        int prevColor = NULL_STATUS_COLOR;

        for (int x = 0; x < size; ++x) {
            int tileX = mapStartX + x * tileSize;

            for (int z = 0; z < size; ++z) {
                int tileY = mapStartY + z * tileSize;

                ChunkStatus status = listener.getStatus(x, z);
                int color;

                if (prevStatus == status) {
                    color = prevColor;
                } else {
                    color = STATUS_TO_COLOR_FAST.getInt(status);

                    prevStatus = status;
                    prevColor = color;
                }

                addRect(writer, pose, tileX, tileY, tileX + mapScale, tileY + mapScale, color);
            }
        }
    }

    private static void addRect(VertexBufferWriter writer, Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(4 * ColorVertex.STRIDE);
            long ptr = buffer;

            ColorVertex.put(ptr, matrix, x1, y2, 0, color);
            ptr += ColorVertex.STRIDE;

            ColorVertex.put(ptr, matrix, x2, y2, 0, color);
            ptr += ColorVertex.STRIDE;

            ColorVertex.put(ptr, matrix, x2, y1, 0, color);
            ptr += ColorVertex.STRIDE;

            ColorVertex.put(ptr, matrix, x1, y1, 0, color);
            ptr += ColorVertex.STRIDE;

            writer.push(stack, buffer, 4, ColorVertex.FORMAT);
        }
    }
}
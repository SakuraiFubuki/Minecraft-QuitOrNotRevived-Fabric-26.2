package skin.grape.quitornot.client.integration.modmenu.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.util.Collections;
import java.util.List;

public final class SettingElementListWidget extends ContainerObjectSelectionList<SettingElementListWidget.Entry> {
    /**
     * @param width        widget width (the full screen width)
     * @param screenHeight current screen height; the list itself stops at {@code bottom}
     * @param top          y coordinate of the first row
     * @param bottom       y coordinate the list must not cross (keeps clear of the Done button)
     * @param itemHeight   row height
     */
    public SettingElementListWidget(Minecraft minecraftClient, int width, int screenHeight, int top, int bottom, int itemHeight) {
        // AbstractSelectionList takes the widget HEIGHT (3rd arg) and its y (4th arg), not the
        // screen height followed by a bottom edge. Passing the screen height made the list run off
        // the bottom of the window and render underneath the footer button.
        super(minecraftClient, width, Math.max(itemHeight, bottom - top), top, itemHeight);
    }

    @Override
    public int addEntry(Entry entry) {
        return super.addEntry(entry);
    }

    public class CategoryEntry extends Entry {
        private final Component text;
        private final int textWidth;
        private final int textColor;

        public CategoryEntry(Component text, int color) {
            this.text = text;
            this.textWidth = SettingElementListWidget.this.minecraft.font.width(text);
            this.textColor = color;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput builder) {
                    builder.add(NarratedElementType.TITLE, text);
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            assert minecraft.gui.screen() != null;
            int dx = this.getContentX() + (this.getContentWidth() - this.textWidth) / 2;
            int dy = this.getContentY() + this.getContentHeight();
            ctx.text(minecraft.font, this.text, dx, dy - minecraft.font.lineHeight, textColor, true);
        }
    }

    /**
     * Draws a row label, clipped so it can never run underneath the control on its right.
     * Long translations (and narrow windows) used to overlap the value button / input box.
     */
    private void drawLabelClipped(GuiGraphicsExtractor ctx, Component label, int x, int y, int controlLeft) {
        int maxRight = controlLeft - 4;
        if (maxRight <= x) {
            return;
        }
        ctx.enableScissor(x, y - 1, maxRight, y + minecraft.font.lineHeight + 1);
        ctx.text(minecraft.font, label, x, y, ARGB.opaque(16777215), true);
        ctx.disableScissor();
    }

    public class InputListEntry extends Entry {
        private final EditBox fieldWidget;
        private final Component describeText;

        public InputListEntry(EditBox fieldWidget, Component describeText) {
            this.fieldWidget = fieldWidget;
            this.describeText = describeText;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(fieldWidget);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.fieldWidget.setX(this.getContentWidth() - fieldWidget.getWidth() + this.getContentX());
            this.fieldWidget.setY(this.getContentY());
            drawLabelClipped(ctx, describeText, this.getContentX(),
                    (int) (this.getContentY() + minecraft.font.lineHeight / 2.0), this.fieldWidget.getX());
            this.fieldWidget.extractRenderState(ctx, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.singletonList(fieldWidget);
        }
    }


    public class ButtonListEntry extends Entry {
        private final Button button;
        private final Component describeText;

        public ButtonListEntry(Button settleButton, Component describeText) {
            this.button = settleButton;
            this.describeText = describeText;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(button);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.setX(this.getContentWidth() - button.getWidth() + this.getContentX());
            this.button.setY(this.getContentY());
            drawLabelClipped(ctx, describeText, this.getContentX(), this.getContentY() + 5, this.button.getX());
            this.button.extractRenderState(ctx, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
            return this.button.mouseClicked(mouseButtonEvent, bl);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
            return this.button.mouseReleased(mouseButtonEvent);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.singletonList(button);
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    }
}

package skin.grape.quitornot.client.integration.modmenu.screen;

import net.minecraft.util.ARGB;
import skin.grape.quitornot.client.config.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.function.Consumer;

public final class SettingScreen extends Screen {
    /** Distance from the top of the screen to the first row (below the title). */
    private static final int LIST_TOP = 30;
    /** Keep the scroll area clear of the bottom Done button. */
    private static final int LIST_BOTTOM_MARGIN = 40;
    private static final int ENTRY_HEIGHT = 24;
    private static final int FOOTER_BUTTON_WIDTH = 200;

    private final Screen parentScreen;
    private Button back;
    private SettingElementListWidget listWidget;

    public SettingScreen(Screen screen) {
        super(Component.translatable("config.quitornot.settings.title"));
        parentScreen = screen;
    }

    /**
     * Height available to the scroll list. Kept in one place so {@link #init()} and
     * {@link #repositionElements()} can never disagree.
     */
    private int listHeight() {
        return Math.max(ENTRY_HEIGHT, this.height - LIST_BOTTOM_MARGIN - LIST_TOP);
    }

    @Override
    protected void init() {
        back = Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - FOOTER_BUTTON_WIDTH / 2, this.height - 30, FOOTER_BUTTON_WIDTH, 20).build();

        listWidget = new SettingElementListWidget(this.minecraft, this.width, this.height, LIST_TOP, this.height - LIST_BOTTOM_MARGIN, ENTRY_HEIGHT);
        {
            MutableComponent category = Component.translatable("config.quitornot.settings.type.confirm.category");
            listWidget.addEntry(listWidget.new CategoryEntry(category.setStyle(Style.EMPTY.withBold(true)), ARGB.opaque(11184810)));

            listWidget.addEntry(listWidget.new ButtonListEntry(Button
                    .builder(Config.config.confirmTypeQuitGame.displayName, (button) -> {
                        Config.config.confirmTypeQuitGame = Config.config.nextEnum(Config.ConfirmType.class, Config.config.confirmTypeQuitGame);
                        button.setMessage((Config.config.confirmTypeQuitGame.displayName));
                    })
                    .bounds(0, 0, 50, 20).build(), Component.translatable("config.quitornot.settings.type.confirm.game")));

            listWidget.addEntry(listWidget.new ButtonListEntry(Button
                    .builder((Config.config.confirmTypeQuitSinglePlayer.displayName), (button) -> {
                        Config.config.confirmTypeQuitSinglePlayer = Config.config.nextEnum(Config.ConfirmType.class, Config.config.confirmTypeQuitSinglePlayer);
                        button.setMessage((Config.config.confirmTypeQuitSinglePlayer.displayName));
                    })
                    .bounds(0, 0, 50, 20).build(), Component.translatable("config.quitornot.settings.type.confirm.singleplayer")));

            listWidget.addEntry(listWidget.new ButtonListEntry(Button
                    .builder((Config.config.confirmTypeQuitMultiplayer.displayName), (button) -> {
                        Config.config.confirmTypeQuitMultiplayer = Config.config.nextEnum(Config.ConfirmType.class, Config.config.confirmTypeQuitMultiplayer);
                        button.setMessage((Config.config.confirmTypeQuitMultiplayer.displayName));
                    })
                    .bounds(0, 0, 50, 20).build(), Component.translatable("config.quitornot.settings.type.confirm.multiplayer")));
        }
        {
            MutableComponent category = Component.translatable("config.quitornot.settings.screen.category");
            listWidget.addEntry(listWidget.new CategoryEntry(category.setStyle(category.getStyle().withBold(true)), ARGB.opaque(11184810)));

            listWidget.addEntry(listWidget.new ButtonListEntry(Button
                    .builder((Config.config.confirmScreenStyle.displayName), (button) -> {
                        Config.config.confirmScreenStyle = Config.config.nextEnum(Config.ScreenStyle.class, Config.config.confirmScreenStyle);
                        button.setMessage((Config.config.confirmScreenStyle.displayName));
                    })
                    .bounds(0, 0, 50, 20).build(),
                    Component.translatable("config.quitornot.settings.screen.style")));


            listWidget.addEntry(listWidget.new ButtonListEntry(Button
                    .builder(Config.config.enableKey ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF, (button) -> {
                        Config.config.enableKey = !Config.config.enableKey;
                        button.setMessage(Config.config.enableKey ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
                    })
                    .bounds(0, 0, 50, 20).build(), Component.translatable("config.quitornot.settings.screen.key")));

            listWidget.addEntry(listWidget.new ButtonListEntry(Button
                    .builder(Config.config.swapButtons ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF, (button) -> {
                        Config.config.swapButtons = !Config.config.swapButtons;
                        button.setMessage(Config.config.swapButtons ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
                    })
                    .bounds(0, 0, 50, 20).build(), Component.translatable("config.quitornot.settings.screen.swap")));

            EditBox keepDark = new EditBox(minecraft.font, 0, 0, 48, 20, Component.empty());
            keepDark.setValue(String.valueOf(Config.config.buttonWaitTime));
            keepDark.setResponder(new PositiveLongParser(keepDark, (it) -> Config.config.buttonWaitTime = it));
            listWidget.addEntry(listWidget.new InputListEntry(keepDark, Component.translatable("config.quitornot.settings.screen.wait")));
        }
        {
            MutableComponent category = Component.translatable("config.quitornot.settings.toast.category");
            listWidget.addEntry(listWidget.new CategoryEntry(category.setStyle(category.getStyle().withBold(true)), ARGB.opaque(11184810)));

            EditBox toastDisplayTime = new EditBox(minecraft.font, 0, 0, 48, 20, Component.empty());
            toastDisplayTime.setValue(String.valueOf(Config.config.toastKeepTime));
            toastDisplayTime.setResponder(new PositiveLongParser(toastDisplayTime, (it) -> Config.config.toastKeepTime = it));
            listWidget.addEntry(listWidget.new InputListEntry(toastDisplayTime, Component.translatable("config.quitornot.settings.toast.time")));

            EditBox toastStartAliveTime = new EditBox(minecraft.font, 0, 0, 48, 20, Component.empty());
            toastStartAliveTime.setValue(String.valueOf(Config.config.toastDelay));
            toastStartAliveTime.setResponder(new PositiveLongParser(toastStartAliveTime, (it) -> Config.config.toastDelay = it));
            listWidget.addEntry(listWidget.new InputListEntry(toastStartAliveTime, Component.translatable("config.quitornot.settings.toast.start")));
        }
        this.addRenderableWidget(listWidget);
        this.addRenderableWidget(back);
    }

    /**
     * 26.2 resizes screens by calling this instead of re-running {@link #init()}, so the widget
     * geometry has to be refreshed here. Resizing the list in place (rather than rebuilding the
     * widgets) also keeps the player's scroll position when the window is resized.
     */
    @Override
    protected void repositionElements() {
        if (this.listWidget == null || this.back == null || minecraft == null) {
            super.repositionElements();
            return;
        }
        this.listWidget.updateSizeAndPosition(this.width, listHeight(), LIST_TOP);
        this.back.setX(this.width / 2 - FOOTER_BUTTON_WIDTH / 2);
        this.back.setY(this.height - 30);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.extractRenderState(ctx, mouseX, mouseY, delta);
        ctx.centeredText(this.font, this.title, this.width / 2, 15, ARGB.opaque(16777215));

        MutableComponent literal = Component.literal("QuitOrNot");
        ctx.text(font, literal, 2, this.height - font.lineHeight, ARGB.opaque(5592405));
    }

    @Override
    public void onClose() {
        Config.save();
        minecraft.gui.setScreen(parentScreen);
    }

    class PositiveLongParser implements Consumer<String> {
        private final EditBox fieldWidget;
        private final Consumer<Long> longConsumer;

        PositiveLongParser(EditBox fieldWidget, Consumer<Long> longConsumer) {
            this.fieldWidget = fieldWidget;
            this.longConsumer = longConsumer;
        }

        @Override
        public void accept(String s) {
            try {
                long parsed = Long.parseLong(s);
                if (parsed >= 0) {
                    fieldWidget.setTextColor(ARGB.opaque(14737632));
                    longConsumer.accept(parsed);
                    back.active = true;
                    return;
                }
            } catch (Exception ignored) {
            }
            back.active = false;
            fieldWidget.setTextColor(ARGB.opaque(16711680));
        }
    }
}

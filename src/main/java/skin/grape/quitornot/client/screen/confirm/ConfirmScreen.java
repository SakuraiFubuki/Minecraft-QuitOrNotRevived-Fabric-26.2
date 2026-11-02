package skin.grape.quitornot.client.screen.confirm;

import net.minecraft.client.input.KeyEvent;
import skin.grape.quitornot.client.config.Config;
import skin.grape.quitornot.client.mixin.AccessorScreen;
import skin.grape.quitornot.client.screen.confirm.style.BaseStyle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class ConfirmScreen extends Screen {
    private final Component message;
    private final Runnable onCancel;
    private final Runnable onConfirm;
    private final long openTime;
    private final BaseStyle style = Config.config.confirmScreenStyle.baseStyleSupplier.get();
    private Button cancel;
    private Button confirm;

    private boolean confirmed = false;

    public boolean isConfirmed() {
        return confirmed;
    }

    public ConfirmScreen(Screen parentScreen, Component message, Runnable confirm) {
        super(Component.translatable("screen.quitornot.confirm.title"));
        this.openTime = System.currentTimeMillis();
        this.message = message;
        this.onCancel = () -> this.minecraft.gui.setScreen(parentScreen);
        this.onConfirm = () -> {
            confirmed = true;
            confirm.run();
        };
    }

    @Override
    protected void init() {
        initButton();
    }

    @Override
    public void tick() {
        if (isActive()) {
//            cancel.active = true;
            confirm.active = true;
        }
    }

    private void initButton() {
        confirm = style.generateConfirmButtons(this, button -> {
            confirmed = true;
            onConfirm.run();
        });

        cancel = style.generateCancelButtons(this, button -> onCancel.run());

        confirm.active = false;
//        cancel.active = false;
        this.addRenderableWidget(confirm);
        this.addRenderableWidget(cancel);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        style.extractRenderState(this.minecraft, this.font, this, title, message, context, mouseX, mouseY, delta);
        for (final Renderable r : ((AccessorScreen) (Object) this).getRenderables()) {
            r.extractRenderState(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (Config.config.enableKey && keyEvent.key() == GLFW.GLFW_KEY_ENTER && isActive()) {
            onConfirm.run();
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return Config.config.enableKey;
    }

    @Override
    public void onClose() {
        onCancel.run();
    }

    public boolean isActive() {
        return openTime + Config.config.buttonWaitTime < System.currentTimeMillis();
    }
}
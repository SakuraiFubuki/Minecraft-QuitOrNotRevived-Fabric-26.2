package skin.grape.quitornot.client;

import com.mojang.logging.LogUtils;
import skin.grape.quitornot.client.config.Config;
import skin.grape.quitornot.client.event.ButtonPressEvent;
import skin.grape.quitornot.client.event.ClientScheduleStopEvent;
import skin.grape.quitornot.client.event.EventResult;
import skin.grape.quitornot.client.handler.ToastQuitHandler;
import skin.grape.quitornot.client.screen.confirm.ConfirmScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.slf4j.Logger;


public class QuitOrNot implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final ToastQuitHandler toastInFinalQuitHandler = new ToastQuitHandler(Component.translatable("toast.quitornot.confirm.title.game"));
    private final ToastQuitHandler toastInSinglePlayerQuitHandle = new ToastQuitHandler(Component.translatable("toast.quitornot.confirm.title.singleplayer"));
    private final ToastQuitHandler toastInMultiplayerQuitHandle = new ToastQuitHandler(Component.translatable("toast.quitornot.confirm.title.multiplayer"));

    /**
     * Opens the confirmation screen, unless one is already open.
     * <p>
     * A quit request can arrive more than once for a single window close (the 26.2 close callback
     * plus the per-frame {@code shouldClose} poll). Without this guard each request would stack
     * another confirmation screen on top of the previous one.
     */
    private static void openConfirmScreen(Component title, Runnable onConfirm) {
        if (Minecraft.getInstance().gui.screen() instanceof ConfirmScreen) {
            return;
        }
        Minecraft.getInstance().gui.setScreen(new ConfirmScreen(Minecraft.getInstance().gui.screen(), title, onConfirm));
    }

    @Override
    public void onInitializeClient() {
        Config.load();
        ClientScheduleStopEvent.CLIENT_SCHEDULE_STOP.register(() -> {
            if (Config.config.confirmTypeQuitGame == Config.ConfirmType.TOAST) {
                return toastInFinalQuitHandler.trigger();
            }
            if (Config.config.confirmTypeQuitGame == Config.ConfirmType.SCREEN) {
                openConfirmScreen(Component.translatable("screen.quitornot.confirm.title.game"), () -> Minecraft.getInstance().stop());
                return EventResult.CANCEL;
            }
            return EventResult.PASS;
        });

        ButtonPressEvent.BUTTON_PRESS.register(button -> {
            if (!(Minecraft.getInstance().gui.screen() instanceof PauseScreen)) {
                return EventResult.PASS;
            }
            String key = null;
            if (button.getMessage() instanceof TranslatableContents) {
                key = ((TranslatableContents) button.getMessage()).getKey();
            } else if (button.getMessage().getContents() instanceof TranslatableContents) {
                key = ((TranslatableContents) button.getMessage().getContents()).getKey();
            }
            if ("menu.returnToMenu".equals(key)) {
                if (Config.config.confirmTypeQuitSinglePlayer == Config.ConfirmType.TOAST) {
                    return toastInSinglePlayerQuitHandle.trigger();
                }
                if (Config.config.confirmTypeQuitSinglePlayer == Config.ConfirmType.SCREEN) {
                    openConfirmScreen(Component.translatable("screen.quitornot.confirm.title.singleplayer"), () -> button.onPress(null));
                    return EventResult.CANCEL;
                }
                return EventResult.PASS;
            }
            if ("menu.disconnect".equals(key)) {
                if (Config.config.confirmTypeQuitMultiplayer == Config.ConfirmType.TOAST) {
                    return toastInMultiplayerQuitHandle.trigger();
                }
                if (Config.config.confirmTypeQuitMultiplayer == Config.ConfirmType.SCREEN) {
                    openConfirmScreen(Component.translatable("screen.quitornot.confirm.title.multiplayer"), () -> button.onPress(null));
                    return EventResult.CANCEL;
                }
                return EventResult.PASS;
            }
            return EventResult.PASS;
        });
    }
}

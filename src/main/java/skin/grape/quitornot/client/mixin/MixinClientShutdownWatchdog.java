package skin.grape.quitornot.client.mixin;

import com.mojang.blaze3d.platform.ClientShutdownWatchdog;
import skin.grape.quitornot.client.event.ClientScheduleStopEvent;
import skin.grape.quitornot.client.event.EventResult;
import skin.grape.quitornot.client.screen.confirm.ConfirmScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Minecraft 26.2 introduced a client shutdown watchdog: the GLFW window-close callback arms it
 * before the quit is polled, and it hard-crashes the game ("Watchdog (Client shutdown from window
 * close callback)") if the render thread has not finished shutting down in time.
 * <p>
 * Because this mod deliberately holds the quit open until the player confirms it, the watchdog must
 * not be armed while a confirmation is pending. It is left untouched whenever the quit is allowed
 * to proceed, so a genuine shutdown hang is still reported as before.
 */
@Mixin(ClientShutdownWatchdog.class)
public abstract class MixinClientShutdownWatchdog {

    @Inject(method = "startShutdownWatchdog", at = @At("HEAD"), cancellable = true)
    private static void qon$inject$holdShutdownForConfirmation(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        Screen currentScreen = client.gui.screen();
        if (currentScreen instanceof ConfirmScreen confirmScreen) {
            if (!confirmScreen.isConfirmed()) {
                // The player is still deciding; do not arm the watchdog.
                ci.cancel();
            }
            return;
        }
        if (ClientScheduleStopEvent.CLIENT_SCHEDULE_STOP.invoker().onScheduleStop() == EventResult.CANCEL) {
            // The mod takes over the quit and shows a confirmation; the watchdog would kill the game.
            ci.cancel();
        }
    }
}

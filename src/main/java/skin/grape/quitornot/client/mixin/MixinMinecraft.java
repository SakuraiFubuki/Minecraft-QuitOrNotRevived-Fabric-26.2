package skin.grape.quitornot.client.mixin;

import skin.grape.quitornot.client.event.ClientScheduleStopEvent;
import skin.grape.quitornot.client.event.EventResult;
import skin.grape.quitornot.client.screen.confirm.ConfirmScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "stop", at = @At("HEAD"), cancellable = true)
    private void qon$inject$scheduleStop(CallbackInfo ci) {
        Minecraft self = (Minecraft) (Object) this;
        // 26.2: the active screen lives on Minecraft#gui, not on Minecraft itself.
        Screen currentScreen = self.gui.screen();
        if (currentScreen instanceof ConfirmScreen confirmScreen) {
            if (confirmScreen.isConfirmed()) {
                return;
            }
            // A confirmation is still pending. Clear the window's close request as well, otherwise
            // shouldClose() keeps returning true and runTick() re-enters stop() on every frame, which
            // also makes dismissing the confirm screen spawn a fresh one immediately.
            GLFW.glfwSetWindowShouldClose(self.getWindow().handle(), false);
            ci.cancel();
            return;
        }
        EventResult result = ClientScheduleStopEvent.CLIENT_SCHEDULE_STOP.invoker().onScheduleStop();
        if (result == EventResult.CANCEL) {
            GLFW.glfwSetWindowShouldClose(self.getWindow().handle(), false);
            ci.cancel();
        }
    }
}

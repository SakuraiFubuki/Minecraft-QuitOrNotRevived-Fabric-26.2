package skin.grape.quitornot.client.mixin;


import skin.grape.quitornot.client.event.ButtonPressEvent;
import skin.grape.quitornot.client.event.EventResult;
import skin.grape.quitornot.client.screen.confirm.ConfirmScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Button.class)
public abstract class MixinButton extends AbstractButton {
    public MixinButton(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void qon$inject$onPress(CallbackInfo ci) {
        if (Minecraft.getInstance().gui.screen() instanceof ConfirmScreen) {
            if (((ConfirmScreen) Minecraft.getInstance().gui.screen()).isConfirmed()) {
                return;
            }
        }
        EventResult result = ButtonPressEvent.BUTTON_PRESS.invoker().onPress((Button) (Object) this);
        if (result == EventResult.CANCEL) {
            ci.cancel();
        }
    }
}

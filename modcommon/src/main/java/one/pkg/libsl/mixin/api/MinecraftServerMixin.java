/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import one.pkg.libsl.api.loader.JavaLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "getServerModName", at = @At("HEAD"), cancellable = true)
    private void libsl$getServerModName(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(JavaLoader.INSTANCE.getServerModName());
    }

    @Redirect(
            method = "buildServerStatus",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;nullToEmpty(Ljava/lang/String;)Lnet/minecraft/network/chat/Component;")
    )
    private static Component libsl$nullToEmpty(String text) {
        if (JavaLoader.INSTANCE.getServerMotd() != null) return JavaLoader.INSTANCE.getServerMotd();
        return Component.nullToEmpty(text);
    }
}

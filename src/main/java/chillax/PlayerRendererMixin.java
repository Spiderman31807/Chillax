package chillax.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.PlayerModel;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

import chillax.StateHolder;
import chillax.State;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
	private PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadow) {
		super(context, model, shadow);
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void extractRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTick, CallbackInfo callback) {
		if (player instanceof StateHolder holder) {
			switch (holder.getChillaxState()) {
				case Swimming :
					state.isVisuallySwimming = true;
					state.swimAmount = 1f;
					break;
				case Sitting :
					state.isPassenger = true;
					state.passengerOffset = new Vec3(0, -0.6 * player.getScale(), 0);
					break;
				case Sleeping :
					state.flyingYRot = holder.getSleepingAngle();
					if (state.yRot > 50)
						state.yRot = 50;
					if (state.yRot < -50)
						state.yRot = -50;
					state.xRot += 80 - Math.abs(state.yRot);
					if (state.xRot > 50)
						state.xRot = 50;
					if (state.xRot < -10)
						state.xRot = -10;
					state.swimAmount = -1f;
					break;
			};
		}
	}

	@Inject(method = "setupRotations", at = @At("HEAD"), cancellable = true)
	private void setupRotations(PlayerRenderState state, PoseStack pose, float p_117804_, float p_117805_, CallbackInfo callback) {
		if (state.swimAmount == -1f) {
			pose.mulPose(Axis.YP.rotationDegrees(state.flyingYRot));
			pose.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees()));
			pose.mulPose(Axis.YP.rotationDegrees(270));
			pose.translate(0, -1.5f, 0);
			callback.cancel();
		}
	}
}
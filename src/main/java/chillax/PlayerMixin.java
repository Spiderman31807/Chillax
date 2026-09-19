package chillax.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

import chillax.StateHolder;
import chillax.State;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements StateHolder {
	@Unique
	private State chillaxState;
	@Unique
	private State chillaxLastState;
	@Unique
	private float sleepingAngle;

	private PlayerMixin() {
		super(null, null);
	}

	@Override
	public State getChillaxState() {
		if (this.chillaxState == null || this.firstTick || this.isVehicle() || this.isPassenger() || this.isDeadOrDying() || this.isSleeping() || this.isFallFlying() || this.isSwimming())
			return State.None;
		return this.chillaxState;
	}

	@Override
	public void setChillaxState(State state) {
		this.chillaxState = state;
	}

	@Override
	public float getSleepingAngle() {
		return this.sleepingAngle;
	}

	@Override
	public void setSleepingAngle(float angle) {
		this.setYBodyRot(this.getYRot());
		this.sleepingAngle = angle;
	}

	@Override
	public void jumpFromGround() {
		if (this.getChillaxState() != State.None)
			this.setChillaxState(State.None);
		else
			super.jumpFromGround();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo callback) {
		State currentState = this.getChillaxState();
		if(this.chillaxLastState != currentState) {
			this.chillaxLastState = currentState;
			this.refreshDimensions();
		}
	}

	@Inject(method = "getSpeed", at = @At("RETURN"), cancellable = true)
	private void getSpeed(CallbackInfoReturnable<Float> callback) {
		State state = this.getChillaxState();
		if (state == State.Swimming)
			callback.setReturnValue(callback.getReturnValue() * 0.33f);
		else if (state != State.None)
			callback.setReturnValue(0f);
	}
}
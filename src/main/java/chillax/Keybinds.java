package chillax;

import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@EventBusSubscriber(modid = "chillax", value = {Dist.CLIENT})
public class Keybinds {
	public static final KeyMapping Crawl = new KeyMapping("chillax.key.swim", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof StateHolder holder) {
				State currentState = holder.getChillaxState();
				if (currentState == State.Swimming) {
					holder.setChillaxState(State.None);
					ClientPacketDistributor.sendToServer(new ServerboundChangeStatePacket(State.None));
				} else {
					holder.setChillaxState(State.Swimming);
					ClientPacketDistributor.sendToServer(new ServerboundChangeStatePacket(State.Swimming));
				}
			}
		}
	};
	public static final KeyMapping Sleep = new KeyMapping("chillax.key.sleep", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof Player player && player instanceof StateHolder holder) {
				State currentState = holder.getChillaxState();
				if (currentState == State.Sleeping) {
					holder.setChillaxState(State.None);
					ClientPacketDistributor.sendToServer(new ServerboundChangeStatePacket(State.None));
				} else {
					holder.setChillaxState(State.Sleeping);
					holder.setSleepingAngle(Mth.wrapDegrees(-player.getYRot() - 90));
					ClientPacketDistributor.sendToServer(new ServerboundChangeStatePacket(State.Sleeping));
					ClientPacketDistributor.sendToServer(new ServerboundChangeSleepingAnglePacket(holder.getSleepingAngle()));
				}
			}
		}
	};
	public static final KeyMapping Sit = new KeyMapping("chillax.key.sit", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof StateHolder holder) {
				State currentState = holder.getChillaxState();
				if (currentState == State.Sitting) {
					holder.setChillaxState(State.None);
					ClientPacketDistributor.sendToServer(new ServerboundChangeStatePacket(State.None));
				} else {
					holder.setChillaxState(State.Sitting);
					ClientPacketDistributor.sendToServer(new ServerboundChangeStatePacket(State.Sitting));
				}
			}
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(Crawl);
		event.register(Sleep);
		event.register(Sit);
	}

	@EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				Crawl.consumeClick();
				Sleep.consumeClick();
				Sit.consumeClick();
			}
		}
	}
}
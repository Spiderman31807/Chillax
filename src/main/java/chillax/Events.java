package chillax;

import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityDimensions;

@EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void hitbox(EntityEvent.Size event) {
		if (event.getEntity() instanceof LivingEntity entity && entity instanceof StateHolder holder) {
			switch (holder.getChillaxState()) {
				case Swimming :
					event.setNewSize(EntityDimensions.scalable(0.6F, 0.6F).scale(entity.getScale()));
					break;
				case Sleeping :
					event.setNewSize(EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.2F).scale(entity.getScale()));
					break;
				case Sitting :
					event.setNewSize(EntityDimensions.scalable(0.6F, 1.2F).withEyeHeight(1F).scale(entity.getScale()));
					break;
			}
		}
	}
}
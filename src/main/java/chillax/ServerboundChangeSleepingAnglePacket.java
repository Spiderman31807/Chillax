package chillax;

import org.apache.commons.lang3.EnumUtils;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

@EventBusSubscriber(modid = ChillaxMod.MODID)
public record ServerboundChangeSleepingAnglePacket(float angle) implements CustomPacketPayload {
	public static final Type<ServerboundChangeSleepingAnglePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChillaxMod.MODID, "server_sync_angle"));
	public static final StreamCodec<FriendlyByteBuf, ServerboundChangeSleepingAnglePacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ServerboundChangeSleepingAnglePacket packet) -> {
		buffer.writeFloat(packet.angle);
	}, (FriendlyByteBuf buffer) -> new ServerboundChangeSleepingAnglePacket(buffer.readFloat()));

	@Override
	public Type<ServerboundChangeSleepingAnglePacket> type() {
		return TYPE;
	}

	public static void handle(ServerboundChangeSleepingAnglePacket packet, IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND && context.player() instanceof Player player && player instanceof StateHolder holder) {
			holder.setSleepingAngle(packet.angle);
			PacketDistributor.sendToPlayersTrackingEntity(player, new ClientboundChangeSleepingAnglePacket(player.getId(), packet.angle));
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChillaxMod.addNetworkMessage(ServerboundChangeSleepingAnglePacket.TYPE, ServerboundChangeSleepingAnglePacket.STREAM_CODEC, ServerboundChangeSleepingAnglePacket::handle);
	}
}
package chillax;

import net.neoforged.neoforge.network.handling.IPayloadContext;
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
public record ClientboundChangeSleepingAnglePacket(int playerId, float angle) implements CustomPacketPayload {
	public static final Type<ClientboundChangeSleepingAnglePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChillaxMod.MODID, "client_sync_angle"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundChangeSleepingAnglePacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ClientboundChangeSleepingAnglePacket packet) -> {
		buffer.writeInt(packet.playerId);
		buffer.writeFloat(packet.angle);
	}, (FriendlyByteBuf buffer) -> new ClientboundChangeSleepingAnglePacket(buffer.readInt(), buffer.readFloat()));

	@Override
	public Type<ClientboundChangeSleepingAnglePacket> type() {
		return TYPE;
	}

	public static void handle(ClientboundChangeSleepingAnglePacket packet, IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND && context.player().level().getEntity(packet.playerId) instanceof Player player && player instanceof StateHolder holder)
			holder.setSleepingAngle(packet.angle);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChillaxMod.addNetworkMessage(ClientboundChangeSleepingAnglePacket.TYPE, ClientboundChangeSleepingAnglePacket.STREAM_CODEC, ClientboundChangeSleepingAnglePacket::handle);
	}
}
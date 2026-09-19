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

import com.sun.jna.platform.EnumUtils;

@EventBusSubscriber(modid = ChillaxMod.MODID)
public record ClientboundChangeStatePacket(int playerId, State state) implements CustomPacketPayload {
	public static final Type<ClientboundChangeStatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChillaxMod.MODID, "client_sync_state"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundChangeStatePacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ClientboundChangeStatePacket packet) -> {
		buffer.writeInt(packet.playerId);
		buffer.writeInt(EnumUtils.toInteger(packet.state));
	}, (FriendlyByteBuf buffer) -> new ClientboundChangeStatePacket(buffer.readInt(), EnumUtils.fromInteger(buffer.readInt(), State.class)));

	@Override
	public Type<ClientboundChangeStatePacket> type() {
		return TYPE;
	}

	public static void handle(ClientboundChangeStatePacket packet, IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND && context.player().level().getEntity(packet.playerId) instanceof Player player && player instanceof StateHolder holder)
			holder.setChillaxState(packet.state);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChillaxMod.addNetworkMessage(ClientboundChangeStatePacket.TYPE, ClientboundChangeStatePacket.STREAM_CODEC, ClientboundChangeStatePacket::handle);
	}
}
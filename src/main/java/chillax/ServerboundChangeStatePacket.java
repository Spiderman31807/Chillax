package chillax;

import com.sun.jna.platform.EnumUtils;

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
public record ServerboundChangeStatePacket(State state) implements CustomPacketPayload {
	public static final Type<ServerboundChangeStatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChillaxMod.MODID, "server_sync_state"));
	public static final StreamCodec<FriendlyByteBuf, ServerboundChangeStatePacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ServerboundChangeStatePacket packet) -> {
		buffer.writeInt(EnumUtils.toInteger(packet.state));
	}, (FriendlyByteBuf buffer) -> new ServerboundChangeStatePacket(EnumUtils.fromInteger(buffer.readInt(), State.class)));

	@Override
	public Type<ServerboundChangeStatePacket> type() {
		return TYPE;
	}

	public static void handle(ServerboundChangeStatePacket packet, IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND && context.player() instanceof Player player && player instanceof StateHolder holder) {
			holder.setChillaxState(packet.state);
			PacketDistributor.sendToPlayersTrackingEntity(player, new ClientboundChangeStatePacket(player.getId(), packet.state));
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChillaxMod.addNetworkMessage(ServerboundChangeStatePacket.TYPE, ServerboundChangeStatePacket.STREAM_CODEC, ServerboundChangeStatePacket::handle);
	}
}
package tocraft.craftedcore.network.fabric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.network.NetworkManager;
import tocraft.craftedcore.network.NetworkManager.NetworkReceiver;
import tocraft.craftedcore.network.PacketSink;
import tocraft.craftedcore.network.PacketTransformer;
import tocraft.craftedcore.platform.Dist;

public class NetworkManagerImpl {
    private static final Map<ResourceLocation, NetworkReceiver> C2S_RECEIVER = new HashMap<>();
    private static final Map<ResourceLocation, NetworkReceiver> S2C_RECEIVER = new HashMap<>();
    private static final Map<ResourceLocation, PacketTransformer> C2S_TRANSFORMERS = new HashMap<>();
    private static final Map<ResourceLocation, PacketTransformer> S2C_TRANSFORMERS = new HashMap<>();
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static void registerReceiver(NetworkManager.Side side, ResourceLocation id, List<PacketTransformer> packetTransformers, NetworkReceiver receiver) {
        Objects.requireNonNull(id, "Cannot register receiver with a null ID!");
        packetTransformers = Objects.requireNonNullElse(packetTransformers, List.of());
        Objects.requireNonNull(receiver, "Cannot register a null receiver!");
        if (side == NetworkManager.Side.C2S) {
            registerC2SReceiver(id, packetTransformers, receiver);
        } else if (side == NetworkManager.Side.S2C) {
            registerS2CReceiver(id, packetTransformers, receiver);
        }
    }
    
    private static void registerC2SReceiver(ResourceLocation id, List<PacketTransformer> packetTransformers, NetworkReceiver receiver) {
        LOGGER.info("Registering C2S receiver with id {}", id);
        C2S_RECEIVER.put(id, receiver);
        PacketTransformer transformer = PacketTransformer.concat(packetTransformers);
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, sender) -> {
            var context = context(player, server, false);
            transformer.inbound(NetworkManager.Side.C2S, id, buf, context, (side, id1, buf1) -> {
                NetworkReceiver networkReceiver = side == NetworkManager.Side.C2S ? C2S_RECEIVER.get(id1) : S2C_RECEIVER.get(id1);
                if (networkReceiver == null) {
                    throw new IllegalArgumentException("Network Receiver not found! " + id1);
                }
                networkReceiver.receive(buf1, context);
            });
        });
        C2S_TRANSFORMERS.put(id, transformer);
    }
    
    @Environment(EnvType.CLIENT)
    private static void registerS2CReceiver(ResourceLocation id, List<PacketTransformer> packetTransformers, NetworkReceiver receiver) {
        LOGGER.info("Registering S2C receiver with id {}", id);
        S2C_RECEIVER.put(id, receiver);
        PacketTransformer transformer = PacketTransformer.concat(packetTransformers);
        ClientPlayNetworking.registerGlobalReceiver(id, new ClientPlayNetworking.PlayChannelHandler() {
            @Override
            public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
                var context = context(client.player, client, true);
                transformer.inbound(NetworkManager.Side.S2C, id, buf, context, (side, id1, buf1) -> {
                    NetworkReceiver networkReceiver = side == NetworkManager.Side.C2S ? C2S_RECEIVER.get(id1) : S2C_RECEIVER.get(id1);
                    if (networkReceiver == null) {
                        throw new IllegalArgumentException("Network Receiver not found! " + id1);
                    }
                    networkReceiver.receive(buf1, context);
                });
            }
        });
        S2C_TRANSFORMERS.put(id, transformer);
    }
    
    private static NetworkManager.PacketContext context(Player player, BlockableEventLoop<?> taskQueue, boolean client) {
        return new NetworkManager.PacketContext() {
            @Override
            public Player getPlayer() {
                return player;
            }
            
            @Override
            public void queue(Runnable runnable) {
                taskQueue.execute(runnable);
            }
            
            @Override
            public Dist getDist() {
                return client ? Dist.CLIENT : Dist.DEDICATED_SERVER;
            }
        };
    }
    
    public static void collectPackets(PacketSink sink, NetworkManager.Side side, ResourceLocation id, FriendlyByteBuf buf) {
        PacketTransformer transformer = side == NetworkManager.Side.C2S ? C2S_TRANSFORMERS.get(id) : S2C_TRANSFORMERS.get(id);
        if (transformer != null) {
            transformer.outbound(side, id, buf, (side1, id1, buf1) -> {
                sink.accept(toPacket(side1, id1, buf1));
            });
        } else {
            sink.accept(toPacket(side, id, buf));
        }
    }
    
    public static Packet<?> toPacket(NetworkManager.Side side, ResourceLocation id, FriendlyByteBuf buf) {
        if (side == NetworkManager.Side.C2S) {
            return toC2SPacket(id, buf);
        } else if (side == NetworkManager.Side.S2C) {
            return toS2CPacket(id, buf);
        }
        
        throw new IllegalArgumentException("Invalid side: " + side);
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean canServerReceive(ResourceLocation id) {
        return ClientPlayNetworking.canSend(id);
    }
    
    public static boolean canPlayerReceive(ServerPlayer player, ResourceLocation id) {
        return ServerPlayNetworking.canSend(player, id);
    }
    
    @Environment(EnvType.CLIENT)
    private static Packet<?> toC2SPacket(ResourceLocation id, FriendlyByteBuf buf) {
        return ClientPlayNetworking.createC2SPacket(id, buf);
    }
    
    private static Packet<?> toS2CPacket(ResourceLocation id, FriendlyByteBuf buf) {
        return ServerPlayNetworking.createS2CPacket(id, buf);
    }
}

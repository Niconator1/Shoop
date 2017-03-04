package main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class NetworkManager {
	private static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

	public void registerNetworkHandling() {
		if (protocolManager != null) {
			protocolManager.addPacketListener(new PacketAdapter(Smashplex.getPlugin(Smashplex.class),
					ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
				@Override
				public void onPacketReceiving(PacketEvent event) {
					if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
						PacketContainer packet = event.getPacket();
						int id = packet.getIntegers().read(0);
						for (int i = 0; i < Smashplex.npcs.size(); i++) {
							NPC n = Smashplex.npcs.get(i);
							if (n.getEntityID() == id
									&& packet.getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.INTERACT) {
								n.openGUI(event.getPlayer());
							}
						}
					}
				}
			});
		}
	}

}

package com.fire;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class QuickFireClient implements ClientModInitializer {

	private static KeyBinding keyBinding;
	private int tickCounter = 0;
	private int slotIndex = 1; // Start at slot 2 (index 1)
	private final int delayTicks = 2; // Delay of 10 ticks between each right-click
	private boolean isActivated = false;

	@Override
	public void onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.quickfire.activate", // Translation key
				InputUtil.Type.KEYSYM, // Keybinding type
				GLFW.GLFW_KEY_R, // Default key
				"category.quickfire.general" // Translation key for the category
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keyBinding.wasPressed()) {
				isActivated = true;
				tickCounter = delayTicks; // Start the counter
				slotIndex = 1; // Reset to the first slot (index 1)
			}

			if (isActivated) {
				tickCounter++;
				if (tickCounter >= delayTicks) {
					simulateRightClickOnSlot(client);
					tickCounter = 0; // Reset the counter after each right-click
				}
			}
		});
	}

	private void simulateRightClickOnSlot(MinecraftClient client) {
		if (client.player != null && client.interactionManager != null) {
			PlayerInventory inventory = client.player.getInventory();
			int previousSlot = inventory.selectedSlot;
			inventory.selectedSlot = slotIndex; // Set the slot to the current index

			// Simulate right-click with the main hand
			client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);

			inventory.selectedSlot = previousSlot;

			slotIndex++; // Move to the next slot
			if (slotIndex > 8) {
				isActivated = false; // Stop the sequence after the last slot
			}
		}
	}
}
	//RELOAD CODE



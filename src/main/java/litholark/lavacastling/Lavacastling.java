package litholark.lavacastling;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Lavacastling implements ModInitializer {
	public static final String MOD_ID = "lavacastling";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Lavacastling initializing");

		UseBlockCallback.EVENT.register(this::onUseBlock);
		ServerWorldEvents.LOAD.register(this::onWorldLoad);
	}

	// look at net/minecraft/item/AxeItem.java
	private ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		Block block = world.getBlockState(hit.getBlockPos()).getBlock();
		String blockId = Registries.BLOCK.getId(block).toString();

		// if the item is a pickaxe
		if (stack.isIn(ItemTags.PICKAXES)) {
			for (List<String> blockList : Config.chiselBlocks) {
				if (blockList.contains(blockId)) {
//			if (world.getBlockState(hit.getBlockPos()).getBlock() == Blocks.STONE) {

					int idx = blockList.indexOf(blockId);
					String newBlockId = blockList.get((idx + 1) % blockList.size());

					Block newBlock = Registries.BLOCK.get(Identifier.of(newBlockId));

					world.setBlockState(hit.getBlockPos(), newBlock.getDefaultState());
					world.emitGameEvent(GameEvent.BLOCK_CHANGE, hit.getBlockPos(), GameEvent.Emitter.of(player, newBlock.getDefaultState()));
//				if (player != null) {
					stack.damage(1, player, LivingEntity.getSlotForHand(hand));
//				}
					world.playSound(player, hit.getBlockPos(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
//					world.syncWorldEvent(player, WorldEvents.SNIFFER_EGG_CRACKS, hit.getBlockPos(), 0);

					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;

	}

	private void onWorldLoad(MinecraftServer server, ServerWorld world) {
		Config.loadConfig();
	}


}
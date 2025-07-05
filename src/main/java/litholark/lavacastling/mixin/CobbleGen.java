package litholark.lavacastling.mixin;

import litholark.lavacastling.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.FluidBlock.FLOW_DIRECTIONS;

@Mixin(FluidBlock.class)
public abstract class CobbleGen {

	@Shadow @Final protected FlowableFluid fluid;

	@Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);

	@Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
	private void receiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (this.fluid.isIn(FluidTags.LAVA)) {
			boolean bl = world.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);

			for (Direction direction : FLOW_DIRECTIONS) {
				BlockPos blockPos = pos.offset(direction.getOpposite());
				if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
					Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Registries.BLOCK.get( Identifier.of(Config.getBlockAtY(pos.getY())) );
					world.setBlockState(pos, block.getDefaultState());
					this.playExtinguishSound(world, pos);
					cir.setReturnValue(false);
				}

				if (bl && world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
					world.setBlockState(pos, Blocks.BASALT.getDefaultState());
					this.playExtinguishSound(world, pos);
					cir.setReturnValue(false);
				}
			}
		}

		cir.setReturnValue(true);
	}

}
package leagueofcrafters.entity;

import leagueofcrafters.LeagueItems;
import leagueofcrafters.entity.projectiles.EntityBomb;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityZiggs extends EntityMob implements IRangedAttackMob {

	public static int timer;
	public static boolean hasBomb;

	public EntityZiggs(World par1World) {
		super(par1World);
		this.setSize(1, 1);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityMinion.class, 0, true));

	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
		if (timer <= 0) {
			EntityBomb entityarrow = new EntityBomb(this.worldObj, this, par1EntityLivingBase, 1.6F, (float) (14 - this.worldObj.difficultySetting * 4));
			entityarrow.setPosition(this.posX, this.posY, this.posZ);
			this.worldObj.spawnEntityInWorld(entityarrow);
			timer = 50;
		}
	}

	public void setAggressive(boolean par1) {
		this.getDataWatcher().updateObject(21, Byte.valueOf((byte) (par1 ? 1 : 0)));
	}

	/**
	 * Return whether this witch is aggressive at an entity.
	 */
	public boolean getAggressive() {
		return this.getDataWatcher().getWatchableObjectByte(21) == 1;
	}

	public void onLivingUpdate() {
		if (!this.worldObj.isRemote && this.timer > 0) {
			timer--;
		} else if (this.timer < 0) {
			this.timer = 0;
		}

		if (this.rand.nextFloat() < 7.5E-4F) {
			this.worldObj.setEntityState(this, (byte) 15);
		}

		super.onLivingUpdate();
	}

	@Override
	protected int getDropItemId() {
		return LeagueItems.bomb.itemID;
	}

	@Override
	protected String getLivingSound() {
		return "league:ziggs";
	}

	@Override
	protected String getHurtSound() {
		return "league:ziggs.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "league:ziggs.death";
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}
}

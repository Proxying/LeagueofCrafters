package leagueofcrafters.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import leagueofcrafters.client.ParticleEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityTwitch extends EntityMob implements IRangedAttackMob {
	private static final UUID field_110184_bp = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final AttributeModifier field_110185_bq = (new AttributeModifier(field_110184_bp, "Drinking speed penalty", -0.25D, 0)).setSaved(false);

	/** List of items a Twitch should drop on death. */
	private static final int[] TwitchDrops = new int[] { Item.glowstone.itemID, Item.sugar.itemID, Item.redstone.itemID, Item.spiderEye.itemID,
			Item.glassBottle.itemID, Item.gunpowder.itemID, Item.stick.itemID, Item.stick.itemID };
	private int timer;
	private int potiontimer;

	/**
	 * Timer used as interval for a Twitch's attack, decremented every tick if
	 * aggressive and when reaches zero the Twitch will throw a potion at the
	 * target entity.
	 */

	public EntityTwitch(World par1World) {
		super(par1World);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(3, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityMinion.class, 0, true));
	}

	protected void entityInit() {
		super.entityInit();
		this.getDataWatcher().addObject(21, Byte.valueOf((byte) 0));
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound() {
		return "league:twitch";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "league:twitch.hurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "league:twitch.death";
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
	}

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	public boolean isAIEnabled() {
		return true;
	}

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		// ParticleEffects.spawnParticle("fly", (double) this.posX - .1,
		// (double) this.posY + 1.1D, (double) this.posZ + .1, -1,
		// Color.BLACK.getGreen(), Color.BLACK.getBlue());

		if (!this.worldObj.isRemote) {
			ItemStack itemstack = this.getHeldItem();
			this.setCurrentItemOrArmor(0, (ItemStack) null);

			if (itemstack != null && itemstack.itemID == Item.potion.itemID) {
				List list = Item.potion.getEffects(itemstack);

				if (list != null) {
					Iterator iterator = list.iterator();

					while (iterator.hasNext()) {
						PotionEffect potioneffect = (PotionEffect) iterator.next();
						this.addPotionEffect(new PotionEffect(potioneffect));
					}
				}

				this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(field_110185_bq);
			}
		} else {
			short short1 = -1;

			if (this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(Potion.fireResistance)) {
				short1 = 16307;
			} else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
				short1 = 16341;
			} else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed)
					&& this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
				short1 = 16274;
			} else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed)
					&& this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
				short1 = 16274;
			}

			if (short1 > -1) {
				this.setCurrentItemOrArmor(0, new ItemStack(Item.potion, 1, short1));
				AttributeInstance attributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
				attributeinstance.removeModifier(field_110185_bq);
				attributeinstance.applyModifier(field_110185_bq);
			}
		}

		if (this.rand.nextFloat() < 7.5E-4F) {
			this.worldObj.setEntityState(this, (byte) 15);
		}
		if (timer != 0)
			timer--;
		if (potiontimer != 0)
			potiontimer--;
		super.onLivingUpdate();
	}

	/**
	 * Reduces damage, depending on potions
	 */
	protected float applyPotionDamageCalculations(DamageSource par1DamageSource, float par2) {
		par2 = super.applyPotionDamageCalculations(par1DamageSource, par2);

		if (par1DamageSource.getEntity() == this) {
			par2 = 0.0F;
		}

		if (par1DamageSource.isMagicDamage()) {
			par2 = (float) ((double) par2 * 0.15D);
		}

		return par2;
	}

	/**
	 * Drop 0-2 items of this living's type. @param par1 - Whether this entity
	 * has recently been hit by a player. @param par2 - Level of Looting used to
	 * kill this mob.
	 */
	protected void dropFewItems(boolean par1, int par2) {
		int j = this.rand.nextInt(3) + 1;

		for (int k = 0; k < j; ++k) {
			int l = this.rand.nextInt(3);
			int i1 = TwitchDrops[this.rand.nextInt(TwitchDrops.length)];

			if (par2 > 0) {
				l += this.rand.nextInt(par2 + 1);
			}

			for (int j1 = 0; j1 < l; ++j1) {
				this.dropItem(i1, 1);
			}
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		if (worldObj.villageCollectionObj.getVillageList().iterator().hasNext()
				&& worldObj.villageCollectionObj.findNearestVillage((int) this.posX, (int) this.posY, (int) this.posZ, 10) == null) {
			return false;
		}
		return true;
	}

	protected boolean canDespawn() {
		return true;
	}

	/**
	 * Attack the specified entity using a ranged attack.
	 */
	public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
		if (timer <= 0) {
			if (potiontimer == 0) {
				EntityPotion entitypotion = new EntityPotion(this.worldObj, this, 32732);
				entitypotion.rotationPitch -= -20.0F;
				double d0 = par1EntityLivingBase.posX + par1EntityLivingBase.motionX - this.posX;
				double d1 = par1EntityLivingBase.posY + (double) par1EntityLivingBase.getEyeHeight() - 1.100000023841858D - this.posY;
				double d2 = par1EntityLivingBase.posZ + par1EntityLivingBase.motionZ - this.posZ;
				float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

				if (par1EntityLivingBase.getHealth() >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.poison)) {
					entitypotion.setPotionDamage(32660);
				}

				entitypotion.setThrowableHeading(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
				this.worldObj.spawnEntityInWorld(entitypotion);
				timer = 50;
				potiontimer = 500;
			} else {
				EntityArrow entityarrow = new EntityArrow(this.worldObj, this, par1EntityLivingBase, 1.6F, (float) (14 - this.worldObj.difficultySetting * 4));
				entityarrow.setDamage(2);
				entityarrow.setPosition(this.posX, this.posY + 1, this.posZ);
				this.worldObj.spawnEntityInWorld(entityarrow);
				timer = 50;

			}
		}
	}
}

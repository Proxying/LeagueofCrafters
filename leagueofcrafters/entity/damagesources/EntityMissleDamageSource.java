package leagueofcrafters.entity.damagesources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EntityDamageSourceIndirect;

public class EntityMissleDamageSource extends EntityDamageSourceIndirect {
	private String player;
	private ChatMessageComponent message = new ChatMessageComponent();

	public EntityMissleDamageSource(String par1Str, Entity par2Entity, Entity par3Entity) {
		super(par1Str, par2Entity, par3Entity);
	}

	@Override
	public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase) {
		player = par1EntityLivingBase.getTranslatedEntityName();
		message.addText(player + " was killed by Tristana");
		return message;
	}
}

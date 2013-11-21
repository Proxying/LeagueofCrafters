package leagueofcrafters.client.renderer;

import leagueofcrafters.client.models.ModelMundo;
import leagueofcrafters.entity.EntityMundo;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderMundo extends RenderLiving {

	private final ResourceLocation texture = new ResourceLocation("league", "textures/models/mundo.png"); // refers
	protected ModelMundo model;

	public RenderMundo(ModelMundo par1ModelBase, float par2) {
		super(par1ModelBase, par2);
		model = par1ModelBase;
	}

	public RenderMundo(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	public void renderMundo(EntityMundo par1EntityMundo, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving(par1EntityMundo, par2, par4, par6, par8, par9);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderMundo((EntityMundo) par1EntityLiving, par2, par4, par6, par8, par9);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity) and this method has signature public
	 * void doRender(T entity, double d, double d1, double d2, float f, float
	 * f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderMundo((EntityMundo) par1Entity, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}

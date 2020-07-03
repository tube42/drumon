package se.tube42.drum.view;

import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;
import se.tube42.lib.item.*;

public class PadItem extends BaseButton {
	private int tile;
	private TextureRegion[] tex;

	public PadItem(int tile) {
		this(World.tex_tiles, tile);
	}

	public PadItem(TextureRegion[] tex, int tile) {
		this.tex = tex;
		setColor(0xffffff);
		this.tile = tile;
	}

	//
	public void animPress() {
		final float r = ServiceProvider.getRandom(0.08f, 0.12f);

		set(ITEM_S, 0.9f).configure(1 * r, null).tail(1.1f).configure(2 * r, null).tail(1.0f).configure(1 * r, null);
	}

	public void animAction(float amount)
    {
		set(BaseItem.ITEM_S, 1, 1.2f).configure(0.15f,null)
			.tail(1).configure(0.1f, null);
    }

	//
	public void setTile(int tile) {
		this.tile = tile;
	}

	public void draw(SpriteBatch sb) {
		final float a = getAlpha();
		final float s = getScale();
		final float x = getX();
		final float y = getY();
		final float r = getRotation();
		final float w2 = w / 2;
		final float h2 = h / 2;
		final float hp = World.halfpixel;

		// draw tile
		if (tile != -1) {
			final TextureRegion tr = tex[tile];
			sb.setColor(cr, cg, cb, a);
			sb.draw(tr, x + hp, y + hp, w2, h2, w, h, s, s, r);
		}
	}

}

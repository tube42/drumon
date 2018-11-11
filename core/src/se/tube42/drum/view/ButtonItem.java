package se.tube42.drum.view;

import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.drum.data.*;
import se.tube42.lib.item.*;

import static se.tube42.drum.data.Constants.*;

// import se.tube42.drum.logic.*;


public class ButtonItem extends PadItem
{
	private TextBox text;
    private boolean active;

    public ButtonItem(String text)
    {
        super(World.tex_rect, 0);

		this.text = new TextBox(World.font2);
		this.text.setAlignment(-0.5f, 0.5f);
		this.setText(text);
        this.active = true;
        setColor(COLOR_BUTTON);
    }

    // --------------------------------------

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
        setAlpha(active ? 1 : ALPHA_DISABLED);
    }

    public void setText(String text)
    {
        if(text != null) {
            // since our font is lower-case only...
			this.text.setText(text.toLowerCase());
            setAlpha(1);
        } else {
            setAlpha(0.4f);
			this.text.setText("");
        }
    }

    public void draw(SpriteBatch sb)
    {
        super.draw(sb);

		// font.setColor( 0.5f, 0.5f, 0.5f, alpha);
		// font.draw(sb, text, x+1, y+1);

		// font.setColor( 0, 0, 0, alpha);
		// font.draw(sb, text, x, y);

		final float alpha = getAlpha();
		final float x = getX() + w / 2;
        final float y = getY() + h / 2;


		text.getFont().setColor( 0.5f, 0.5f, 0.5f, alpha);
		text.draw(sb, x + 1, y + 1);

		text.getFont().setColor( 0, 0, 0, alpha);
		text.draw(sb, x, y);
    }
}

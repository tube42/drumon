package se.tube42.drum.view;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.Input.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.util.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
// import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;


public class ButtonItem extends PadItem
{
    private String text;
    private float tw, th;
    private BitmapFont font;
    private boolean active;
    
    public ButtonItem(String text)
    {
        super(World.tex_rect, 0);  
        
        this.font = World.font2;
        this.active = true;
        setColor(0xFFFFFF);
        setText(text);
    }
        
    // --------------------------------------
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
        setAlpha(active ? 1 : 0.4f);
    }
        
    public void setText(String text)
    {
        if(text != null) {
            
            // since our font is lower-case only...            
            text = text.toLowerCase();
            setAlpha(1);
            
            // calc boundss
            BitmapFont.TextBounds tb;
            tb = font.getBounds(text);
            th = tb.height;
            tw = tb.width;
            
            this.text = text;
        } else {
            setAlpha(0.4f);
            tw = th = 1;
            this.text = null;
        }
    }
    
    public void draw(SpriteBatch sb)
    {
        super.draw(sb);
                
        if(this.text != null) {            
            final int x = (int)(getX() + (w - tw) / 2);
            final int y = (int)(getY() + (h + th) / 2);
            final float alpha = getAlpha();
            
            font.setColor( 0.5f, 0.5f, 0.5f, alpha);
            font.draw(sb, text, x+1, y+1);
            
            font.setColor( 0, 0, 0, alpha);
            font.draw(sb, text, x, y);
        }
    }    
}

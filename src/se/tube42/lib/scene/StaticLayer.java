
package se.tube42.lib.scene;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;
import se.tube42.lib.item.*;

public class StaticLayer extends Layer
{
    private BaseItem [] array;
    public StaticLayer(BaseItem [] array)
    {
        super(null);
        this.array = array;
    }
    
    // disabled
    public Layer add(BaseItem [] bi) { return this; }
    public Layer add(BaseItem bi) { return this; }
    public void removeIfState(int state) { }
    
    
    public int getSize()
    {
        return array.length;
    }
    
    public BaseItem get(int index)
    {
        return array[index];
    }
}

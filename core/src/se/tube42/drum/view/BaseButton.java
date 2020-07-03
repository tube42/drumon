package se.tube42.drum.view;


import se.tube42.lib.item.*;


public abstract class BaseButton extends BaseItem
{
    /* package */ boolean active, hasLongpress;
    /* package */ long timeDown;
	/* package */ int class_, id;
	protected int animtype;

    public BaseButton()
    {
        this.active = true;
        this.timeDown = -1;
		this.flags |= BaseItem.FLAG_TOUCHABLE;
		setAnimType(0);
    }

    public void register(int class_, int id, boolean hasLongpress)
    {
        this.class_ = class_;
        this.id = id;
        this.hasLongpress = hasLongpress;
	}

	// animation actions for this button
	public abstract void animPress();
	public abstract void animAction(float amount);

	public void setAnimType(int t)
	{
		this.animtype = t;
	}
}

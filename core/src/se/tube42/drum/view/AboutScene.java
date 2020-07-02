package se.tube42.drum.view;

import com.badlogic.gdx.Input.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.item.*;

import se.tube42.drum.data.*;
import se.tube42.drum.logic.*;

import static se.tube42.drum.data.Constants.*;

public class AboutScene extends Scene {

	private Layer layer;
	private ButtonItem close, code;
	private BaseText text;

	public AboutScene() {
		super("about");

		text = new BaseText(World.font2);
		text.setAlignment(-0.5f, 0.5f);
		text.setText(ABOUT_TEXT);

		code = new ButtonItem("github page");
		close = new ButtonItem("close");
		close.setColor(COLOR_BUTTON_CLOSE);

		layer = getLayer(0);
		layer.add(close, code, text);
	}

	public void onShow() {
		super.onShow();
		animate(true);
	}

	public void onHide() {
		super.onHide();
		animate(false);
	}


	private void animate(boolean in_)
	{
		// TODO
	}

	public void resize(int w, int h)
	{
		final int size = World.size_button;
        final int gap = size / 2;

		text.setMaxWidth(w - 2 * gap);
		text.setPosition(w / 2, h / 2 + (size + 2 * gap) / 2);
		text.setSize( w - 2 * gap, h);

		close.setSize( w - 2 * gap, size);
		close.setPosition(gap, gap);

		code.setSize( w - 2 * gap, size);
		code.setPosition(gap, gap + close.getY() + close.getH());
	}


	// ----------------------------------------------------------

	public void go_back() {
		World.mgr.setScene(World.scene_drum, 200);
	}

	public boolean type(int key, boolean down) {
		if (down) {
			if (key == Keys.BACK || key == Keys.ESCAPE) {
				go_back();
				return true;
			}
		}
		return false;
	}

	public boolean touch(int p, int x, int y, boolean down, boolean drag) {
		if (down && !drag) {
			BaseItem hit = layer.hit(x, y);
			if (hit == close)
				go_back();
			else if(hit == code)
				SystemService.getInstance().showURL(GITHUB_PAGE);
		} else if (!down) {

		}

		return true;
	}

}


package se.tube42.drum.desktop;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;

import se.tube42.drum.*;
import se.tube42.drum.logic.SystemService;

public class DesktopMain
{
    public static void main(String[] args )
    {

        SystemService.setInstance(new DesktopService());

        DrumApp app = new DrumApp();
        new LwjglApplication( app, "Drum On...", 400, 640);
    }

}

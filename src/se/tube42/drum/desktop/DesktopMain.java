
package se.tube42.drum.desktop;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;

import se.tube42.drum.*;

public class DesktopMain
{
    public static void main(String[] args )
    {
        DrumApp app = new DrumApp();
        new LwjglApplication( app, "Drum On...", 480, 640);
    }
    
}

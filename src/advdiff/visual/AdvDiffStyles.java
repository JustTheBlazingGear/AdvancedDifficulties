package advdiff.visual;

import arc.Core;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

public class AdvDiffStyles {
    public static Drawable accent;
    public static ImageButton.ImageButtonStyle difficulties,
            cruxhard, cruxeradication, cruxunreasonable,
            malishard, maliseradication, malisunreasonable;

    public AdvDiffStyles() {
    }

    public static void load() {
        //no idea what all this is tbh, I coded this in 2024 before official difficulties were even a thing
        final TextureRegionDrawable whiteui = (TextureRegionDrawable)Tex.whiteui;
        accent = whiteui.tint(1.0F, 0.82F, 0.49F, 1.0F);
        difficulties = new ImageButton.ImageButtonStyle() {
            {
                up = Tex.pane;
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.gray;  //still
                imageOverColor = Color.white;  //hover
                imageDownColor = Color.lightGray;  //click
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
        cruxhard = new ImageButton.ImageButtonStyle() {
            {
                up = Core.atlas.drawable("advdiff-crux-hard-borders");
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.HSVtoRGB(0,0,0,0);
                imageOverColor = Color.white;
                imageDownColor = Color.lightGray;
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
        cruxeradication = new ImageButton.ImageButtonStyle() {
            {
                up = Core.atlas.drawable("advdiff-crux-eradication-borders");
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.HSVtoRGB(0,0,0,0);
                imageOverColor = Color.white;
                imageDownColor = Color.lightGray;
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
        cruxunreasonable = new ImageButton.ImageButtonStyle() {
            {
                up = Core.atlas.drawable("advdiff-crux-unreasonable-borders");
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.HSVtoRGB(0,0,0,0);
                imageOverColor = Color.white;
                imageDownColor = Color.lightGray;
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
        malishard = new ImageButton.ImageButtonStyle() {
            {
                up = Core.atlas.drawable("advdiff-malis-hard-borders");
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.HSVtoRGB(0,0,0,0);
                imageOverColor = Color.white;
                imageDownColor = Color.lightGray;
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
        maliseradication = new ImageButton.ImageButtonStyle() {
            {
                up = Core.atlas.drawable("advdiff-malis-eradication-borders");
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.HSVtoRGB(0,0,0,0);
                imageOverColor = Color.white;
                imageDownColor = Color.lightGray;
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
        malisunreasonable = new ImageButton.ImageButtonStyle() {
            {
                up = Core.atlas.drawable("advdiff-malis-unreasonable-borders");
                over = Styles.flatDown;
                down = accent;
                imageUpColor = Color.HSVtoRGB(0,0,0,0);
                imageOverColor = Color.white;
                imageDownColor = Color.lightGray;
                checked = Styles.flatDown;
                imageCheckedColor = Color.white;
            }
        };
    }
}
package advdiff;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Rand;
import mindustry.entities.Effect;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class AdvDiffFx {
    public static final Rand rand = new Rand();

    public static final Effect
    radiance = new Effect(25f, b -> {
        //Will stay as whatever the fuck this is untill I make a fancy outline like boss has but rainbow
        for(int i = 0; i < 1; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.5f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(), 3, 0, (x, y, in, out) -> {
                    float rad = e.fout() * 2f;
                    color(rand.random(0,1),rand.random(0,1),rand.random(0,1));
                    if (b.color == Color.black) b.color = Color.cyan;
                    if (b.color == Color.white) b.color = Color.magenta;
                    Fill.square(e.x + x, e.y + y, rad);
                });
            });
        }
    });
}

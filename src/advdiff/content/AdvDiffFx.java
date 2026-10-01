package advdiff.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class AdvDiffFx {
    public static final Rand rand = new Rand();

    public static final Effect
    radiance = new Effect(50f, b -> {
        for(int i = 0; i < 1; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.5f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(), 3, 0, (x, y, in, out) -> {
                    float rad = e.fout() * 2f;
                    float hue = (Time.time * 2.5f + e.fout() * 360) % 360f;
                    Tmp.c1.fromHsv(hue, 1f, 1f);
                    Draw.color(Tmp.c1);
                    Fill.square(e.x + x, e.y + y, rad);
                });
            });
        }
    });
}

package advdiff.visual;

import arc.graphics.g2d.*;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

import static arc.math.Mathf.random;

public class RadianceStatus extends StatusEffect {
    public float glintAlpha;
    public float baseSize;

    public RadianceStatus(String name) {
        super(name);
        glintAlpha = 1.0F;
        baseSize = 1.35F;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        float hue = (int)((Time.time * (2.5f + (unit.id % 250f) / 250) + unit.id % 360f)*baseSize) % 360f;
        Tmp.c1.fromHsv(hue, 1f, 1f);
        Draw.color(Tmp.c1.a(glintAlpha));
        TextureRegion tex = new TextureRegion(unit.type.region);
        TextureRegion tex2 = new TextureRegion(unit.type.region);
        tex2.scale = baseSize * 1.2F - unit.hitSize / 2000F;
        float offset = unit.hitSize / 30 + 0.35f;

        float layer = unit.type.groundLayer;
        if (unit.type.flying) layer = Layer.flyingUnitLow;

        Drawf.additive(tex, Tmp.c1.cpy().a(glintAlpha), unit.x - offset, unit.y - offset, unit.rotation - 90, layer);
        Drawf.additive(tex, Tmp.c1.cpy().a(glintAlpha), unit.x + offset, unit.y + offset, unit.rotation - 90, layer);
        Drawf.additive(tex, Tmp.c1.cpy().a(glintAlpha), unit.x + offset, unit.y - offset, unit.rotation - 90, layer);
        Drawf.additive(tex, Tmp.c1.cpy().a(glintAlpha), unit.x - offset, unit.y + offset, unit.rotation - 90, layer);
        Drawf.additive(tex2, Tmp.c1.cpy().a(glintAlpha/2), unit.x, unit.y, unit.rotation - 90, layer);
        Draw.reset();
    }
}

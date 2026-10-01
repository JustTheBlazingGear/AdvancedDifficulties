package advdiff.visual;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

public class DegradationStatus extends StatusEffect {
    public float glintAlpha;
    public float baseSize;

    public DegradationStatus(String name) {
        super(name);
        glintAlpha = 0.4F;
        baseSize = 1F;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        Draw.mixcol(Color.valueOf("7f2626"), Color.valueOf("26040a"), Mathf.absin(15f + baseSize * 3 + (unit.id % 15f) / 15, 1f));
        TextureRegion tex = new TextureRegion(unit.type.region);
        tex.scale = baseSize;
        float offset = (unit.hitSize / 30 + 0.35f) * (Mathf.absin(15f * 3 + (unit.id % 15f) / 15, 0.5f)+0.5f);

        Draw.z(unit.type.groundLayer);
        if (unit.type.flying) Draw.z(Layer.flyingUnitLow);

        Draw.rect(tex, unit.x - offset, unit.y - offset, unit.rotation - 90);
        Draw.rect(tex, unit.x + offset, unit.y + offset, unit.rotation - 90);
        Draw.rect(tex, unit.x + offset, unit.y - offset, unit.rotation - 90);
        Draw.rect(tex, unit.x - offset, unit.y + offset, unit.rotation - 90);
    }
}

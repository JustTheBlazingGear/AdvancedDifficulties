package advdiff.content;

import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.MechUnit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class AdvDiffUnitTypes {

    public static UnitType
            butterKnife, flea, laserPointer, glimpse, zero, plankton, slug;

    public static void load(){

        //region T0

        butterKnife = new UnitType("butter-knife"){{
            constructor = MechUnit::create;
            outlines = false;
            flying = false;
            researchCostMultiplier = 0.5f;
            speed = 0.5f;
            hitSize = 6f;
            health = 50;
            stepSoundVolume = 0.2f;
            itemCapacity = 0;

            weapons.add(new Weapon("advdiff-butter-knife-weapon"){{
                reload = 60f;
                x = 0;
                y = 0;
                top = true;
                layerOffset = -0.00001f;
                mirror = false;
                ejectEffect = Fx.casing1;
                bullet = new BasicBulletType(2.5f, 9){{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                }};
            }});
        }};
    }
}

package me.wesley1808.servercore.common.activation_range;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Collection of entity type tests for the activation range configuration.
 */
public class EntityTypeTests {
    public static final EntityTypeTest<?, ?> MOB = EntityTypeTest.forClass(Mob.class);
    public static final EntityTypeTest<?, ?> MONSTER = EntityTypeTest.forClass(Enemy.class);
    public static final EntityTypeTest<?, ?> RAIDER = EntityTypeTest.forClass(Raider.class);
    public static final EntityTypeTest<?, ?> AMBIENT = EntityTypeTest.forClass(AmbientCreature.class);
    public static final EntityTypeTest<?, ?> ANIMAL = EntityTypeTest.forClass(AgeableMob.class);
    public static final EntityTypeTest<?, ?> NEUTRAL = EntityTypeTest.forClass(NeutralMob.class);
    public static final EntityTypeTest<?, ?> WATER_ANIMAL = EntityTypeTest.forClass(WaterAnimal.class);
    public static final EntityTypeTest<?, ?> FLYING_ANIMAL = EntityTypeTest.forClass(FlyingAnimal.class);
    public static final EntityTypeTest<?, ?> FLYING_MONSTER = EntityTypeTest.forClass(FlyingMob.class);
    public static final EntityTypeTest<?, ?> VILLAGER = EntityTypeTest.forClass(Npc.class);
    private static final Map<String, EntityTypeTest<?, ?>> ENTITY_TYPE_TESTS = Map.of(
            "mob", MOB,
            "monster", MONSTER,
            "raider", RAIDER,
            "ambient", AMBIENT,
            "animal", ANIMAL,
            "neutral", NEUTRAL,
            "water_animal", WATER_ANIMAL,
            "flying_animal", FLYING_ANIMAL,
            "flying_monster", FLYING_MONSTER,
            "villager", VILLAGER
    );

    @Nullable
    public static EntityTypeTest<?, ?> get(String key) {
        return ENTITY_TYPE_TESTS.get(key);
    }

    @Nullable
    public static String getKey(EntityTypeTest<?, ?> entityTypeTest) {
        for (String key : ENTITY_TYPE_TESTS.keySet()) {
            EntityTypeTest<?, ?> value = ENTITY_TYPE_TESTS.get(key);
            if (value == entityTypeTest) {
                return key;
            }
        }
        return null;
    }
}

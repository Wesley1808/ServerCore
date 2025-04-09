package me.wesley1808.servercore.common.activation_range;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Collection of entity type tests for the activation range configuration.
 */
public class EntityTypeTests {
    private static final Map<String, EntityTypeTest<?, ?>> ENTITY_TYPE_TESTS = new Object2ObjectOpenHashMap<>();
    public static final EntityTypeTest<? super Entity, ?> MOB = register("mob", Mob.class);
    public static final EntityTypeTest<? super Entity, ?> MONSTER = register("monster", Enemy.class);
    public static final EntityTypeTest<? super Entity, ?> RAIDER = register("raider", Raider.class);
    public static final EntityTypeTest<? super Entity, ?> AMBIENT = register("ambient", AmbientCreature.class);
    public static final EntityTypeTest<? super Entity, ?> ANIMAL = register("animal", AgeableMob.class);
    public static final EntityTypeTest<? super Entity, ?> NEUTRAL = register("neutral", NeutralMob.class);
    public static final EntityTypeTest<? super Entity, ?> WATER_ANIMAL = register("water_animal", WaterAnimal.class);
    public static final EntityTypeTest<? super Entity, ?> FLYING_ANIMAL = register("flying_animal", FlyingAnimal.class);
    public static final EntityTypeTest<? super Entity, ?> VILLAGER = register("villager", Npc.class);
    public static final EntityTypeTest<? super Entity, ?> PROJECTILE = register("projectile", Projectile.class);

    public static EntityTypeTest<? super Entity, ?> register(String key, Class<?> clazz) {
        EntityTypeTest<? super Entity, ?> matcher = EntityTypeTest.forClass(clazz);
        ENTITY_TYPE_TESTS.put(key, matcher);
        return matcher;
    }

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

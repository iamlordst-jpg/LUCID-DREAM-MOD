package dev.luciddream;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.animal.Allay;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;

@Mod(LucidDreamMod.MOD_ID)
public final class LucidDreamMod {
    public static final String MOD_ID = "luciddream";
    private static final Random RANDOM = new Random();
    private static final Map<String, String> POWERS = new LinkedHashMap<>();

    static {
        String[][] rows = {
            {"flight","Dream Flight"},{"levitation","Levitation"},{"super_jump","Super Jump"},{"slow_fall","Featherfall"},{"speed_burst","Speed Burst"},
            {"water_walk","Walk on Water"},{"wall_phase","Phase Step"},{"blink","Blink Teleport"},{"long_blink","Far Blink"},{"return_mark","Return to Mark"},
            {"gravity_flip","Gravity Drift"},{"zero_gravity","Zero Gravity"},{"moon_jump","Moon Jump"},{"air_dash","Air Dash"},{"double_jump","Double Jump"},
            {"telekinesis","Telekinesis"},{"block_pull","Block Attraction"},{"block_push","Force Push"},{"item_summon","Pocket Manifestation"},{"item_shrink","Tiny Object"},
            {"item_giant","Giant Object"},{"illusion_decoy","Illusion Decoy"},{"mirror_gate","Mirror Gate"},{"dream_door","Dream Door"},{"pocket_room","Pocket Room"},
            {"time_slow","Slow Time"},{"time_haste","Haste Time"},{"moment_freeze","Moment Freeze"},{"echo_rewind","Echo Rewind"},{"future_glimpse","Future Glimpse"},
            {"night_vision","Dream Sight"},{"true_sight","Hidden Sight"},{"invisibility","Fade From Sight"},{"glow_aura","Lucid Aura"},{"shadow_form","Shadow Form"},
            {"shape_wolf","Wolf Shape"},{"shape_cat","Cat Shape"},{"shape_allay","Wisp Shape"},{"giant_form","Giant Form"},{"mini_form","Miniature Form"},
            {"heal_dream","Dream Mending"},{"regen_aura","Regeneration Aura"},{"resistance","Unshakable"},{"absorb_impact","Impact Absorption"},{"second_wind","Second Wind"},
            {"water_breath","Ocean Breath"},{"fire_dream","Flame Dream"},{"frost_dream","Frost Dream"},{"storm_call","Storm Calling"},{"thunder_step","Thunder Step"},
            {"wind_burst","Wind Burst"},{"sonic_wave","Sonic Wave"},{"dream_pulse","Dream Pulse"},{"starfall","Starfall"},{"meteor_dream","Meteor Dream"},
            {"gravity_well","Gravity Well"},{"singularity","Pocket Singularity"},{"orbiting_stars","Orbiting Stars"},{"prism_shield","Prism Shield"},{"bubble_barrier","Bubble Barrier"},
            {"fearless","Fearless Mind"},{"calm_field","Calm Field"},{"sleeping_song","Lullaby Field"},{"confusion_mist","Confusion Mist"},{"dream_mark","Dream Mark"},
            {"summon_guide","Summon Dream Guide"},{"summon_wisp","Summon Wisp"},{"summon_cat","Summon Dream Cat"},{"summon_wolf","Summon Dream Wolf"},{"summon_sentinel","Summon Sentinel"},
            {"nightmare_bell","Nightmare Bell"},{"nightmare_lure","Nightmare Lure"},{"banish_phantom","Banish Phantom"},{"warding_circle","Warding Circle"},{"dream_duel","Dream Duel"},
            {"terrain_raise","Raise Terrain"},{"terrain_lower","Lower Terrain"},{"flower_bloom","Instant Bloom"},{"tree_dream","Dream Tree"},{"crystal_growth","Crystal Growth"},
            {"color_shift","Color Shift"},{"dawn_call","Call the Dawn"},{"eclipse","Dream Eclipse"},{"aurora","Aurora Veil"},{"rainbow_bridge","Rainbow Bridge"},
            {"dream_weather","Dream Weather"},{"rain_stop","Clear the Skies"},{"fog_weave","Weave Fog"},{"light_orb","Light Orb"},{"darkness_drape","Darkness Drape"},
            {"soundless_step","Soundless Step"},{"echo_clone","Echo Clone"},{"dream_double","Dream Double"},{"memory_palace","Memory Palace"},{"lucidity_surge","Lucidity Surge"},
            {"wake_anchor","Wake Anchor"},{"dream_anchor","Dream Anchor"},{"reality_ripple","Reality Ripple"},{"world_flip","World Flip"},{"lucid_overdrive","Lucid Overdrive"}
        };
        for (String[] row : rows) POWERS.put(row[0], row[1]);
    }

    public LucidDreamMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("dreampower")
            .then(Commands.literal("list").executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                player.sendSystemMessage(Component.literal("§dLucid Dream Simulator — " + POWERS.size() + " powers"));
                StringBuilder line = new StringBuilder();
                for (Map.Entry<String, String> power : POWERS.entrySet()) {
                    if (line.length() > 0) line.append(" §7• §f");
                    line.append(power.getKey()).append(" §8(").append(power.getValue()).append(")");
                    if (line.length() > 170) { player.sendSystemMessage(Component.literal(line.toString())); line.setLength(0); }
                }
                if (line.length() > 0) player.sendSystemMessage(Component.literal(line.toString()));
                return POWERS.size();
            }))
            .then(Commands.literal("use").then(Commands.argument("power", StringArgumentType.word())
                .suggests((ctx, builder) -> { POWERS.keySet().forEach(builder::suggest); return builder.buildFuture(); })
                .executes(ctx -> activate(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "power")))))
        );
    }

    private static int activate(ServerPlayer p, String id) {
        String name = POWERS.get(id);
        if (name == null) { p.sendSystemMessage(Component.literal("§cUnknown power. Try /dreampower list")); return 0; }
        Level level = p.serverLevel();
        Vec3 look = p.getLookAngle();
        switch (id) {
            case "flight": case "levitation": case "zero_gravity": case "gravity_flip":
                p.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, id.equals("flight") ? 1 : 2)); break;
            case "super_jump": case "moon_jump": case "double_jump":
                p.addEffect(new MobEffectInstance(MobEffects.JUMP, 500, id.equals("moon_jump") ? 4 : 2)); p.setDeltaMovement(p.getDeltaMovement().add(0, 0.9, 0)); break;
            case "slow_fall": case "air_dash": case "wind_burst":
                p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 500, 0)); p.setDeltaMovement(p.getDeltaMovement().add(look.scale(1.4))); break;
            case "speed_burst": case "thunder_step": case "blink": case "long_blink":
                Vec3 dest = p.position().add(look.scale(id.equals("long_blink") ? 16 : 6));
                p.teleportTo(dest.x, dest.y, dest.z); p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 2)); break;
            case "night_vision": case "true_sight": case "future_glimpse":
                p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0)); break;
            case "invisibility": case "shadow_form": case "fade_from_sight":
                p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 400, 0)); break;
            case "heal_dream": case "regen_aura": case "second_wind":
                p.heal(12); p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1)); break;
            case "resistance": case "absorb_impact": case "prism_shield": case "bubble_barrier":
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 2)); p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 2)); break;
            case "water_breath":
                p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 1200, 0)); break;
            case "fearless": case "calm_field": case "lucidity_surge":
                p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1, 0)); p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0)); break;
            case "summon_guide": case "summon_wisp": case "summon_allay":
                spawn(EntityType.ALLAY, p); break;
            case "summon_cat": case "shape_cat":
                spawn(EntityType.CAT, p); break;
            case "summon_wolf": case "shape_wolf":
                spawn(EntityType.WOLF, p); break;
            case "nightmare_bell": case "nightmare_lure": case "summon_sentinel":
                spawn(EntityType.PHANTOM, p); break;
            case "banish_phantom":
                level.getEntitiesOfClass(Phantom.class, p.getBoundingBox().inflate(24)).forEach(Entity::discard); break;
            case "dream_duel":
                spawn(EntityType.ENDERMAN, p); break;
            case "sonic_wave": case "dream_pulse": case "reality_ripple": case "gravity_well":
                for (Entity e : level.getEntities(p, p.getBoundingBox().inflate(8), e -> e instanceof Mob)) {
                    e.push(look.x * 1.5, 0.6, look.z * 1.5);
                } break;
            case "dawn_call": case "rain_stop":
                level.setDayTime(1000); level.setWeatherParameters(6000, 0, false, false); break;
            case "eclipse": case "darkness_drape":
                level.setDayTime(18000); level.setWeatherParameters(6000, 0, true, true); break;
            case "dream_weather": case "storm_call":
                level.setWeatherParameters(12000, 0, true, true); break;
            case "lucid_overdrive":
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 3));
                p.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 3));
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1)); break;
            default:
                // Every catalogue entry has a distinct command ID; safe vanilla mechanics provide
                // an immediate visual/gameplay response while specialized implementations grow.
                p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0));
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 0));
                break;
        }
        p.sendSystemMessage(Component.literal("§d✦ §fDream power: §b" + name));
        return 1;
    }

    private static void spawn(EntityType<? extends Mob> type, ServerPlayer player) {
        Mob entity = type.create(player.serverLevel());
        if (entity != null) {
            entity.moveTo(player.getX() + player.getLookAngle().x * 2, player.getY(), player.getZ() + player.getLookAngle().z * 2, player.getYRot(), 0);
            entity.finalizeSpawn(player.serverLevel(), player.serverLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            player.serverLevel().addFreshEntity(entity);
        }
    }
}

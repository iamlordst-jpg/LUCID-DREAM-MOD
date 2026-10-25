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
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.LinkedHashMap;

@Mod(LucidDreamMod.MOD_ID)
public final class LucidDreamMod {
    public static final String MOD_ID = "luciddream";
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

    public static Map<String, String> getPowers() { return java.util.Collections.unmodifiableMap(POWERS); }

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
        String name=POWERS.get(id);
        if(name==null){p.sendSystemMessage(Component.literal("§cUnknown dream power."));return 0;}
        ServerLevel level=p.serverLevel(); Vec3 look=p.getLookAngle();
        switch(id){
            case "flight": { p.addEffect(new MobEffectInstance(MobEffects.LEVITATION,100,1)); p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,1200,0)); break; }
            case "levitation": { p.addEffect(new MobEffectInstance(MobEffects.LEVITATION,240,2)); break; }
            case "super_jump": { p.setDeltaMovement(p.getDeltaMovement().add(0,1.6,0)); p.addEffect(new MobEffectInstance(MobEffects.JUMP,300,2)); break; }
            case "slow_fall": { p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,1200,0)); break; }
            case "speed_burst": { p.setDeltaMovement(look.scale(2.8).add(0,0.2,0)); p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,100,2)); break; }
            case "water_walk": { for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++){var b=p.blockPosition().offset(x,-1,z);if(level.getBlockState(b).isAir())level.setBlockAndUpdate(b,net.minecraft.world.level.block.Blocks.FROSTED_ICE.defaultBlockState());} break; }
            case "wall_phase": { teleportForward(p,3); break; }
            case "blink": { teleportForward(p,6); break; }
            case "long_blink": { teleportForward(p,14); break; }
            case "return_mark": { if(p.getRespawnPosition()!=null)p.teleportTo(p.getRespawnPosition().getX()+0.5,p.getRespawnPosition().getY()+1,p.getRespawnPosition().getZ()+0.5);else p.sendSystemMessage(Component.literal("§7Set a bed or use Dream Mark first.")); break; }
            case "gravity_flip": { p.setDeltaMovement(p.getDeltaMovement().add(0,-1.1,0)); p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,200,0)); break; }
            case "zero_gravity": { p.setDeltaMovement(p.getDeltaMovement().add(0,0.5,0)); p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,500,0)); break; }
            case "moon_jump": { p.setDeltaMovement(p.getDeltaMovement().add(0,2.2,0)); break; }
            case "air_dash": { p.setDeltaMovement(look.scale(3.5)); p.fallDistance=0; break; }
            case "double_jump": { p.setDeltaMovement(p.getDeltaMovement().add(0,1.15,0)); p.fallDistance=0; break; }
            case "telekinesis": { forceTarget(p,2.8,0.8,false); break; }
            case "block_pull": { forceTarget(p,1.8,0.35,true); break; }
            case "block_push": { forceTarget(p,3.2,0.8,false); break; }
            case "item_summon": { p.addItem(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND,1)); break; }
            case "item_shrink": { p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,500,1)); p.addEffect(new MobEffectInstance(MobEffects.JUMP,500,1)); break; }
            case "item_giant": { p.addEffect(new MobEffectInstance(MobEffects.STRENGTH,500,1)); p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,500,1)); break; }
            case "illusion_decoy": { spawn(EntityType.ALLAY,p); level.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF,p.getX(),p.getY()+1,p.getZ(),35,0.6,0.8,0.6,0.08); break; }
            case "mirror_gate": { teleportForward(p,9); level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,p.getX(),p.getY()+1,p.getZ(),60,0.7,1,0.7,0.2); break; }
            case "dream_door": { teleportForward(p,5); break; }
            case "pocket_room": { p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,1200,4)); p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,200,0)); break; }
            case "time_slow": { for(Mob e:nearbyMobs(p,10))e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,240,3)); break; }
            case "time_haste": { p.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,600,2)); p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,600,1)); break; }
            case "moment_freeze": { for(Mob e:nearbyMobs(p,8))e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,10)); break; }
            case "echo_rewind": { p.setDeltaMovement(p.getDeltaMovement().scale(-1)); p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,100,0)); break; }
            case "future_glimpse": { p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,600,0)); p.addEffect(new MobEffectInstance(MobEffects.LUCK,600,1)); break; }
            case "night_vision": { p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,2400,0)); break; }
            case "true_sight": { for(Mob e:nearbyMobs(p,24))e.addEffect(new MobEffectInstance(MobEffects.GLOWING,400,0)); break; }
            case "invisibility": { p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,600,0)); break; }
            case "glow_aura": { p.addEffect(new MobEffectInstance(MobEffects.GLOWING,1200,0)); level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,p.getX(),p.getY()+1,p.getZ(),40,0.7,1,0.7,0.04); break; }
            case "shadow_form": { p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,240,0)); p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,240,2)); break; }
            case "shape_wolf": { p.addEffect(new MobEffectInstance(MobEffects.STRENGTH,600,1)); p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,600,1)); break; }
            case "shape_cat": { p.addEffect(new MobEffectInstance(MobEffects.JUMP,800,2)); p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,800,0)); break; }
            case "shape_allay": { p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,600,0)); p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,160,0)); break; }
            case "giant_form": { p.addEffect(new MobEffectInstance(MobEffects.STRENGTH,600,2)); p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,600,1)); break; }
            case "mini_form": { p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,600,2)); p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,100,0)); break; }
            case "heal_dream": { p.heal(20); p.getFoodData().setFoodLevel(20); break; }
            case "regen_aura": { p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,600,1)); break; }
            case "resistance": { p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,800,2)); break; }
            case "absorb_impact": { p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,1000,4)); p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,600,0)); break; }
            case "second_wind": { p.heal(8); p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,400,1)); p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,1)); break; }
            case "water_breath": { p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,2400,0)); p.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE,800,0)); break; }
            case "fire_dream": { p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,1200,0)); level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,p.getX(),p.getY()+1,p.getZ(),50,0.6,0.7,0.6,0.04); break; }
            case "frost_dream": { for(Mob e:nearbyMobs(p,8))e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,240,2)); level.sendParticles(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,p.getX(),p.getY()+1,p.getZ(),70,2,1,2,0.02); break; }
            case "storm_call": { level.setWeatherParameters(12000,0,true,true); break; }
            case "thunder_step": { teleportForward(p,5); var bolt=EntityType.LIGHTNING_BOLT.create(level); if(bolt!=null){bolt.moveTo(p.getX(),p.getY(),p.getZ());level.addFreshEntity(bolt);} break; }
            case "wind_burst": { p.setDeltaMovement(look.scale(2.2).add(0,0.8,0)); for(Mob e:nearbyMobs(p,5))e.push(look.x*1.6,0.6,look.z*1.6); break; }
            case "sonic_wave": { for(Mob e:nearbyMobs(p,12)){e.push(look.x*2,0.7,look.z*2);e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,1));} level.sendParticles(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,p.getX()+look.x*2,p.getY()+1,p.getZ()+look.z*2,1,0,0,0,0); break; }
            case "dream_pulse": { for(Mob e:nearbyMobs(p,7))e.push(e.getX()-p.getX(),0.9,e.getZ()-p.getZ()); level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,p.getX(),p.getY()+1,p.getZ(),80,2,1,2,0.1); break; }
            case "starfall": { for(int i=0;i<6;i++){var b=p.blockPosition().offset((i%3)-1,3+i%2,(i/3)*2-1);level.setBlockAndUpdate(b,net.minecraft.world.level.block.Blocks.GLOWSTONE.defaultBlockState());} break; }
            case "meteor_dream": { for(Mob e:nearbyMobs(p,10)){e.hurt(level.damageSources().magic(),8);e.setSecondsOnFire(4);} level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,p.getX(),p.getY()+1,p.getZ(),100,3,2,3,0.08); break; }
            case "gravity_well": { for(Mob e:nearbyMobs(p,10))e.setDeltaMovement(p.position().subtract(e.position()).normalize().scale(1.2)); break; }
            case "singularity": { for(Mob e:nearbyMobs(p,7)){e.hurt(level.damageSources().magic(),5);e.setDeltaMovement(p.position().subtract(e.position()).normalize().scale(2));} level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,p.getX(),p.getY()+1,p.getZ(),100,2,2,2,0.4); break; }
            case "orbiting_stars": { level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,p.getX(),p.getY()+1,p.getZ(),120,1.5,1,1.5,0.15); p.addEffect(new MobEffectInstance(MobEffects.GLOWING,600,0)); break; }
            case "prism_shield": { p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,800,3)); p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,400,1)); level.sendParticles(net.minecraft.core.particles.ParticleTypes.WAX_ON,p.getX(),p.getY()+1,p.getZ(),50,0.8,1,0.8,0.1); break; }
            case "bubble_barrier": { p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,600,0)); p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,600,1)); level.sendParticles(net.minecraft.core.particles.ParticleTypes.BUBBLE,p.getX(),p.getY()+1,p.getZ(),90,1,1,1,0.08); break; }
            case "fearless": { p.removeEffect(MobEffects.DIG_SLOWDOWN); p.removeEffect(MobEffects.MOVEMENT_SLOWDOWN); p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,400,0)); break; }
            case "calm_field": { for(Mob e:nearbyMobs(p,9))e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,160,1)); p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,240,0)); break; }
            case "sleeping_song": { for(Mob e:nearbyMobs(p,9))e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,300,1)); level.sendParticles(net.minecraft.core.particles.ParticleTypes.NOTE,p.getX(),p.getY()+1,p.getZ(),40,2,1,2,0.1); break; }
            case "confusion_mist": { for(Mob e:nearbyMobs(p,10)){e.addEffect(new MobEffectInstance(MobEffects.CONFUSION,240,0));e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,240,0));} break; }
            case "dream_mark": { p.setRespawnPosition(level.dimension(),p.blockPosition(),p.getYRot(),true,false); level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,p.getX(),p.getY()+1,p.getZ(),40,0.5,1,0.5,0.03); break; }
            case "summon_guide": { spawn(EntityType.ALLAY,p); p.addEffect(new MobEffectInstance(MobEffects.LUCK,600,0)); break; }
            case "summon_wisp": { spawn(EntityType.ALLAY,p); level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,p.getX()+look.x*2,p.getY()+1,p.getZ()+look.z*2,35,0.4,0.7,0.4,0.04); break; }
            case "summon_cat": { spawn(EntityType.CAT,p); break; }
            case "summon_wolf": { spawn(EntityType.WOLF,p); break; }
            case "summon_sentinel": { spawn(EntityType.IRON_GOLEM,p); break; }
            case "nightmare_bell": { for(Mob e:nearbyMobs(p,12))e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,300,1)); level.playSound(null,p.blockPosition(),net.minecraft.sounds.SoundEvents.BELL_BLOCK,p.getSoundSource(),2,0.6f); break; }
            case "nightmare_lure": { spawn(EntityType.PHANTOM,p); break; }
            case "banish_phantom": { int removed=0; for(Phantom e:level.getEntitiesOfClass(Phantom.class,p.getBoundingBox().inflate(48))){e.discard();removed++;} p.sendSystemMessage(Component.literal("§dBanished "+removed+" phantom(s).")); break; }
            case "warding_circle": { p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,500,2)); for(Mob e:nearbyMobs(p,5))e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,160,2)); break; }
            case "dream_duel": { spawn(EntityType.ZOMBIE,p); p.addEffect(new MobEffectInstance(MobEffects.STRENGTH,300,1)); break; }
            case "terrain_raise": { for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){var b=p.blockPosition().offset(x,-1,z);if(level.getBlockState(b).isAir())level.setBlockAndUpdate(b,net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState());} break; }
            case "terrain_lower": { for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){var b=p.blockPosition().offset(x,-1,z);if(level.getBlockState(b).isSolid())level.destroyBlock(b,true,p);} break; }
            case "flower_bloom": { for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++){var b=p.blockPosition().offset(x,0,z);if(level.getBlockState(b).isAir()&&level.getBlockState(b.below()).isSolid())level.setBlockAndUpdate(b,net.minecraft.world.level.block.Blocks.POPPY.defaultBlockState());} break; }
            case "tree_dream": { var t=p.blockPosition().relative(p.getDirection(),2);for(int y=0;y<4;y++)level.setBlockAndUpdate(t.above(y),net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState());for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)for(int y=3;y<=5;y++)if(Math.abs(x)+Math.abs(z)+(y-4)<5)level.setBlockAndUpdate(t.offset(x,y,z),net.minecraft.world.level.block.Blocks.OAK_LEAVES.defaultBlockState()); break; }
            case "crystal_growth": { for(int i=1;i<=3;i++)level.setBlockAndUpdate(p.blockPosition().relative(p.getDirection(),i),net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK.defaultBlockState()); level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,p.getX()+look.x*2,p.getY()+1,p.getZ()+look.z*2,50,0.4,0.7,0.4,0.04); break; }
            case "color_shift": { level.sendParticles(net.minecraft.core.particles.ParticleTypes.WITCH,p.getX(),p.getY()+1,p.getZ(),100,1.5,1,1.5,0.1); p.addEffect(new MobEffectInstance(MobEffects.GLOWING,300,0)); break; }
            case "dawn_call": { level.setDayTime(1000); level.setWeatherParameters(6000,0,false,false); break; }
            case "eclipse": { level.setDayTime(18000); level.setWeatherParameters(12000,0,true,true); level.sendParticles(net.minecraft.core.particles.ParticleTypes.ASH,p.getX(),p.getY()+2,p.getZ(),100,3,2,3,0.02); break; }
            case "aurora": { level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,p.getX(),p.getY()+5,p.getZ(),180,5,1,5,0.06); p.addEffect(new MobEffectInstance(MobEffects.LUCK,600,1)); break; }
            case "rainbow_bridge": { for(int i=1;i<=8;i++){var b=p.blockPosition().relative(p.getDirection(),i).below();if(level.getBlockState(b).isAir())level.setBlockAndUpdate(b,net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState());} break; }
            case "dream_weather": { level.setWeatherParameters(10000,0,true,false); level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,p.getX(),p.getY()+2,p.getZ(),100,4,2,4,0.02); break; }
            case "rain_stop": { level.setWeatherParameters(12000,0,false,false); break; }
            case "fog_weave": { level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,p.getX(),p.getY()+1,p.getZ(),180,4,2,4,0.01); for(Mob e:nearbyMobs(p,8))e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,100,0)); break; }
            case "light_orb": { var b=p.blockPosition().relative(p.getDirection(),3);level.setBlockAndUpdate(b,net.minecraft.world.level.block.Blocks.LANTERN.defaultBlockState());level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,b.getX()+0.5,b.getY()+0.5,b.getZ()+0.5,30,0.3,0.3,0.3,0.02); break; }
            case "darkness_drape": { p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,80,0)); for(Mob e:nearbyMobs(p,7))e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,100,0)); break; }
            case "soundless_step": { p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,600,1)); p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,100,0)); break; }
            case "echo_clone": { spawn(EntityType.ARMOR_STAND,p); level.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF,p.getX()+look.x*2,p.getY()+1,p.getZ()+look.z*2,50,0.5,1,0.5,0.05); break; }
            case "dream_double": { p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,600,2)); p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,300,0)); break; }
            case "memory_palace": { p.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,800,2)); p.addEffect(new MobEffectInstance(MobEffects.LUCK,800,1)); p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,800,0)); break; }
            case "lucidity_surge": { p.removeEffect(MobEffects.CONFUSION);p.removeEffect(MobEffects.BLINDNESS);p.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,300,1)); break; }
            case "wake_anchor": { p.setRespawnPosition(level.dimension(),p.blockPosition(),p.getYRot(),true,false);p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,300,1)); break; }
            case "dream_anchor": { p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,1200,0));p.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,600,1)); break; }
            case "reality_ripple": { for(Mob e:nearbyMobs(p,9)){e.setDeltaMovement(e.getDeltaMovement().add(0,1.3,0));e.addEffect(new MobEffectInstance(MobEffects.GLOWING,200,0));} level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,p.getX(),p.getY()+1,p.getZ(),90,2,1,2,0.15); break; }
            case "world_flip": { p.setDeltaMovement(p.getDeltaMovement().add(0,1.7,0));p.setYRot(p.getYRot()+180);p.setXRot(-p.getXRot());level.sendParticles(net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,p.getX(),p.getY()+1,p.getZ(),80,1,1,1,0.12); break; }
            case "lucid_overdrive": { p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,1200,3));p.addEffect(new MobEffectInstance(MobEffects.JUMP,1200,3));p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,1200,1));p.addEffect(new MobEffectInstance(MobEffects.STRENGTH,1200,1)); break; }
            default: return 0;
        }
        p.sendSystemMessage(Component.literal("§d✦ §fDream power: §b"+name));
        return 1;
    }

    private static void teleportForward(ServerPlayer p,double distance){
        Vec3 look=p.getLookAngle();
        for(double d=distance;d>=1;d-=1){Vec3 dest=p.position().add(look.scale(d));if(p.serverLevel().noCollision(p,p.getBoundingBox().move(dest.subtract(p.position())))){p.teleportTo(dest.x,dest.y,dest.z);p.fallDistance=0;return;}}
        p.sendSystemMessage(Component.literal("§7No safe landing spot in that direction."));
    }
    private static java.util.List<Mob> nearbyMobs(ServerPlayer p,double radius){
        return p.serverLevel().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(radius),e->e.isAlive()&&e!=p);
    }
    private static void forceTarget(ServerPlayer p,double strength,double lift,boolean pull){
        Vec3 look=p.getLookAngle();
        Entity target=p.serverLevel().getEntities(p,p.getBoundingBox().expandTowards(look.scale(14)).inflate(2.0),e->e instanceof Mob&&p.hasLineOfSight(e)).stream()
          .filter(e->{Vec3 toward=e.position().add(0,e.getBbHeight()*0.5,0).subtract(p.getEyePosition()).normalize();return look.dot(toward)>0.94;})
          .min(java.util.Comparator.comparingDouble(e->e.distanceToSqr(p))).orElse(null);
        if(target==null){p.sendSystemMessage(Component.literal("§7Aim at a mob within 14 blocks."));return;}
        Vec3 force=pull?p.position().subtract(target.position()).normalize().scale(strength).add(0,lift,0):look.scale(strength).add(0,lift,0);
        target.setDeltaMovement(force);target.hurtMarked=true;target.hasImpulse=true;
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

package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityWalkingMob;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSwordStone;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class EntityWitherSkeleton extends EntityWalkingMob {

    public static final int NETWORK_ID = 48;

    public EntityWitherSkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.fireProof = true;
        this.setMaxHealth(20);
        this.setDamage(new int[] { 0, 5, 8, 12 });
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 10 && player.distanceSquared(this) <= 1) {
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getDamage()));

            Effect wither = Effect.getEffect(Effect.WITHER);
            wither.setAmplifier(1);
            wither.setDuration(200);
            player.addEffect(wither);
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = new ItemSwordStone();
        pk.hotbarSlot = 0;
        player.dataPacket(pk);
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent && !this.isBaby()) {
            int coal = EntityUtils.rand(0, 2);
            int bones = EntityUtils.rand(0, 3);

            for (int i = 0; i < coal; i++) {
                drops.add(Item.get(Item.COAL, 0, 1));
            }

            for (int i = 0; i < bones; i++) {
                drops.add(Item.get(Item.BONE, 0, 1));
            }
        }

        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    @Override
    public String getName() {
        return "Wither Skeleton";
    }
}

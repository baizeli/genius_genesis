package miku.united_as_one.genesis.item.weapon;

import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import java.util.Map;
import miku.united_as_one.genesis.network.GenesisNetwork;
import miku.united_as_one.genesis.network.packet.SpawnSlashPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

@SuppressWarnings("removal")
public class MithrilSword extends MagicSwordItem {
    private static final int SLASH_COLOR = 0xFF4AA6FF;

    public MithrilSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, (int) -tier.getAttackDamageBonus(), 0.0F,
                SpellDataRegistryHolder.of(new SpellDataRegistryHolder(SpellRegistry.RAY_OF_FROST_SPELL, 5)),
                Map.of(), properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            GenesisNetwork.sendToTrackingAndSelf(attacker, new SpawnSlashPacket(attacker.getId(), target.getId(), SLASH_COLOR));
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}

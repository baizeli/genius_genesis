package miku.united_as_one.genesis.data.datagen.provider;

import dev.xkmc.l2library.compat.curios.CurioEntityBuilder;
import dev.xkmc.l2library.compat.curios.CurioSlotBuilder;
import dev.xkmc.l2library.compat.curios.SlotCondition;
import dev.xkmc.l2library.serial.config.RecordDataProvider;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ModCuriosDataProvider extends RecordDataProvider {
    public ModCuriosDataProvider(DataGenerator generator) {
        super(generator, "Curios Generator");
    }

    @Override
    public void add(BiConsumer<String, Record> map) {
        map.accept(Genesis.MOD_ID + "/curios/entities/genius_genesis_entities", new CurioEntityBuilder(
                new ArrayList<>(List.of(new ResourceLocation("player"))),
                new ArrayList<>(List.of("spellbook", "ring", "necklace", "magic_guide", "crown")),
                SlotCondition.of()
        ));

        map.accept(Genesis.MOD_ID + "/curios/slots/crown", new CurioSlotBuilder(
                41,
                new ResourceLocation("curios", "slot/crown_slot").toString(),
                1,
                CurioSlotBuilder.Operation.SET,
                false,
                true,
                true,
                false,
                SlotCondition.of()
        ));

        map.accept(Genesis.MOD_ID + "/curios/slots/magic_guide", new CurioSlotBuilder(
                201,
                new ResourceLocation("curios", "slot/magic_guide_slot").toString(),
                2,
                CurioSlotBuilder.Operation.SET,
                false,
                true,
                true,
                false,
                SlotCondition.of()
        ));

        map.accept(Genesis.MOD_ID + "/curios/slots/ring", new CurioSlotBuilder(
                0,
                "",
                2,
                CurioSlotBuilder.Operation.SET
        ));
    }
}

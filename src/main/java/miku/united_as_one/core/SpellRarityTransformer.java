package miku.united_as_one.core;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public final class SpellRarityTransformer implements ITransformer<ClassNode> {
    static final String TARGET = "io.redspace.ironsspellbooks.api.spells.SpellRarity";
    private static final String OWNER = TARGET.replace('.', '/');
    private static final String DESC = "L" + OWNER + ";";

    @Override
    public ClassNode transform(ClassNode node, ITransformerVotingContext context) {
        if (node.fields.stream().anyMatch(field -> field.name.equals("INNATE"))) return node;
        node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_ENUM,
                "INNATE", DESC, null, null));
        for (MethodNode method : node.methods) {
            if (method.name.equals("$values") && method.desc.equals("()[" + DESC)) extendValues(method);
            if (method.name.equals("<clinit>")) initializeConstant(method);
        }
        return node;
    }

    private static void extendValues(MethodNode method) {
        AbstractInsnNode size = method.instructions.getFirst();
        method.instructions.set(size, new IntInsnNode(Opcodes.BIPUSH, 6));
        AbstractInsnNode returnInsn = method.instructions.getLast();
        InsnList addition = new InsnList();
        addition.add(new InsnNode(Opcodes.DUP));
        addition.add(new InsnNode(Opcodes.ICONST_5));
        addition.add(new FieldInsnNode(Opcodes.GETSTATIC, OWNER, "INNATE", DESC));
        addition.add(new InsnNode(Opcodes.AASTORE));
        method.instructions.insertBefore(returnInsn, addition);
    }

    private static void initializeConstant(MethodNode method) {
        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof MethodInsnNode call && call.owner.equals(OWNER) && call.name.equals("$values")) {
                InsnList addition = new InsnList();
                addition.add(new TypeInsnNode(Opcodes.NEW, OWNER));
                addition.add(new InsnNode(Opcodes.DUP));
                addition.add(new LdcInsnNode("INNATE"));
                addition.add(new InsnNode(Opcodes.ICONST_5));
                addition.add(new InsnNode(Opcodes.ICONST_5));
                addition.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, OWNER, "<init>", "(Ljava/lang/String;II)V", false));
                addition.add(new FieldInsnNode(Opcodes.PUTSTATIC, OWNER, "INNATE", DESC));
                method.instructions.insertBefore(call, addition);
                return;
            }
        }
    }

    @Override public TransformerVoteResult castVote(ITransformerVotingContext context) { return TransformerVoteResult.YES; }
    @Override public Set<Target> targets() { return Set.of(Target.targetClass(TARGET)); }
}

var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var TableSwitchInsnNode = Java.type('org.objectweb.asm.tree.TableSwitchInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

function log(msg) {
    print("[XrayOF/Transformer] " + msg);
}

function getMethod(classNode, name) {
    for (var i in classNode.methods) {
        var method = classNode.methods[i];
        if (method.name === name)
            return method;
    }
    throw name + " can't be find";
}

function initializeCoreMod() {
    log("Init Xray coremod");
    return {
        'xrayOF': {
            'target': {
                'type': 'CLASS',
                'name': 'net.optifine.util.BlockUtils'
            },
            'transformer': function (classNode) {
                log("Building Xray redirects...");

                var method = getMethod(classNode, "shouldSideBeRendered");

                var inst = ASM.getMethodNode().instructions;

                inst.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Lnet/minecraft/block/BlockState
                inst.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Lnet/minecraft/world/IBlockReader
                inst.add(new VarInsnNode(Opcodes.ALOAD, 2)); // Lnet/minecraft/util/math/BlockPos
                inst.add(new VarInsnNode(Opcodes.ALOAD, 3)); // Lnet/minecraft/util/Direction
                inst.add(new VarInsnNode(Opcodes.ALOAD, 4)); // Lnet/optifine/render/RenderEnv
                inst.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/atesab/xray/XrayMain", "shouldSideBeRendered", "("
                    + "Lnet/minecraft/block/BlockState;"
                    + "Lnet/minecraft/world/IBlockReader;"
                    + "Lnet/minecraft/util/math/BlockPos;"
                    + "Lnet/minecraft/util/Direction;"
                    + "Ljava/lang/Object;"
                    + ")I",
                    false));

                var case0 = new LabelNode();
                var case1 = new LabelNode();
                var casedft = new LabelNode();

                inst.add(new TableSwitchInsnNode(0, 2, casedft, case0, case1, casedft)); // switch
                // (returnvalue)
                inst.add(case0); // case 0
                inst.add(new InsnNode(Opcodes.ICONST_1));
                inst.add(new InsnNode(Opcodes.IRETURN)); // return true

                inst.add(case1); // case 1
                inst.add(new InsnNode(Opcodes.ICONST_0));
                inst.add(new InsnNode(Opcodes.IRETURN)); // return false

                inst.add(casedft); // default

                log("Adding Xray redirects...");

                method.instructions.insert(inst);

                log("Xray redirects added.");

                return classNode;
            }
        }
    }
}

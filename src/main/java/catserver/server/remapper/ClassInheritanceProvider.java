package catserver.server.remapper;

import net.md_5.specialsource.provider.InheritanceProvider;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import static catserver.server.remapper.RemapUtils.reverseMap;

public class ClassInheritanceProvider implements InheritanceProvider {
    @Override
    public Collection<String> getParents(String className) {
        className = ReflectionTransformer.remapper.map(className);
        FMLDeobfuscatingRemapper mapper = FMLDeobfuscatingRemapper.INSTANCE;
        String obfName = mapper.unmap(className);
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream((obfName + ".class"))) {
            if (is == null) {
                return null;
            }

            ClassReader reader = new ClassReader(is);
            ParentVisitor visitor = new ParentVisitor();

            reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            Collection<String> parents = new HashSet<>();

            if (visitor.getSuperName() != null && !visitor.getSuperName().equals("java/lang/Object")) {
                parents.add(reverseMap(mapper.map(visitor.getSuperName())));
            }

            for (String inter : visitor.getInterfaces()) {
                if (inter != null) {
                    parents.add(reverseMap(mapper.map(inter)));
                }
            }

            return parents;

        } catch (Exception e) {
            System.err.println("Failed to read class for parents: " + className);
        }

        return null;
    }

    private static class ParentVisitor extends ClassVisitor {
        private String superName;
        private String[] interfaces;

        public ParentVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superName = superName;
            this.interfaces = interfaces;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        public String getSuperName() {
            return superName;
        }

        public String[] getInterfaces() {
            return interfaces == null ? new String[0] : interfaces;
        }
    }

}
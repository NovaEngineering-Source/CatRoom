package catserver.dev;

import catserver.server.CatServer;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CatServerGradleStart {
    public static void main(String[] args) throws Throwable
    {
        System.out.println("================================================================");
        System.out.println("[Warning] Don't use the class to start the server!");
        System.out.println("[Warning] It's only for development!");
        System.out.println("[Warning] If you want to start the server");
        System.out.println("[Warning] Please use catserver.server.CatServerLaunch class");
        System.out.println("================================================================");

        if (new File("build.gradle").exists()) {
            System.out.println("[ERROR] Detect the server is running in project directory!");
            return;
        }

        (new CatServerGradleStart()).launch(args);
    }

    protected static Logger LOGGER        = LogManager.getLogger("GradleStart");

    Map<String, String>     argMap        = Maps.newHashMap();
    List<String>            extras        = Lists.newArrayList();

    static final File       SRG_SRG_MCP   = new File(CatServerGradleStart.class.getClassLoader().getResource("mappings/" + CatServer.getNativeVersion() + "/srg2mcp.srg").getFile());

    protected void launch(String[] args) throws Throwable
    {
        System.setProperty("net.minecraftforge.gradle.GradleStart.srg.srg-mcp", SRG_SRG_MCP.getCanonicalPath());
        parseArgs(args);
        System.setProperty("fml.ignoreInvalidMinecraftCertificates", "true");
        args = getArgs();
        argMap = null;
        extras = null;

        System.gc();
        try {
            Class.forName("net.minecraft.launchwrapper.Launch").getDeclaredMethod("main", String[].class).invoke(null, new Object[] { args });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private String[] getArgs()
    {
        ArrayList<String> list = new ArrayList<String>(22);

        for (Map.Entry<String, String> e : argMap.entrySet())
        {
            String val = e.getValue();
            if (!Strings.isNullOrEmpty(val))
            {
                list.add("--" + e.getKey());
                list.add(val);
            }
        }

        if (!Strings.isNullOrEmpty("net.minecraftforge.fml.common.launcher.FMLServerTweaker"))
        {
            list.add("--tweakClass");
            list.add("net.minecraftforge.fml.common.launcher.FMLServerTweaker");
        }

        if (extras != null)
        {
            list.addAll(extras);
        }

        String[] out = list.toArray(new String[list.size()]);

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int x = 0; x < out.length; x++)
        {
            b.append(out[x]);
            if ("--accessToken".equalsIgnoreCase(out[x]))
            {
                b.append("{REDACTED}");
                x++;
            }

            if (x < out.length - 1)
            {
                b.append(", ");
            }
        }
        b.append(']');
        CatServerGradleStart.LOGGER.info("Running with arguments: " + b.toString());

        return out;
    }

    private void parseArgs(String[] args)
    {
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        for (String key : argMap.keySet())
        {
            parser.accepts(key).withRequiredArg().ofType(String.class);
        }

        final NonOptionArgumentSpec<String> nonOption = parser.nonOptions();

        final OptionSet options = parser.parse(args);
        for (String key : argMap.keySet())
        {
            if (options.hasArgument(key))
            {
                String value = (String) options.valueOf(key);
                argMap.put(key, value);
                if (!"password".equalsIgnoreCase(key) && !"accessToken".equalsIgnoreCase(key))
                    LOGGER.info(key + ": " + value);
            }
        }

        extras = Lists.newArrayList(nonOption.values(options));
        LOGGER.info("Extra: " + extras);
    }
}

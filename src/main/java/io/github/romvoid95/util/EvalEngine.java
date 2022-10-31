package io.github.romvoid95.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import com.google.common.util.concurrent.MoreExecutors;

public enum EvalEngine
{

    GROOVY("Groovy", "groovy")
    {

        @Override
        public Triple<Object, String, String> eval(final Map<String, Object> fields, final Collection<String> classImports, final Collection<String> packageImports, final int timeout, final String script)
        {
            StringBuilder importString = new StringBuilder();
            for (final String s : classImports)
                importString.append("import ").append(s).append(";");
            for (final String s : packageImports)
                importString.append("import ").append(s).append(".*;");
            return this.eval(fields, timeout, importString + script, new GroovyScriptEngineImpl());
        }

    },
    JAVASCRIPT("JavaScript", "js", "javascript")
    {

        private final ScriptEngineManager engineManager = new ScriptEngineManager();

        @Override
        public Triple<Object, String, String> eval(final Map<String, Object> fields, final Collection<String> classImports, final Collection<String> packageImports, final int timeout, String script)
        {
            StringBuilder importString = new StringBuilder();
            for (final String s : packageImports)
                importString.append(s).append(", ");
            importString = new StringBuilder(StringUtils.replaceLast(importString.toString(), ", ", ""));

            script = " (function() { with (new JavaImporter(" + importString + ")) {" + script + "} })();";
            return this.eval(fields, timeout, script, this.engineManager.getEngineByName("nashorn"));
        }

    };

    //@noformat
    public static final Collection<String> DEFAULT_IMPORTS = Arrays.asList(
        "net.dv8tion.jda.api.entities.impl", 
        "net.dv8tion.jda.api.managers", 
        "net.dv8tion.jda.api.entities", 
        "net.dv8tion.jda.api", 
        "java.lang", 
        "java.io", 
        "java.math", 
        "java.util", 
        "java.util.concurrent", 
        "java.time");
    //@format
    private final static ScheduledExecutorService service         = Factory.newScheduledThreadPool(1, "eval-thread", false);

    private final List<String>                    codes;

    private final String                          name;

    EvalEngine(final String name, final String... codes)
    {
        this.name = name;
        this.codes = new ArrayList<>();
        for (final String code : codes)
            this.codes.add(code.toLowerCase());
    }

    public static EvalEngine getEngineByCode(String code)
    {
        code = code.toLowerCase();
        for (final EvalEngine engine : EvalEngine.values())
            if (engine.codes.contains(code))
                return engine;
        return null;
    }

    public static void shutdown()
    {
        MoreExecutors.shutdownAndAwaitTermination(EvalEngine.service, 10, TimeUnit.SECONDS);
    }

    public abstract Triple<Object, String, String> eval(Map<String, Object> fields, final Collection<String> classImports, final Collection<String> packageImports, int timeout, String script);

    public List<String> getCodes()
    {
        return this.codes;
    }

    public String getName()
    {
        return this.name;
    }

    protected Triple<Object, String, String> eval(final Map<String, Object> fields, final int timeout, final String script, final ScriptEngine engine)
    {

        for (final Entry<String, Object> shortcut : fields.entrySet())
            engine.put(shortcut.getKey(), shortcut.getValue());

        final StringWriter outString = new StringWriter();
        final PrintWriter outWriter = new PrintWriter(outString);
        engine.getContext().setWriter(outWriter);

        final StringWriter errorString = new StringWriter();
        final PrintWriter errorWriter = new PrintWriter(errorString);
        engine.getContext().setErrorWriter(errorWriter);

        final ScheduledFuture<Object> future = EvalEngine.service.schedule(() -> engine.eval(script), 0, TimeUnit.MILLISECONDS);

        Object result = null;

        try
        {
            result = future.get(timeout, TimeUnit.SECONDS);
        } catch (final ExecutionException e)
        {
            errorWriter.println(e.getCause().toString());
        } catch (TimeoutException | InterruptedException e)
        {
            future.cancel(true);
            errorWriter.println(e.toString());
        }

        return new ImmutableTriple<>(result, outString.toString(), errorString.toString());
    }

    public static class Import
    {

        private final String name;

        private final Type   type;

        public Import(final Import.Type type, final String name)
        {
            this.type = type;
            this.name = name;
        }

        public final String getName()
        {
            return this.name;
        }

        public final Type getType()
        {
            return this.type;
        }

        public enum Type
        {
            CLASS,
            PACKAGE
        }

    }
}

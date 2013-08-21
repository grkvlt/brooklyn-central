package brooklyn.util.task.ssh;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.BrooklynTasks;
import brooklyn.management.Task;
import brooklyn.management.TaskWrapper;
import brooklyn.util.config.ConfigBag;
import brooklyn.util.internal.ssh.SshTool;
import brooklyn.util.stream.Streams;
import brooklyn.util.task.TaskBuilder;
import brooklyn.util.task.Tasks;
import brooklyn.util.text.Strings;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/** Wraps a fully constructed SSH task, and allows callers to inspect status. 
 * Note that methods in here such as {@link #getStdout()} will return partially completed streams while the task is ongoing
 * (and exit code will be null). You can {@link #block()} or {@link #get()} as conveniences on the underlying {@link #getTask()}. */ 
public class SshExecTaskWrapper<RET> extends SshExecTaskStub implements TaskWrapper<RET> {

    private static final Logger log = LoggerFactory.getLogger(SshExecTaskWrapper.class);
    
    private final Task<RET> task;

    // execution details
    protected ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    protected ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    protected Integer exitCode = null;
    
    @SuppressWarnings("unchecked")
    SshExecTaskWrapper(AbstractSshExecTaskFactory<?,RET> constructor) {
        super(constructor);
        TaskBuilder<Object> tb = constructor.constructCustomizedTaskBuilder();
        if (stdout!=null) tb.tag(BrooklynTasks.tagForStream(BrooklynTasks.STREAM_STDOUT, stdout));
        if (stderr!=null) tb.tag(BrooklynTasks.tagForStream(BrooklynTasks.STREAM_STDERR, stderr));
        task = (Task<RET>) tb.body(new SshJob()).build();
    }
    
    @Override
    public Task<RET> asTask() {
        return getTask();
    }
    
    @Override
    public Task<RET> getTask() {
        return task;
    }
    
    public Integer getExitCode() {
        return exitCode;
    }
    
    public byte[] getStdoutBytes() {
        if (stdout==null) return null;
        return stdout.toByteArray();
    }
    
    public byte[] getStderrBytes() {
        if (stderr==null) return null;
        return stderr.toByteArray();
    }
    
    public String getStdout() {
        if (stdout==null) return null;
        return stdout.toString();
    }
    
    public String getStderr() {
        if (stderr==null) return null;
        return stderr.toString();
    }
    
    private class SshJob implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            Preconditions.checkNotNull(getMachine(), "machine");
            
            ConfigBag config = ConfigBag.newInstanceCopying(SshExecTaskWrapper.this.config);
            if (stdout!=null) config.put(SshTool.PROP_OUT_STREAM, stdout);
            if (stderr!=null) config.put(SshTool.PROP_ERR_STREAM, stderr);
            
            if (!config.containsKey(SshTool.PROP_NO_EXTRA_OUTPUT))
                // by default no extra output (so things like cat, etc work as expected)
                config.put(SshTool.PROP_NO_EXTRA_OUTPUT, true);

            if (runAsRoot)
                config.put(SshTool.PROP_RUN_AS_ROOT, true);

            if (runAsScript==Boolean.FALSE)
                exitCode = getMachine().execCommands(config.getAllConfigRaw(), getSummary(), commands, shellEnvironment);
            else // null or TRUE
                exitCode = getMachine().execScript(config.getAllConfigRaw(), getSummary(), commands, shellEnvironment);
            
            if (exitCode!=0 && requireExitCodeZero!=Boolean.FALSE) {
                if (requireExitCodeZero==Boolean.TRUE) {
                    logWithDetailsAndThrow("SSH task ended with exit code "+exitCode+" when 0 was required, in "+Tasks.current()+": "+getSummary(), null);
                } else {
                    // warn, but allow, on non-zero not explicitly allowed
                    log.warn("SSH task ended with exit code "+exitCode+" when non-zero was not explicitly allowed (error may be thrown in future), in "
                            +Tasks.current()+": "+getSummary());
                }
            }
            for (Function<SshExecTaskWrapper<?>, Void> listener: completionListeners) {
                try {
                    listener.apply(SshExecTaskWrapper.this);
                } catch (Exception e) {
                    logWithDetailsAndThrow("Error in SSH task "+getSummary()+": "+e, e);                    
                }
            }

            switch (returnType) {
            case CUSTOM: return returnResultTransformation.apply(SshExecTaskWrapper.this);
            case STDOUT_STRING: return stdout.toString();
            case STDOUT_BYTES: return stdout.toByteArray();
            case STDERR_STRING: return stderr.toString();
            case STDERR_BYTES: return stderr.toByteArray();
            case EXIT_CODE: return exitCode;
            }

            throw new IllegalStateException("Unknown return type for ssh job "+getSummary()+": "+returnType);
        }

        protected void logWithDetailsAndThrow(String message, Throwable optionalCause) {
            message = (extraErrorMessage!=null ? extraErrorMessage+": " : "") + message;
            log.warn(message+" (throwing)");
            logProblemDetails("STDERR", stderr, 1024);
            logProblemDetails("STDOUT", stdout, 1024);
            logProblemDetails("STDIN", Streams.byteArrayOfString(Strings.join(commands,"\n")), 4096);
            if (optionalCause!=null) throw new IllegalStateException(message, optionalCause);
            throw new IllegalStateException(message);
        }

        protected void logProblemDetails(String streamName, ByteArrayOutputStream stream, int max) {
            if (stream!=null && stream.size()>0) {
                String stderrOut = stream.toString();
                if (stderrOut.length()>max)
                    stderrOut = "... "+stderrOut.substring(stderrOut.length()-max);
                log.info(streamName+" for problem in "+Tasks.current()+":\n"+stderrOut);
            }
        }
    }
    
    @Override
    public String toString() {
        return super.toString()+"["+task+"]";
    }

    /** blocks and gets the result, throwing if there was an exception */
    public RET get() {
        return getTask().getUnchecked();
    }
    
    /** blocks until the task completes; does not throw */
    public SshExecTaskWrapper<RET> block() {
        getTask().blockUntilEnded();
        return this;
    }
 
    /** true iff the ssh job has completed (with or without failure) */
    public boolean isDone() {
        return getTask().isDone();
    }
    
}
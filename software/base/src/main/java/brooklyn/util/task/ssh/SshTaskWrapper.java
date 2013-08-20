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
import brooklyn.util.task.TaskBuilder;
import brooklyn.util.task.Tasks;

import com.google.common.base.Preconditions;

/** Wraps a fully constructed SSH task, and allows callers to inspect status. 
 * Note that methods in here such as {@link #getStdout()} will return partially completed streams while the task is ongoing
 * (and exit code will be null). You can {@link #block()} or {@link #get()} as conveniences on the underlying {@link #getTask()}. */ 
public class SshTaskWrapper<RET> extends SshTaskStub implements TaskWrapper<RET> {

    private static final Logger log = LoggerFactory.getLogger(SshTaskWrapper.class);
    
    private Task<RET> task;

    // execution details
    protected ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    protected ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    protected Integer exitCode = null;
    
    @SuppressWarnings("unchecked")
    // package private as only AbstractSshTaskFactory should invoke
    SshTaskWrapper(AbstractSshTaskFactory<?,RET> constructor) {
        super(constructor);
        TaskBuilder<Object> tb = constructor.constructCustomizedTaskBuilder();
        if (stdout!=null) tb.tag(BrooklynTasks.tagForStream(BrooklynTasks.STREAM_STDOUT, stdout));
        if (stderr!=null) tb.tag(BrooklynTasks.tagForStream(BrooklynTasks.STREAM_STDERR, stderr));
        task = (Task<RET>) tb.body(new SshJob()).build();
    }
    
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
            
            ConfigBag config = ConfigBag.newInstanceCopying(SshTaskWrapper.this.config);
            if (stdout!=null) config.put(SshTool.PROP_OUT_STREAM, stdout);
            if (stderr!=null) config.put(SshTool.PROP_ERR_STREAM, stderr);
            
            if (runAsRoot)
                config.put(SshTool.PROP_RUN_AS_ROOT, true);

            if (runAsScript)
                exitCode = getMachine().execScript(config.getAllConfigRaw(), getSummary(), commands, shellEnvironment);
            else
                exitCode = getMachine().execCommands(config.getAllConfigRaw(), getSummary(), commands, shellEnvironment);
            
            if (exitCode!=0 && requireExitCodeZero!=Boolean.FALSE) {
                if (requireExitCodeZero==Boolean.TRUE) {
                    throw new IllegalStateException(
                        (extraErrorMessage!=null ? extraErrorMessage+": " : "")+
                        "SSH task ended with exit code "+exitCode+" when 0 was required, in "+Tasks.current()+": "+getSummary());
                } else {
                    // warn, but allow, on non-zero not explicitly allowed
                    log.warn("SSH task ended with exit code "+exitCode+" when non-zero was not explicitly allowed (error may be thrown in future), in "
                            +Tasks.current()+": "+getSummary());
                }
            }

            switch (returnType) {
            case CUSTOM: return returnResultTransformation.apply(SshTaskWrapper.this);
            case STDOUT_STRING: return stdout.toString();
            case STDOUT_BYTES: return stdout.toByteArray();
            case STDERR_STRING: return stderr.toString();
            case STDERR_BYTES: return stderr.toByteArray();
            case EXIT_CODE: return exitCode;
            }

            throw new IllegalStateException("Unknown return type for ssh job "+getSummary()+": "+returnType);
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
    public SshTaskWrapper<RET> block() {
        getTask().blockUntilEnded();
        return this;
    }
 
    /** true iff the ssh job has completed (with or without failure) */
    public boolean isDone() {
        return getTask().isDone();
    }
    
}
package brooklyn.util.internal.ssh;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.BasicConfigKey.StringConfigKey;
import brooklyn.util.stream.KnownSizeInputStream;

/**
 * Defines the methods available on the various different implementations of SSH,
 * and configuration options which are also generally available.
 * <p>
 * The config keys in this class can be supplied (or their string equivalents, where the flags/props take Map<String,?>)
 * to influence configuration, either for the tool/session itself or for individual commands.
 * <p>
 * To specify some of these properties on a global basis, use the variants of the keys here
 * contained in {@link ConfigKeys}
 * (which are generally {@value #BROOKLYN_CONFIG_KEY_PREFIX} prefixed to the names of keys here).
 */
public interface SshTool {

//    /** Intermediate config keys for Brooklyn are defined where they are used, e.g. in {@link SshMachineLocation} 
//     * and have this prefix pre-prended to the config keys in this class. */
//    String LOCATION_CONFIG_KEY_PREFIX = "ssh.config.";
    
    /** Public-facing global config keys for Brooklyn are defined in ConfigKeys, 
     * and have this prefix pre-prended to the config keys in this class. */
    String BROOKLYN_CONFIG_KEY_PREFIX = "brooklyn.ssh.config.";
    
    ConfigKey<String> PROP_TOOL_CLASS = ConfigKeys.newConfigKey("tool.class", "SshTool implementation to use", null);
    
    ConfigKey<String> PROP_HOST = ConfigKeys.newConfigKey("host", "Host to connect to (required)", null);
    ConfigKey<Integer> PROP_PORT = ConfigKeys.newConfigKey("port", "Port on host to connect to", 22);
    ConfigKey<String> PROP_USER = ConfigKeys.newConfigKey("user", "User to connect as", System.getProperty("user.name"));
    ConfigKey<String> PROP_PASSWORD = ConfigKeys.newConfigKey("password", "Password to use to connect", null);
    
    ConfigKey<String> PROP_PRIVATE_KEY_FILE = ConfigKeys.newConfigKey("privateKeyFile", "the path of an ssh private key file; leave blank to use defaults (i.e. ~/.ssh/id_rsa and id_dsa)", null);
    ConfigKey<String> PROP_PRIVATE_KEY_DATA = ConfigKeys.newConfigKey("privateKeyData", "the private ssh key (e.g. contents of an id_rsa.pub or id_dsa.pub file)", null);
    ConfigKey<String> PROP_PRIVATE_KEY_PASSPHRASE = ConfigKeys.newConfigKey("privateKeyPassphrase", "the passphrase for the ssh private key", null);
    ConfigKey<Boolean> PROP_STRICT_HOST_KEY_CHECKING = ConfigKeys.newConfigKey("strictHostKeyChecking", "whether to check the remote host's identification; defaults to false", false);
    ConfigKey<Boolean> PROP_ALLOCATE_PTY = ConfigKeys.newConfigKey("allocatePTY", "whether to allocate PTY (vt100); if true then stderr is sent to stdout, but sometimes required for sudo'ing due to requiretty", false);

    ConfigKey<Integer> PROP_CONNECT_TIMEOUT = ConfigKeys.newConfigKey("connectTimeout", "The timeout when establishing an SSH connection; if 0 then uses default", 0);
    ConfigKey<Integer> PROP_SESSION_TIMEOUT = ConfigKeys.newConfigKey("sessionTimeout", "The timeout for an ssh session; if 0 then uses default", 0);
    ConfigKey<Integer> PROP_SSH_TRIES = ConfigKeys.newConfigKey("sshTries", "Max number of attempts to connect when doing ssh operations", 4);
    ConfigKey<Integer> PROP_SSH_TRIES_TIMEOUT = ConfigKeys.newConfigKey("sshTriesTimeout", "Timeout when attempting to connect for ssh operations; so if too slow trying sshTries times, will abort anyway", 2*60*1000);
    ConfigKey<Long> PROP_SSH_RETRY_DELAY = ConfigKeys.newConfigKey("sshRetryDelay", "Time (in milliseconds) before first ssh-retry, after which it will do exponential backoff", 50L);

    ConfigKey<File> PROP_LOCAL_TEMP_DIR = ConfigKeys.newConfigKey("localTempDir", "The directory on the local machine (i.e. running brooklyn) for writing temp files", 
            new File(System.getProperty("java.io.tmpdir"), "tmpssh"));
    
    // NB -- items above apply for _session_ (a tool), below apply for a _call_
    // TODO would be nice to track which arguments are used, so we can indicate whether extras are supplied

    ConfigKey<OutputStream> PROP_OUT_STREAM = ConfigKeys.newConfigKey("out", "Stream to which to capture stdout");
    ConfigKey<OutputStream> PROP_ERR_STREAM = ConfigKeys.newConfigKey("err", "Stream to which to capture stderr");
    
    ConfigKey<String> PROP_SEPARATOR = ConfigKeys.newConfigKey("separator", "string to insert between caller-supplied commands being executed as commands", " ; ");
    
    ConfigKey<String> PROP_SCRIPT_DIR = ConfigKeys.newConfigKey("scriptDir", "directory where scripts should be copied", "/tmp");
    ConfigKey<String> PROP_SCRIPT_HEADER = ConfigKeys.newConfigKey("scriptHeader", "lines to insert at the start of scripts generated for caller-supplied commands for script execution", "#!/bin/bash -e\n");
    ConfigKey<String> PROP_DIRECT_HEADER = ConfigKeys.newConfigKey("directHeader", "commands to run remotely before any caller-supplied commands for direct execution", "exec bash -e");

    ConfigKey<String> PROP_PERMISSIONS = ConfigKeys.newConfigKey("permissions", "Default permissions for files copied/created on remote machine; must be four-digit octal string, default '0644'", "0644");
    ConfigKey<Long> PROP_LAST_MODIFICATION_DATE = ConfigKeys.newConfigKey("lastModificationDate", "Last-modification-date to be set on files copied/created (should be UTC/1000, ie seconds since 1970; defaults to current)", 0L);
    ConfigKey<Long> PROP_LAST_ACCESS_DATE = ConfigKeys.newConfigKey("lastAccessDate", "Last-access-date to be set on files copied/created (should be UTC/1000, ie seconds since 1970; defaults to lastModificationDate)", 0L);

    // TODO Could define the following in SshMachineLocation, or some such?
    // ConfigKey<String> PROP_LOG_PREFIX = ConfigKeys.newConfigKey("logPrefix", "???", ???);
    // ConfigKey<Boolean> PROP_NO_STDOUT_LOGGING = ConfigKeys.newConfigKey("noStdoutLogging", "???", ???);
    // ConfigKey<Boolean> PROP_NO_STDOUT_LOGGING = ConfigKeys.newConfigKey("noStdoutLogging", "???", ???);

    /**
     * @deprecated since 0.4; use PROP_PRIVATE_KEY_FILE; if this contains more than one element then it will fail.
     */
    ConfigKey<List<String>> PROP_KEY_FILES = ConfigKeys.newConfigKey("keyFiles", "DEPRECATED: see privateKeyFile", Collections.<String>emptyList());

    /**
     * @deprecated since 0.4; use PROP_PRIVATE_KEY_DATA instead
     */
    @Deprecated
    ConfigKey<String> PROP_PRIVATE_KEY = ConfigKeys.newConfigKey("privateKey", "DEPRECATED: see privateKeyData", null);

    /**
     * @throws SshException
     */
    void connect();

    /**
     * @param maxAttempts
     * @throws SshException
     */
    void connect(int maxAttempts);

    void disconnect();

    boolean isConnected();

    /**
     * Executes the set of commands in a shell script. Blocks until completion.
     * <p>
     * Optional properties are:
     * <ul>
     *   <li>{@code out} {@link java.io.OutputStream} - see {@link #PROP_OUT_STREAM}
     *   <li>{@code err} {@link java.io.OutputStream} - see {@link #PROP_ERR_STREAM}
     * </ul>
     * 
     * @return exit status of script
     * @throws SshException If failed to connect
     * @see #execCommands(Map, List, Map)
     */
    int execScript(Map<String,?> props, List<String> commands, Map<String,?> env);

    /** @see #execScript(Map, List, Map) */
    int execScript(Map<String,?> props, List<String> commands);

    /** @deprecated since 0.4; use {@link #execScript(Map, List)} */
    @Deprecated
    int execShell(Map<String,?> props, List<String> commands);

    /** @deprecated since 0.4; use {@link #execScript(Map, List, Map)} */
    @Deprecated
    int execShell(Map<String,?> props, List<String> commands, Map<String,?> env);

    /**
     * Executes the set of commands using ssh exec.
     * <p>
     * This is generally more efficient than shell, but is not suitable if you need 
     * env values which are only set on a fully-fledged shell.
     * <p>
     * Optional properties are:
     * <ul>
     *   <li>{@code out} {@link java.io.OutputStream} - see {@link #PROP_OUT_STREAM}
     *   <li>{@code err} {@link java.io.OutputStream} - see {@link #PROP_ERR_STREAM}
     *   <li>{@code separator} defaulting to {@literal ;} - see {@link #PROP_SEPARATOR}
     * </ul>
     * 
     * @return exit status of commands
     * @throws SshException If failed to connect
     * @see #execScript(Map, List, Map)
     */
    int execCommands(Map<String,?> properties, List<String> commands, Map<String,?> env);

    /** @see #execCommands(Map, List, Map) */
    int execCommands(Map<String,?> properties, List<String> commands);

    /**
     * Copies the file to the server at the given path.
     * If path is null, empty, {literal .}, {@literal ..}, or ends with {@literal /} then file name is used.
     * <p>
     * The file will not preserve the permission of last <em>access</em> date.
     * <p>
     * Optional properties are:
     * <ul>
     *   <li>{@code permissions} in octal, for example {@literal 0644} - see {@link #PROP_PERMISSIONS}
     *   <li>{@code lastModificationDate} - see {@link #PROP_LAST_MODIFICATION_DATE}; not supported by all SshTool implementations
     *   <li>{@code lastAccessDate} - see {@link #PROP_LAST_ACCESS_DATE}; not supported by all SshTool implementations
     * </ul>
     * 
     * @return exit code (not supported by all SshTool implementations, sometimes just returning 0)
     */
    int copyToServer(Map<String,?> props, File localFile, String pathAndFileOnRemoteServer);

    /**
     * Closes the given input stream before returning.
     * Consider using {@link KnownSizeInputStream} for efficiency when the size of the stream is known.
     * 
     * @see #copyToServer(Map, File, String)
     */
    int copyToServer(Map<String,?> props, InputStream contents, String pathAndFileOnRemoteServer);

    /** @see #copyToServer(Map, File, String) */
    int copyToServer(Map<String,?> props, byte[] contents, String pathAndFileOnRemoteServer);

    /**
     * Copies the file to the server at the given path.
     * If path is null, empty, {literal .}, {@literal ..}, or ends with {@literal /} then file name is used.
     * <p>
     * Optional properties are:
     * <ul>
     *   <li>{@code permissions} in octal, for example {@literal 0644} - see {@link #PROP_PERMISSIONS}
     * </ul>
     *
     * @return exit code (not supported by all SshTool implementations, sometimes just returning 0)
     */
    public int copyFromServer(Map<String,?> props, String pathAndFileOnRemoteServer, File local);

    /**
     * @deprecated since 0.5.0; use {@link #copyToServer(Map, InputStream, String)}
     */
    @Deprecated
    public int transferFileTo(Map<String,?> props, InputStream input, String pathAndFileOnRemoteServer);

    /**
     * @deprecated since 0.5.0; use {@link #copyFromServer(Map, InputStream, String)}
     */
    @Deprecated
    public int transferFileFrom(Map<String,?> props, String pathAndFileOnRemoteServer, String pathAndFileOnLocalServer);

    /**
     * @deprecated since 0.5.0; use {@link #copyToServer(Map, InputStream, String)}
     */
    @Deprecated
    public int createFile(Map<String,?> props, String pathAndFileOnRemoteServer, InputStream input, long size);

    /**
     * @deprecated since 0.5.0; use {@link #copyToServer(Map, byte[], String)}
     */
    @Deprecated
    public int createFile(Map<String,?> props, String pathAndFileOnRemoteServer, String contents);

    /**
     * @deprecated since 0.5.0; use {@link #copyToServer(Map, byte[], String)}
     */
    @Deprecated
    public int createFile(Map<String,?> props, String pathAndFileOnRemoteServer, byte[] contents);
}

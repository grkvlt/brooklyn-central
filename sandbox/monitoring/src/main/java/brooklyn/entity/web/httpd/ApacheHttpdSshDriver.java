package brooklyn.entity.web.httpd;

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import brooklyn.entity.basic.AbstractSoftwareProcessSshDriver;
import brooklyn.entity.basic.lifecycle.CommonCommands;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.MutableMap;
import brooklyn.util.NetworkUtils;

public class ApacheHttpdSshDriver extends AbstractSoftwareProcessSshDriver implements ApacheHttpdDriver {

    public ApacheHttpdSshDriver(ApacheHttpdImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    public String getLogFileLocation() {
        return String.format("%s/httpd.log", getRunDir());
    }

    public String getDeploySubdir() {
        return "httpd";
    }

    public Integer getHttpPort() {
        return entity.getAttribute(ApacheHttpd.HTTP_PORT);
    }

    @Override
    public void install() {
        String url = "http://download.nextag.com/apache/tomcat/tomcat-7/v" + getVersion() + "/bin/apache-tomcat-" + getVersion() + ".tar.gz";
        String saveAs = "apache-httpd-" + getVersion() + ".tar.gz";

        List<String> commands = new LinkedList<String>();
        commands.addAll(CommonCommands.downloadUrlAs(url, getEntityVersionLabel("/"), saveAs));
        commands.add(CommonCommands.installExecutable("tar"));
        commands.add(format("tar xvzf %s", saveAs));

        newScript(INSTALLING).
                failOnNonZeroResultCode().body.append(commands).execute();
    }

    @Override
    public void customize() {
        newScript(CUSTOMIZING).body.append("").execute();
    }

    @Override
    public void launch() {
        Map ports = MutableMap.of("httpPort", getHttpPort());
        NetworkUtils.checkPortsValid(ports);

        Map flags = MutableMap.of("usePidFile", false);

        newScript(flags, LAUNCHING).body.append("").execute();
    }

    @Override
    public boolean isRunning() {
        Map flags = MutableMap.of("usePidFile", "pid.txt");
        return newScript(flags, CHECK_RUNNING).execute() == 0;
    }

    @Override
    public void stop() {
        Map flags = MutableMap.of("usePidFile", "pid.txt");
        newScript(flags, STOPPING).execute();
    }

    @Override
    public void kill() {
        Map flags = MutableMap.of("usePidFile", "pid.txt");
        newScript(flags, KILLING).execute();
    }
}

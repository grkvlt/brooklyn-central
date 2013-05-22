/*
 * Copyright 2012-2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.entity.web.httpd;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import brooklyn.entity.basic.AbstractSoftwareProcessSshDriver;
import brooklyn.entity.drivers.downloads.DownloadResolver;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.NetworkUtils;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.ssh.CommonCommands;

import com.google.common.collect.ImmutableList;

public class ApacheHttpdSshDriver extends AbstractSoftwareProcessSshDriver implements ApacheHttpdDriver {

    private String expandedInstallDir;

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

    private String getExpandedInstallDir() {
        if (expandedInstallDir == null) throw new IllegalStateException("expandedInstallDir is null; most likely install was not called");
        return expandedInstallDir;
    }

    @Override
    public void install() {
        DownloadResolver resolver = entity.getManagementContext().getEntityDownloadsManager().newDownloader(this);
        List<String> urls = resolver.getTargets();
        String saveAs = resolver.getFilename();
        expandedInstallDir = getInstallDir()+"/"+resolver.getUnpackedDirectoryName(format("apache-httpd-%s", getVersion()));

        List<String> commands = ImmutableList.<String>builder()
                .addAll(CommonCommands.downloadUrlAs(urls, saveAs))
                .add(CommonCommands.INSTALL_TAR)
                .add("tar xzfv " + saveAs)
                .build();

        newScript(INSTALLING).
                failOnNonZeroResultCode().body.append(commands).execute();
    }

    @Override
    public void customize() {
        newScript(CUSTOMIZING).body.append("").execute();

        // Copy tgz/zip to machine and expand
        // Add config for site at root URI

        String statusConf = "/etc/httpd/conf.d/status.conf";
        copyResource("classpath://brooklyn/entity/web/httpd/status.conf", statusConf);

    }

    @Override
    public void launch() {
        Map<String, Integer> ports = MutableMap.of("httpPort", getHttpPort());
        NetworkUtils.checkPortsValid(ports);

        Map<String, Boolean> flags = MutableMap.of("usePidFile", Boolean.FALSE);

        newScript(flags, LAUNCHING).body.append(CommonCommands.sudo("service httpd start")).execute();
    }

    @Override
    public boolean isRunning() {
        Map<String, Boolean> flags = MutableMap.of("usePidFile", Boolean.FALSE);
        return newScript(flags, CHECK_RUNNING).execute() == 0;
    }

    @Override
    public void stop() {
        Map<String, Boolean> flags = MutableMap.of("usePidFile", Boolean.FALSE);
        newScript(flags, STOPPING).body.append(CommonCommands.sudo("service httpd stop")).execute();
    }
}

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
package brooklyn.entity.nosql.couchdb;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.lifecycle.CommonCommands;
import brooklyn.entity.drivers.downloads.DownloadResolver;
import brooklyn.entity.java.JavaSoftwareProcessSshDriver;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.MutableMap;
import brooklyn.util.NetworkUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Start a {@link CouchDBNode} in a {@link Location} accessible over ssh.
 */
public class CouchDBNodeSshDriver extends JavaSoftwareProcessSshDriver implements CouchDBNodeDriver {

    private static final Logger log = LoggerFactory.getLogger(CouchDBNodeSshDriver.class);

    public CouchDBNodeSshDriver(CouchDBNodeImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    @Override
    protected String getLogFileLocation() { return String.format("%s/couchdb.log", getRunDir()); }

    @Override
    public Integer getHttpPort() { return entity.getAttribute(CouchDBNode.HTTP_PORT); }

    @Override
    public Integer getHttpsPort() { return entity.getAttribute(CouchDBNode.HTTPS_PORT); }

    @Override
    public String getClusterName() { return entity.getAttribute(CouchDBNode.CLUSTER_NAME); }

    @Override
    public String getCouchDBConfigTemplateUrl() { return entity.getAttribute(CouchDBNode.COUCHDB_CONFIG_TEMPLATE_URL); }

    @Override
    public String getCouchDBUriTemplateUrl() { return entity.getAttribute(CouchDBNode.COUCHDB_URI_TEMPLATE_URL); }

    @Override
    public String getCouchDBConfigFileName() { return entity.getAttribute(CouchDBNode.COUCHDB_CONFIG_FILE_NAME); }

    private String expandedInstallDir;

    private String getExpandedInstallDir() {
        if (expandedInstallDir == null) throw new IllegalStateException("expandedInstallDir is null; most likely install was not called");
        return expandedInstallDir;
    }

    @Override
    public void install() {
        log.info("Installing {}", entity);

        DownloadResolver resolver = entity.getManagementContext().getEntityDownloadsManager().newDownloader(this);
        List<String> urls = resolver.getTargets();
        String saveAs = resolver.getFilename();
        expandedInstallDir = getInstallDir()+"/" + resolver.getUnpackedDirectoryName(format("apache-couchdb-%s", getVersion()));

        MutableMap<String, String> installGccPackageFlags = MutableMap.of(
                "onlyifmissing", "gcc",
                "yum", "gcc",
                "apt", "gcc",
                "port", null);
        MutableMap<String, String> installMakePackageFlags = MutableMap.of(
                "onlyifmissing", "make",
                "yum", "make",
                "apt", "make",
                "port", null);

        List<String> cmds = Lists.newArrayList();
        cmds.add(CommonCommands.INSTALL_TAR);
        cmds.add(CommonCommands.installPackage(installGccPackageFlags, "couchdb-prerequisites-gcc"));
        cmds.add(CommonCommands.installPackage(installMakePackageFlags, "couchdb-prerequisites-make"));
        cmds.add(CommonCommands.installPackage("erlang"));
        cmds.addAll(CommonCommands.downloadUrlAs(urls, saveAs));
        cmds.add(format("tar xvzf %s", saveAs));
        cmds.add(format("cd %s", getExpandedInstallDir()));
        cmds.add("mkdir -p dist");
        cmds.add("./configure"+ format(" --prefix=%s/dist", getExpandedInstallDir()));
        cmds.add("make install");

        newScript(INSTALLING)
                .body.append(cmds)
                .header.prepend("set -x")
                .gatherOutput()
                .failOnNonZeroResultCode()
                .execute();
    }

    public Set<Integer> getPortsUsed() {
        Set<Integer> result = Sets.newLinkedHashSet(super.getPortsUsed());
        result.addAll(getPortMap().values());
        return result;
    }

    private Map<String, Integer> getPortMap() {
        return ImmutableMap.<String, Integer>builder()
                .put("httpPort", getHttpPort())
                .build();
    }

    @Override
    public void customize() {
        log.info("Customizing {} (Cluster {})", entity, getClusterName());
        NetworkUtils.checkPortsValid(getPortMap());

        newScript(CUSTOMIZING)
                .body.append(format("cp -R %s/dist/* %s", getExpandedInstallDir(), getRunDir()))
                .failOnNonZeroResultCode()
                .execute();

        // Copy the configuration files across
        String configFileContents = processTemplate(getCouchDBConfigTemplateUrl());
        String destinationConfigFile = String.format("%s/%s", getRunDir(), getCouchDBConfigFileName());
        getMachine().copyTo(new ByteArrayInputStream(configFileContents.getBytes()), destinationConfigFile);
        String uriFileContents = processTemplate(getCouchDBUriTemplateUrl());
        String destinationUriFile = String.format("%s/couch.uri", getRunDir());
        getMachine().copyTo(new ByteArrayInputStream(uriFileContents.getBytes()), destinationUriFile);
    }

    @Override
    public void launch() {
        log.info("Launching  {}", entity);
        newScript(MutableMap.of("usePidFile", false), LAUNCHING)
                .body.append(String.format("nohup ./bin/couchdb -p %s -a %s/%s -o couchdb-console.log -e couchdb-error.log -b &", getPidFile(), getRunDir(), getCouchDBConfigFileName()))
                .failOnNonZeroResultCode()
                .execute();
    }

    public String getPidFile() { return String.format("%s/couchdb.pid", getRunDir()); }

    @Override
    public boolean isRunning() {
        return newScript(MutableMap.of("usePidFile", false), CHECK_RUNNING)
                .body.append(String.format("./bin/couchdb -p %s -s", getPidFile()))
                .execute() == 0;
    }

    @Override
    public void stop() {
        newScript(MutableMap.of("usePidFile", false), STOPPING)
                .body.append(String.format("./bin/couchdb -p %s -k", getPidFile()))
                .failOnNonZeroResultCode()
                .execute();
    }
}
